package com.employee.onboarding.demo.controller;

import com.employee.onboarding.demo.controller.dto.CreateEmployeeRequest;
import com.employee.onboarding.demo.controller.dto.EmployeeOnboardingTransitionRequest;
import com.employee.onboarding.demo.controller.dto.UpdateEmployeeWithOnboardingRequest;
import com.employee.onboarding.demo.entity.Employee;
import com.employee.onboarding.demo.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(
            summary = "Create a new employee and start the onboarding process",
            description = "Creates a new employee based on the provided input and initializes their onboarding state machine."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee created and onboarding started"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/create")
    public ResponseEntity<String> createAndStartOnboarding(@Valid @RequestBody CreateEmployeeRequest request) throws Exception {
        Employee employee = employeeService.createEmployee(request);
        return ResponseEntity.ok("Employee created and onboarding started. ID: " + employee.getId());
    }

    @Operation(
            summary = "Trigger onboarding state transition",
            description = "Triggers a transition in the onboarding process for a specific employee using the provided event."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Onboarding event processed successfully"),
            @ApiResponse(responseCode = "409", description = "Invalid or duplicate event for current state")
    })
    @PostMapping("/onboarding/events")
    public ResponseEntity<String> triggerOnboardingTransition(
            @RequestBody EmployeeOnboardingTransitionRequest request) throws Exception {

        boolean success = employeeService.employeeOnboardingTransitionRequest(request);
        if (success) {
            return ResponseEntity.ok("Event '" + request.getOnboardingEvents() + "' processed for employee ID: " + request.getId());
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Could not process event '" + request.getOnboardingEvents() + "' for employee ID: " + request.getId());
        }
    }

    @PatchMapping("/update-partial")
    @Operation(summary = "Partially update employee and onboarding info in one transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "404", description = "Employee or onboarding record not found"),
            @ApiResponse(responseCode = "500", description = "Update failed")
    })
    public ResponseEntity<String> updateEmployeeWithOnboarding(
            @RequestBody UpdateEmployeeWithOnboardingRequest request) {

        try {
            employeeService.patchEmployeeAndOnboarding(request);
            return ResponseEntity.ok("Employee and onboarding info updated.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update." + e.getMessage());
        }
    }

    @PostMapping(value = "/bulk-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload CSV file to create and onboard employees in parallel")
    public ResponseEntity<String> bulkUploadFromCsv(@RequestParam("file") MultipartFile file) {
        employeeService.bulkUploadFromCsv(file);
        return ResponseEntity.ok("Upload and onboarding initiated.");
    }
}