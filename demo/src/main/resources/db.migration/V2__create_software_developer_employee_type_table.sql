CREATE TABLE software_developer (
                                    id BIGINT PRIMARY KEY,
                                    programming_language VARCHAR(100),
                                    FOREIGN KEY (id) REFERENCES employee(id)
);