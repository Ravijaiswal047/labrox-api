-- Flyway Migration V6: Workflow Approvals, 21 CFR Part 11 Electronic Signatures, and GxP Amendments
CREATE SCHEMA IF NOT EXISTS approval_schema;

CREATE TABLE IF NOT EXISTS approval_schema.electronic_signature (
    id BIGSERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    test_id BIGINT NOT NULL,
    signer_id INT NOT NULL,
    signer_name VARCHAR(100) NOT NULL,
    registration_number VARCHAR(60) NOT NULL,
    designation VARCHAR(100) NOT NULL,
    signature_meaning VARCHAR(255) NOT NULL,
    signed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    signature_hash VARCHAR(64) NOT NULL,
    is_valid BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS approval_schema.test_approval_log (
    id BIGSERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    test_id BIGINT NOT NULL,
    action VARCHAR(30) NOT NULL, -- PROVISIONAL_APPROVE, FINAL_APPROVE, AUTO_APPROVE, HOLD, RERUN, AMEND
    approver_id INT NOT NULL,
    approver_name VARCHAR(100) NOT NULL,
    approver_role VARCHAR(50) NOT NULL, -- RESIDENT, PATHOLOGIST, SYSTEM
    signature_hash VARCHAR(64),
    comments TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS approval_schema.amendment_history (
    id BIGSERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    test_id BIGINT NOT NULL,
    observation_id INT NOT NULL,
    observation_name VARCHAR(150) NOT NULL,
    original_value VARCHAR(100) NOT NULL,
    amended_value VARCHAR(100) NOT NULL,
    reason_for_change TEXT NOT NULL,
    amended_by INT NOT NULL,
    amended_by_name VARCHAR(100) NOT NULL,
    amended_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    superseded_signature_id BIGINT REFERENCES approval_schema.electronic_signature(id)
);

CREATE TABLE IF NOT EXISTS approval_schema.panic_call_log (
    id BIGSERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    test_id BIGINT NOT NULL,
    patient_id INT NOT NULL,
    observation_name VARCHAR(150) NOT NULL,
    panic_value VARCHAR(50) NOT NULL,
    called_person_name VARCHAR(100) NOT NULL,
    called_person_designation VARCHAR(100) NOT NULL,
    department_or_ward VARCHAR(100) NOT NULL,
    call_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    caller_id INT NOT NULL,
    call_status VARCHAR(30) NOT NULL DEFAULT 'INFORMED',
    remarks TEXT
);

CREATE TABLE IF NOT EXISTS approval_schema.auto_approval_rule (
    id SERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    department_id INT,
    investigation_id INT,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    allow_delta_warning BOOLEAN NOT NULL DEFAULT FALSE,
    allow_panic_alarm BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_approval_test ON approval_schema.test_approval_log(company_id, test_id);
CREATE INDEX IF NOT EXISTS idx_signature_test ON approval_schema.electronic_signature(company_id, test_id);
CREATE INDEX IF NOT EXISTS idx_amendment_test ON approval_schema.amendment_history(company_id, test_id);
CREATE INDEX IF NOT EXISTS idx_panic_test ON approval_schema.panic_call_log(company_id, test_id);
