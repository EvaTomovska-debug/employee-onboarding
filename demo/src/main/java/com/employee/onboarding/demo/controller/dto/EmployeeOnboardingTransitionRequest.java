package com.employee.onboarding.demo.controller.dto;

import com.employee.onboarding.demo.state.OnboardingEvents;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmployeeOnboardingTransitionRequest {

    private Long id;
    private OnboardingEvents onboardingEvents;

}
