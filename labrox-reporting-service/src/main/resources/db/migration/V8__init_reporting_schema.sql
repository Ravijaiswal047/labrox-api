-- Flyway Migration V8: Reporting, Document Archive, and Digital Report Verification
CREATE SCHEMA IF NOT EXISTS reporting_schema;

CREATE TABLE IF NOT EXISTS reporting_schema.report_template_header (
    id SERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    centre_id INT NOT NULL,
    centre_name VARCHAR(150) NOT NULL,
    tagline VARCHAR(200),
    address TEXT NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(100),
    nabl_accreditation_no VARCHAR(60),
    logo_url VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_report_centre UNIQUE (company_id, centre_id)
);

CREATE TABLE IF NOT EXISTS reporting_schema.generated_report_archive (
    id BIGSERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    test_id BIGINT NOT NULL,
    patient_id INT NOT NULL,
    patient_name VARCHAR(150),
    report_type VARCHAR(30) NOT NULL, -- DIAGNOSTIC_PDF, EXCEL_MIS
    file_name VARCHAR(200) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    verification_token VARCHAR(64) NOT NULL UNIQUE,
    pdf_storage_path VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_report_test ON reporting_schema.generated_report_archive(company_id, test_id);
CREATE INDEX IF NOT EXISTS idx_report_token ON reporting_schema.generated_report_archive(verification_token);

-- Seed default center letterhead template
INSERT INTO reporting_schema.report_template_header (
    company_id, centre_id, centre_name, tagline, address, phone, email, nabl_accreditation_no
) VALUES (
    1, 1, 'LabRox Advanced Diagnostics & Research Institute',
    'Center of Excellence in Clinical Pathology, Genomics & Precision Medicine',
    'Plot 42, Healthcare City, Tech Corridor, Bangalore - 560100',
    '+91-80-4920-1000', 'reports@labrox.com', 'NABL-MC-2849'
) ON CONFLICT (company_id, centre_id) DO NOTHING;
