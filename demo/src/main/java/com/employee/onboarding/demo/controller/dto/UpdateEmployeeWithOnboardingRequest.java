package com.employee.onboarding.demo.controller.dto;

import com.employee.onboarding.demo.state.OnboardingEvents;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class UpdateEmployeeWithOnboardingRequest {

    // Employee
    private Long employeeId;
    private String type;
    private String email;
    private String lastName;
    private String address;
    private List<String> skills;
    private String plcCertification;

    // Onboarding
    private OnboardingEvents state;
    private LocalDateTime stateChangedAt;
    private String notes;

}
