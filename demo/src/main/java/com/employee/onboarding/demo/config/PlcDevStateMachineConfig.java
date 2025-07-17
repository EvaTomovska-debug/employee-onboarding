package com.employee.onboarding.demo.config;

import com.employee.onboarding.demo.state.OnboardingEvents;
import com.employee.onboarding.demo.state.OnboardingStates;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(name = "plcDevStateMachineFactory")
public class PlcDevStateMachineConfig extends EnumStateMachineConfigurerAdapter<OnboardingStates, OnboardingEvents> {


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
                .withExternal().source(OnboardingStates.VERIFY_DOCUMENTS).target(OnboardingStates.SETUP_PLC).event(OnboardingEvents.SETUP_PLC_DONE)
                .and()
                .withExternal().source(OnboardingStates.SETUP_PLC).target(OnboardingStates.SAFETY_CHECK).event(OnboardingEvents.SAFETY_CHECK_DONE)
                .and()
                .withExternal().source(OnboardingStates.SAFETY_CHECK).target(OnboardingStates.COMPLETED).event(OnboardingEvents.COMPLETE_ONBOARDING);
    }
}

