-- =============================================================================
-- LabRox Master Data & Test Catalog Schema Migration
-- Flyway Version: V2
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS catalog_schema;
SET search_path TO catalog_schema, public;

-- 1. Department Master
CREATE TABLE IF NOT EXISTS department_master (
    department_id SERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    department_code VARCHAR(50) NOT NULL,
    department_name VARCHAR(150) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_dept_code UNIQUE (company_id, department_code)
);

-- 2. Sample Type Master
CREATE TABLE IF NOT EXISTS sample_type_master (
    sample_type_id SERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    sample_type_name VARCHAR(100) NOT NULL,
    container_color VARCHAR(50) DEFAULT 'Red',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_sample_type UNIQUE (company_id, sample_type_name)
);

-- 3. Investigation Master (Test Catalog)
CREATE TABLE IF NOT EXISTS investigation_master (
    investigation_id SERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    test_code VARCHAR(50) NOT NULL,
    test_name VARCHAR(200) NOT NULL,
    department_id INT NOT NULL REFERENCES department_master(department_id),
    sample_type_id INT NOT NULL REFERENCES sample_type_master(sample_type_id),
    report_type VARCHAR(50) NOT NULL DEFAULT 'Numeric', -- Numeric, Culture, Histo, Radiology
    is_package BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_test_code UNIQUE (company_id, test_code)
);

-- 4. Analyte / Parameter Observation Master
CREATE TABLE IF NOT EXISTS labobservation_master (
    observation_id SERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    observation_name VARCHAR(150) NOT NULL,
    reading_format VARCHAR(50) NOT NULL DEFAULT 'Numeric', -- Numeric, Text, Heading, Formula
    decimal_places INT NOT NULL DEFAULT 1,
    default_value VARCHAR(255),
    formula TEXT,
    unit VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 5. Test to Observation Mapping
CREATE TABLE IF NOT EXISTS investigation_observation_map (
    id SERIAL PRIMARY KEY,
    investigation_id INT NOT NULL REFERENCES investigation_master(investigation_id),
    observation_id INT NOT NULL REFERENCES labobservation_master(observation_id),
    print_order INT NOT NULL DEFAULT 0,
    CONSTRAINT uk_inv_obs UNIQUE (investigation_id, observation_id)
);

-- 6. Biological Reference Intervals
CREATE TABLE IF NOT EXISTS labobservation_range (
    range_id SERIAL PRIMARY KEY,
    observation_id INT NOT NULL REFERENCES labobservation_master(observation_id),
    gender VARCHAR(10) NOT NULL DEFAULT 'Both', -- Male, Female, Both
    from_age_days INT NOT NULL DEFAULT 0,
    to_age_days INT NOT NULL DEFAULT 36500, -- Up to 100 years in days
    min_reading NUMERIC(12,4),
    max_reading NUMERIC(12,4),
    min_critical NUMERIC(12,4),
    max_critical NUMERIC(12,4),
    interpretation TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 7. Rate Type Master (Tariffs)
CREATE TABLE IF NOT EXISTS rate_type_master (
    rate_type_id SERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    rate_type_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_rate_type UNIQUE (company_id, rate_type_name)
);

-- 8. Rate List (Pricing Schedule)
CREATE TABLE IF NOT EXISTS f_ratelist (
    id SERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    rate_type_id INT NOT NULL REFERENCES rate_type_master(rate_type_id),
    investigation_id INT NOT NULL REFERENCES investigation_master(investigation_id),
    rate NUMERIC(12,4) NOT NULL DEFAULT 0.0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_ratelist_item UNIQUE (rate_type_id, investigation_id)
);

-- 9. Referring Doctor Master
CREATE TABLE IF NOT EXISTS doctor_referal (
    doctor_id SERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    doctor_code VARCHAR(50) NOT NULL,
    doctor_name VARCHAR(150) NOT NULL,
    mobile VARCHAR(20),
    email VARCHAR(150),
    specialization VARCHAR(100),
    share_percentage NUMERIC(5,2) DEFAULT 0.0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_doc_code UNIQUE (company_id, doctor_code)
);

-- Composite Performance Indexes for Sub-Millisecond Lookups
CREATE INDEX idx_inv_search ON investigation_master (company_id, is_active, test_code, test_name);
CREATE INDEX idx_obs_range_seek ON labobservation_range (observation_id, gender, from_age_days, to_age_days);
CREATE INDEX idx_rate_seek ON f_ratelist (company_id, rate_type_id, investigation_id, is_active);
CREATE INDEX idx_doc_search ON doctor_referal (company_id, is_active, doctor_name);
