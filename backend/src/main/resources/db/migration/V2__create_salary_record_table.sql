CREATE TABLE salary_record (
    id              BIGSERIAL PRIMARY KEY,
    employee_id     BIGINT       NOT NULL REFERENCES employee (id) ON DELETE CASCADE,
    amount          NUMERIC(14, 2) NOT NULL,
    currency        VARCHAR(3)   NOT NULL,
    effective_from  DATE         NOT NULL,
    effective_to    DATE,
    reason          VARCHAR(20)  NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_salary_record_employee_effective ON salary_record (employee_id, effective_from);
CREATE INDEX idx_salary_record_current ON salary_record (employee_id) WHERE effective_to IS NULL;
