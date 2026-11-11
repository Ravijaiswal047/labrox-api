-- Flyway Migration V4: Clinical Results, Observations, and Delta Check Trail
CREATE SCHEMA IF NOT EXISTS diagnostic_schema;

CREATE TABLE IF NOT EXISTS diagnostic_schema.patient_labinvestigation_opd (
    test_id BIGSERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    barcode_no VARCHAR(64) NOT NULL,
    order_id VARCHAR(64) NOT NULL,
    patient_id INT NOT NULL,
    patient_name VARCHAR(150),
    patient_age_days INT NOT NULL,
    patient_gender VARCHAR(20) NOT NULL,
    investigation_id INT NOT NULL,
    test_code VARCHAR(50) NOT NULL,
    test_name VARCHAR(200) NOT NULL,
    department_id INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'SAMPLE_RECEIVED', -- SAMPLE_RECEIVED, RESULT_ENTERED, TEST_APPROVED
    is_critical BOOLEAN NOT NULL DEFAULT FALSE,
    result_date TIMESTAMP WITH TIME ZONE,
    result_entered_by INT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS diagnostic_schema.patient_labobservation_opd (
    id BIGSERIAL PRIMARY KEY,
    test_id BIGINT NOT NULL REFERENCES diagnostic_schema.patient_labinvestigation_opd(test_id) ON DELETE CASCADE,
    observation_id INT NOT NULL,
    observation_name VARCHAR(150) NOT NULL,
    result_value VARCHAR(100),
    numeric_value NUMERIC(14, 4),
    old_value NUMERIC(14, 4),
    delta_percentage NUMERIC(6, 2),
    delta_flag VARCHAR(20) DEFAULT 'OK', -- OK, DELTA_WARNING
    unit VARCHAR(50),
    min_reading NUMERIC(12, 4),
    max_reading NUMERIC(12, 4),
    min_critical NUMERIC(12, 4),
    max_critical NUMERIC(12, 4),
    display_reading VARCHAR(100),
    flag VARCHAR(20) DEFAULT 'NORMAL', -- NORMAL, LOW, HIGH, CRITICAL_LOW, CRITICAL_HIGH
    is_calculated BOOLEAN NOT NULL DEFAULT FALSE,
    comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_investigation_barcode ON diagnostic_schema.patient_labinvestigation_opd(company_id, barcode_no);
CREATE INDEX IF NOT EXISTS idx_investigation_patient ON diagnostic_schema.patient_labinvestigation_opd(company_id, patient_id);
CREATE INDEX IF NOT EXISTS idx_investigation_status ON diagnostic_schema.patient_labinvestigation_opd(company_id, status);
CREATE INDEX IF NOT EXISTS idx_observation_test ON diagnostic_schema.patient_labobservation_opd(test_id);
CREATE INDEX IF NOT EXISTS idx_observation_id ON diagnostic_schema.patient_labobservation_opd(observation_id);
