CREATE TABLE onboarding_process (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    employee_id BIGINT NOT NULL,
                                    state VARCHAR(50) NOT NULL,
                                    state_changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    notes VARCHAR(500),
                                    FOREIGN KEY (employee_id) REFERENCES employee(id)
);