package com.employee.onboarding.demo.service;

import com.employee.onboarding.demo.controller.dto.*;
import com.employee.onboarding.demo.entity.*;
import com.employee.onboarding.demo.repository.EmployeeRepository;
import com.employee.onboarding.demo.state.OnboardingEvents;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.employee.onboarding.demo.controller.dto.EmployeeType.safeFromString;

@Service
public class EmployeeService {

    private final OnboardingService onboardingService;
    private final EmployeeRepository employeeRepository;

    private final Map<EmployeeType, EmployeeCreator> employeeCreators = Map.of(
            EmployeeType.SOFTWARE, request -> new SoftwareDeveloperBuilder()
                    .withSkills(request.getSkills())
                    .withProgramingLanguage(request.getProgrammingLanguage())
                    .withCommonFields(request)
                    .build(),
            EmployeeType.PLC, request -> new PlcDeveloperBuilder()
                    .withPlcCertification(request.getPlcCertification())
                    .withCommonFields(request)
                    .build()
    );

    public EmployeeService(OnboardingService onboardingService,
                           EmployeeRepository employeeRepository) {
        this.onboardingService = onboardingService;
        this.employeeRepository = employeeRepository;
    }

    public Employee createEmployee(CreateEmployeeRequest request) throws Exception {
        Employee employee = Optional.ofNullable(employeeCreators.get(request.getType()))
                .map(creator -> creator.create(request))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported employee type: " + request.getType()));

        employeeRepository.save(employee);
        onboardingService.startOnboarding(employee);
        return employee;
    }

    public boolean employeeOnboardingTransitionRequest(EmployeeOnboardingTransitionRequest employeeOnboardingTransitionRequest) throws Exception {
      Optional<Employee> employee = employeeRepository.findById(employeeOnboardingTransitionRequest.getId());
      return onboardingService.employeeOnboardingTransitionRequest(employee.get(), employeeOnboardingTransitionRequest.getOnboardingEvents());
    }
    @Transactional
    public void patchEmployeeAndOnboarding(UpdateEmployeeWithOnboardingRequest request) {
        employeeRepository.findById(request.getEmployeeId())
                .map(employee -> {
                    patchFields(employee, request);
                    return employeeRepository.save(employee);
                })
                .ifPresentOrElse(
                        updated -> {
                            boolean transitionOk = onboardingService.employeeOnboardingTransitionRequest(updated, request.getState());
                            if (!transitionOk) {
                                throw new IllegalStateException("Onboarding state transition failed. Rolling back transaction.");
                            }
                        },
                        () -> { throw new EntityNotFoundException("Employee not found"); }
                );
    }

    private void patchFields(Employee employee, UpdateEmployeeWithOnboardingRequest request) {
        Optional.ofNullable(request.getEmail()).ifPresent(employee::setEmail);
        Optional.ofNullable(request.getLastName()).ifPresent(employee::setLastName);
        Optional.ofNullable(request.getAddress()).ifPresent(employee::setAddress);
    }

    public void bulkUploadFromCsv(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<CreateEmployeeRequest> requests = reader.lines()
                    .skip(1) // skip header
                    .map(this::parseCsvLine)
                    .toList();

            bulkCreateAndStartOnboarding(requests);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read CSV file", e);
        }
    }

    private CreateEmployeeRequest parseCsvLine(String line) {
        String[] parts = line.split(",", -1);

        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setType(safeFromString(parts[0]).isPresent() ? safeFromString(parts[0].trim()).get() : EmployeeType.NOT_FOUND);
        request.setEmail(parts[1].trim());
        request.setLastName(parts[2].trim());
        request.setAddress(parts[3].trim());

        if (!parts[4].isBlank()) {
            request.setSkills(Arrays.stream(parts[4].split(";")).map(String::trim).toList());
        }

        request.setPlcCertification(parts[5].trim());
        return request;
    }

    public void bulkCreateAndStartOnboarding(List<CreateEmployeeRequest> requests) {
        List<CompletableFuture<Void>> futures = requests.stream()
                .map(request -> CompletableFuture.runAsync(() -> {
                    try {
                        Employee employee = createEmployee(request);
                        List<OnboardingEvents> events = List.of(
                                OnboardingEvents.VERIFY_DOCUMENTS_DONE,
                                OnboardingEvents.SETUP_SOFTWARE_DONE,
                                OnboardingEvents.SAFETY_CHECK_DONE,
                                OnboardingEvents.COMPLETE_ONBOARDING
                        );
                        for (OnboardingEvents event : events) {
                            EmployeeOnboardingTransitionRequest e  = new EmployeeOnboardingTransitionRequest();
                            e.setId(employee.getId());
                            e.setOnboardingEvents(event);
                            boolean success = employeeOnboardingTransitionRequest(e);
                            if (!success) {
                                throw new IllegalStateException("Failed to process event: " + event + " for employee: " + employee.getId());
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to onboard employee: " + request.getEmail() + " - " + e.getMessage());
                    }
                }))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}

