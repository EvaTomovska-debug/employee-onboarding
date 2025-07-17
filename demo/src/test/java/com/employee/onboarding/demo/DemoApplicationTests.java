package com.employee.onboarding.demo;

import com.employee.onboarding.demo.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
@TestConfiguration
class DemoApplicationTests {

	@Bean
	public EmployeeService employeeService() {
		return Mockito.mock(EmployeeService.class);
	}

	@Test
	void contextLoads() {
	}

}
