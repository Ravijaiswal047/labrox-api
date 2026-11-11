-- Flyway Migration V3: Sample Logistics, Phlebotomy, and Accessioning
CREATE SCHEMA IF NOT EXISTS sample_schema;

CREATE TABLE IF NOT EXISTS sample_schema.sample_rejection_reason (
    id SERIAL PRIMARY KEY,
    reason_code VARCHAR(30) NOT NULL UNIQUE,
    reason_description VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS sample_schema.sample_collection (
    id BIGSERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    barcode_no VARCHAR(64) NOT NULL,
    order_id VARCHAR(64) NOT NULL,
    patient_id INT NOT NULL,
    patient_name VARCHAR(150),
    sample_type_id INT NOT NULL,
    sample_type_name VARCHAR(100) NOT NULL,
    tube_color VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'COLLECTED', -- COLLECTED, RECEIVED, REJECTED, IN_TRANSIT
    collection_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    collected_by INT,
    received_time TIMESTAMP WITH TIME ZONE,
    received_by INT,
    rejection_reason_id INT REFERENCES sample_schema.sample_rejection_reason(id),
    rejection_remarks TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_sample_barcode ON sample_schema.sample_collection(company_id, barcode_no);
CREATE INDEX IF NOT EXISTS idx_sample_order ON sample_schema.sample_collection(company_id, order_id);
CREATE INDEX IF NOT EXISTS idx_sample_status ON sample_schema.sample_collection(company_id, status);

-- Seed standard clinical rejection reasons
INSERT INTO sample_schema.sample_rejection_reason (reason_code, reason_description) VALUES
('HEMOLYZED', 'Grossly Hemolyzed specimen - redraw required'),
('CLOTTED', 'Specimen clotted in EDTA/Citrate tube'),
('INSUFFICIENT_QTY', 'Quantity not sufficient (QNS) for analysis'),
('LIPEMIC', 'Severely Lipemic / Turbid serum'),
('WRONG_CONTAINER', 'Specimen collected in incorrect tube/container'),
('UNLABELLED', 'Specimen tube unlabelled or mismatched UHID')
ON CONFLICT (reason_code) DO NOTHING;
