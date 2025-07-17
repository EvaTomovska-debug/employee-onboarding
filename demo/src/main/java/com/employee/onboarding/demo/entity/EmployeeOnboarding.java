package com.employee.onboarding.demo.entity;

import com.employee.onboarding.demo.state.OnboardingStates;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "onboarding_process")
public class EmployeeOnboarding {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private OnboardingStates state;

    private LocalDateTime stateChangedAt;

    private String notes;

}


