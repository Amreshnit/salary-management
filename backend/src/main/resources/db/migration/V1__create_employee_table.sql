CREATE TABLE employee (
    id              BIGSERIAL PRIMARY KEY,
    employee_code   VARCHAR(20)  NOT NULL UNIQUE,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    department      VARCHAR(100) NOT NULL,
    job_title       VARCHAR(100) NOT NULL,
    seniority_level VARCHAR(50)  NOT NULL,
    country         VARCHAR(100) NOT NULL,
    currency        VARCHAR(3)   NOT NULL,
    hire_date       DATE         NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_employee_department ON employee (department);
CREATE INDEX idx_employee_country ON employee (country);
CREATE INDEX idx_employee_status ON employee (status);
CREATE INDEX idx_employee_name ON employee (last_name, first_name);
