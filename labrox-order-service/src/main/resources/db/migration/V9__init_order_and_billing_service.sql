-- Flyway Migration V9: Patient Registration, WorkOrder Booking and Cashier POS Billing
CREATE SCHEMA IF NOT EXISTS order_schema;

CREATE TABLE IF NOT EXISTS order_schema.doctor_master (
    doctor_id SERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    doctor_code VARCHAR(50) NOT NULL,
    doctor_name VARCHAR(150) NOT NULL,
    specialization VARCHAR(100),
    mobile VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS order_schema.f_ratetype_master (
    rate_type_id SERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    rate_type_name VARCHAR(100) NOT NULL,
    is_credit BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS order_schema.patient_master (
    patient_id BIGSERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    uhid VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(20),
    p_name VARCHAR(150) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    dob DATE,
    age_year INT DEFAULT 0,
    age_month INT DEFAULT 0,
    age_days INT DEFAULT 0,
    total_age_in_days INT NOT NULL,
    mobile VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(20),
    is_vip BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS order_schema.f_ledgertransaction (
    ledger_transaction_id BIGSERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    ledger_transaction_no VARCHAR(64) NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL REFERENCES order_schema.patient_master(patient_id),
    centre_id INT NOT NULL,
    rate_type_id INT NOT NULL REFERENCES order_schema.f_ratetype_master(rate_type_id),
    doctor_id INT REFERENCES order_schema.doctor_master(doctor_id),
    doctor_name VARCHAR(150),
    gross_amount NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    discount_amount NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    discount_reason VARCHAR(255),
    discount_approved_by INT,
    net_amount NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    adjustment NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    balance_amount NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    is_credit BOOLEAN DEFAULT FALSE,
    status VARCHAR(30) NOT NULL DEFAULT 'REGISTERED',
    created_by INT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS order_schema.f_reciept (
    receipt_id BIGSERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    receipt_no VARCHAR(64) NOT NULL UNIQUE,
    ledger_transaction_no VARCHAR(64) NOT NULL,
    patient_id BIGINT NOT NULL,
    amount_paid NUMERIC(12, 2) NOT NULL,
    payment_mode VARCHAR(50) NOT NULL,
    bank_name VARCHAR(100),
    card_no VARCHAR(20),
    transaction_no VARCHAR(100),
    centre_id INT NOT NULL,
    created_by INT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_patient_uhid ON order_schema.patient_master(company_id, uhid);
CREATE INDEX IF NOT EXISTS idx_patient_mobile ON order_schema.patient_master(company_id, mobile);
CREATE INDEX IF NOT EXISTS idx_lt_no ON order_schema.f_ledgertransaction(company_id, ledger_transaction_no);
CREATE INDEX IF NOT EXISTS idx_lt_patient ON order_schema.f_ledgertransaction(company_id, patient_id);
CREATE INDEX IF NOT EXISTS idx_rc_no ON order_schema.f_reciept(company_id, receipt_no);

-- Seed Initial Doctors and Rate Types
INSERT INTO order_schema.doctor_master (doctor_id, company_id, doctor_code, doctor_name, specialization, is_active) VALUES
(1, 1, 'DOC001', 'Self / Walk-in Patient', 'General Medicine', TRUE),
(2, 1, 'DOC002', 'Dr. Rajesh Sharma, MD (Pathology)', 'Clinical Pathology', TRUE),
(3, 1, 'DOC003', 'Dr. Ananya Roy, MS (Surgery)', 'General Surgery', TRUE)
ON CONFLICT (doctor_id) DO NOTHING;

INSERT INTO order_schema.f_ratetype_master (rate_type_id, company_id, rate_type_name, is_credit, is_active) VALUES
(1, 1, 'Standard Cash Rate', FALSE, TRUE),
(2, 1, 'Corporate Panel Credit', TRUE, TRUE),
(3, 1, 'VIP / Senior Citizen 15% Disc', FALSE, TRUE)
ON CONFLICT (rate_type_id) DO NOTHING;
