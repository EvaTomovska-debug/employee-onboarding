package com.employee.onboarding.demo.service;

import com.employee.onboarding.demo.config.RedisStateMachinePersistent;
import com.employee.onboarding.demo.entity.Employee;
import com.employee.onboarding.demo.entity.EmployeeOnboarding;
import com.employee.onboarding.demo.entity.PlcDeveloper;
import com.employee.onboarding.demo.entity.SoftwareDeveloper;
import com.employee.onboarding.demo.repository.EmployeeOnboardingRepository;
import com.employee.onboarding.demo.state.OnboardingEvents;
import com.employee.onboarding.demo.state.OnboardingStates;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OnboardingService {

    @Qualifier("softwareDevStateMachineFactory")
    private final StateMachineFactory<OnboardingStates, OnboardingEvents> softwareDevFactory;

    @Qualifier("plcDevStateMachineFactory")
    private final StateMachineFactory<OnboardingStates, OnboardingEvents> plcDevFactory;

    private final EmployeeOnboardingRepository onboardingRepository;

    private final RedisStateMachinePersistent redisPersistent;

    public OnboardingService(
            EmployeeOnboardingRepository onboardingRepository,
            @Qualifier("softwareDevStateMachineFactory")
            StateMachineFactory<OnboardingStates, OnboardingEvents> softwareDevFactory,
            @Qualifier("plcDevStateMachineFactory")
            StateMachineFactory<OnboardingStates, OnboardingEvents> plcDevFactory,
            RedisStateMachinePersistent redisPersistent
    ) {
        this.onboardingRepository = onboardingRepository;
        this.softwareDevFactory = softwareDevFactory;
        this.plcDevFactory = plcDevFactory;
        this.redisPersistent = redisPersistent;
    }

    public boolean employeeOnboardingTransitionRequest(Employee employee, OnboardingEvents onboardingEvent) {
        StateMachine<OnboardingStates, OnboardingEvents> stateMachine = getStateMachine(employee);

        try {
            redisPersistent.restore(stateMachine, employee.getId());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to restore state machine from Redis", e);
        }

        if (isTransitionSuccess(onboardingEvent, stateMachine)) return false;

        OnboardingStates newState = stateMachine.getState().getId();
        onboardingRepository.save(
                EmployeeOnboarding.builder()
                        .employee(employee)
                        .state(newState)
                        .stateChangedAt(LocalDateTime.now())
                        .notes("Transitioned to state: " + newState)
                        .build()
        );

        try {
            redisPersistent.persist(stateMachine, employee.getId());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to persist state machine to Redis", e);
        }

        return true;
    }

    private static boolean isTransitionSuccess(OnboardingEvents onboardingEvent, StateMachine<OnboardingStates, OnboardingEvents> stateMachine) {
        return !stateMachine.sendEvent(onboardingEvent);
    }

    public void startOnboarding(Employee employee) throws Exception {
        StateMachine<OnboardingStates, OnboardingEvents> sm = getStateMachine(employee);
        sm.startReactively().block();

        EmployeeOnboarding onboarding = EmployeeOnboarding.builder()
                .employee(employee)
                .state(OnboardingStates.START)
                .stateChangedAt(LocalDateTime.now())
                .notes("Initial onboarding started.")
                .build();
        onboardingRepository.save(onboarding);
        redisPersistent.persist(sm, employee.getId());
    }

    private StateMachine<OnboardingStates, OnboardingEvents> getStateMachine(Employee employee) {

        String machineId = String.valueOf(employee.getId());
        if (employee instanceof SoftwareDeveloper) {
            return softwareDevFactory.getStateMachine(machineId);

        } else if (employee instanceof PlcDeveloper) {
            return plcDevFactory.getStateMachine(machineId);
        }
        throw new IllegalStateException("Unknown employee type");
    }
}

