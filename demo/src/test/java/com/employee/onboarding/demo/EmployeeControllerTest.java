package com.employee.onboarding.demo;

import com.employee.onboarding.demo.controller.EmployeeController;
import com.employee.onboarding.demo.controller.dto.CreateEmployeeRequest;
import com.employee.onboarding.demo.controller.dto.EmployeeOnboardingTransitionRequest;
import com.employee.onboarding.demo.controller.dto.EmployeeType;
import com.employee.onboarding.demo.entity.Employee;
import com.employee.onboarding.demo.entity.SoftwareDeveloper;
import com.employee.onboarding.demo.service.EmployeeService;
import com.employee.onboarding.demo.state.OnboardingEvents;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(DemoApplicationTests.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAndStartOnboarding() throws Exception {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setType(EmployeeType.SOFTWARE);
        request.setEmail("eva.tomovska@gmail.com");
        request.setLastName("Eva");
        request.setAddress("123 Java St");
        request.setSkills(List.of("Java", "Spring Boot"));

        Employee mockEmployee = new SoftwareDeveloper();
        mockEmployee.setId(42L);

        Mockito.when(employeeService.createEmployee(any())).thenReturn(mockEmployee);

        mockMvc.perform(post("/api/employees/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee created and onboarding started. ID: 42"));
    }

    @Test
    void shouldProcessOnboardingEventSuccessfully() throws Exception {
        EmployeeOnboardingTransitionRequest request = new EmployeeOnboardingTransitionRequest();
        request.setId(1L);
        request.setOnboardingEvents(OnboardingEvents.VERIFY_DOCUMENTS_DONE);

        Mockito.when(employeeService.employeeOnboardingTransitionRequest(Mockito.any()))
                .thenReturn(true);

        mockMvc.perform(post("/api/employees/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Event 'VERIFY_DOCUMENTS_DONE' processed")));
}
}
