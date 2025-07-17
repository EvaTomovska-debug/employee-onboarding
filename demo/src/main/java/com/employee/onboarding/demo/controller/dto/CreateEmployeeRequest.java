package com.employee.onboarding.demo.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CreateEmployeeRequest {

   // @NotNull(message = "Employee ID must not be null")
    private Long employeeId;

    @NotNull(message = "Employee type is required")
    private EmployeeType type;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Address is required")
    private String address;

    @Size(min = 1, message = "At least one skill must be provided")
    private List<@NotBlank(message = "Skill cannot be blank") String> skills;

    private String plcCertification;

    private String programmingLanguage;

}
