package com.employee.onboarding.demo.repository;

import com.employee.onboarding.demo.entity.Employee;
import com.employee.onboarding.demo.entity.EmployeeOnboarding;
import com.employee.onboarding.demo.state.OnboardingStates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeOnboardingRepository extends JpaRepository<EmployeeOnboarding, Long> {

    Optional<EmployeeOnboarding> findTopByEmployeeOrderByStateChangedAtDesc(Employee employee);

    List<EmployeeOnboarding> findByEmployee(Employee employee);

    List<EmployeeOnboarding> findByState(OnboardingStates state);
}
