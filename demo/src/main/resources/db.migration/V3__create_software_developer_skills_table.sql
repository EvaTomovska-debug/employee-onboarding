CREATE TABLE software_dev_skills (
                                     software_developer_id BIGINT,
                                     skill VARCHAR(100),
                                     PRIMARY KEY (software_developer_id, skill),
                                     FOREIGN KEY (software_developer_id) REFERENCES software_developer(id)
);