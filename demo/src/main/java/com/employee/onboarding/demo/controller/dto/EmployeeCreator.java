package com.employee.onboarding.demo.controller.dto;

import com.employee.onboarding.demo.entity.Employee;

@FunctionalInterface
public interface EmployeeCreator {
    Employee create(CreateEmployeeRequest request);
}