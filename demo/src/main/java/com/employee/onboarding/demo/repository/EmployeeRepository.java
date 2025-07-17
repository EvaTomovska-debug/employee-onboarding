package com.employee.onboarding.demo.repository;

import com.employee.onboarding.demo.entity.Employee;
import com.employee.onboarding.demo.entity.EmployeeOnboarding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}