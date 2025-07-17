package com.employee.onboarding.demo.config;

import com.employee.onboarding.demo.entity.EmployeeOnboarding;
import com.employee.onboarding.demo.repository.EmployeeOnboardingRepository;
import com.employee.onboarding.demo.state.OnboardingEvents;
import com.employee.onboarding.demo.state.OnboardingStates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.time.LocalDateTime;
import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name = "softwareDevStateMachineFactory")
public class SoftwareDevStateMachineConfig extends EnumStateMachineConfigurerAdapter<OnboardingStates, OnboardingEvents> {

    @Override
    public void configure(StateMachineStateConfigurer<OnboardingStates, OnboardingEvents> states) throws Exception {
        states.withStates()
                .initial(OnboardingStates.START)
                .states(EnumSet.allOf(OnboardingStates.class))
                .end(OnboardingStates.COMPLETED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OnboardingStates, OnboardingEvents> transitions) throws Exception {
        transitions
                .withExternal().source(OnboardingStates.START).target(OnboardingStates.VERIFY_DOCUMENTS).event(OnboardingEvents.VERIFY_DOCUMENTS_DONE)
                .and()
                .withExternal().source(OnboardingStates.VERIFY_DOCUMENTS).target(OnboardingStates.SETUP_SOFTWARE).event(OnboardingEvents.SETUP_SOFTWARE_DONE)
                .and()
                .withExternal().source(OnboardingStates.SETUP_SOFTWARE).target(OnboardingStates.SAFETY_CHECK).event(OnboardingEvents.SAFETY_CHECK_DONE)
                .and()
                .withExternal().source(OnboardingStates.SAFETY_CHECK).target(OnboardingStates.COMPLETED).event(OnboardingEvents.COMPLETE_ONBOARDING);
    }
}
