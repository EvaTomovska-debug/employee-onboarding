package com.employee.onboarding.demo.controller.dto;

import java.util.Optional;

public enum EmployeeType {
    SOFTWARE,
    PLC,
    NOT_FOUND;

    public static Optional<EmployeeType> safeFromString(String type) {
        try {
            return Optional.of(EmployeeType.SOFTWARE);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }


}
