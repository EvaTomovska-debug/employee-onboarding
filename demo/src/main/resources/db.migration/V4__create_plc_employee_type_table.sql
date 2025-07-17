CREATE TABLE plc_developer (
                               id BIGINT PRIMARY KEY,
                               plc_tool VARCHAR(100),
                               certification_level VARCHAR(100),
                               FOREIGN KEY (id) REFERENCES employee(id)
);