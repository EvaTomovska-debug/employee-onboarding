package com.employee.onboarding.demo.fegin;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "", url = "", configuration = EmployeeOnboardingApprovalClientConfig.class)
public interface EmployeeOnboardingApprovalClient {


}
