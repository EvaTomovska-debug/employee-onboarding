package com.employee.onboarding.demo.config;

import com.employee.onboarding.demo.state.OnboardingEvents;
import com.employee.onboarding.demo.state.OnboardingStates;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;

@Component
@RequiredArgsConstructor
public class RedisStateMachinePersistent implements StateMachinePersister<OnboardingStates, OnboardingEvents, Long> {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "onboarding:sm:";

    @Override
    public void persist(StateMachine<OnboardingStates, OnboardingEvents> stateMachine, Long employeeId) throws Exception {
        String state = stateMachine.getState().getId().name();
        redisTemplate.opsForValue().set(PREFIX + employeeId, state);
    }


    @Override
    public StateMachine<OnboardingStates, OnboardingEvents> restore(StateMachine<OnboardingStates, OnboardingEvents> stateMachine, Long employeeId) throws Exception {
        String stateStr = redisTemplate.opsForValue().get(PREFIX + employeeId);

        if (stateStr != null) {
            OnboardingStates restoredState = OnboardingStates.valueOf(stateStr);
            stateMachine.stop();
            stateMachine.getStateMachineAccessor().doWithAllRegions(access -> {
                access.resetStateMachine(new DefaultStateMachineContext<>(restoredState, null, null, null));
            });
            stateMachine.start();
        }

        return stateMachine;
    }
}

