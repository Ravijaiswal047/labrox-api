-- Flyway Migration V5: Quality Control, Westgard Rules, and Levey-Jennings
CREATE SCHEMA IF NOT EXISTS qc_schema;

CREATE TABLE IF NOT EXISTS qc_schema.qc_control_lot (
    id SERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    control_name VARCHAR(100) NOT NULL,
    lot_number VARCHAR(60) NOT NULL,
    level INT NOT NULL, -- 1=Low, 2=Normal, 3=High
    expiry_date DATE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_qc_lot UNIQUE (company_id, lot_number)
);

CREATE TABLE IF NOT EXISTS qc_schema.qc_assay_target (
    id SERIAL PRIMARY KEY,
    lot_id INT NOT NULL REFERENCES qc_schema.qc_control_lot(id) ON DELETE CASCADE,
    observation_id INT NOT NULL,
    observation_name VARCHAR(100) NOT NULL,
    target_mean NUMERIC(12, 4) NOT NULL,
    target_sd NUMERIC(12, 4) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_qc_assay UNIQUE (lot_id, observation_id)
);

CREATE TABLE IF NOT EXISTS qc_schema.qc_run_data (
    id BIGSERIAL PRIMARY KEY,
    assay_target_id INT NOT NULL REFERENCES qc_schema.qc_assay_target(id) ON DELETE CASCADE,
    run_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    analyzer_id VARCHAR(50) NOT NULL,
    measured_value NUMERIC(12, 4) NOT NULL,
    z_score NUMERIC(6, 3) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_CONTROL', -- IN_CONTROL, WARNING, REJECTED
    violated_rule VARCHAR(50), -- 1_2s, 1_3s, 2_2s, R_4s, 4_1s, 10_x
    lockout_triggered BOOLEAN NOT NULL DEFAULT FALSE,
    corrective_action TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_qc_run_assay ON qc_schema.qc_run_data(assay_target_id, run_time DESC);
CREATE INDEX IF NOT EXISTS idx_qc_run_status ON qc_schema.qc_run_data(status);
