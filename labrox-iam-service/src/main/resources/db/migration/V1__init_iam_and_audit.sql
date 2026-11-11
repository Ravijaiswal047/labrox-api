-- =============================================================================
-- LabRox IAM & 21 CFR Part 11 Audit Schema Migration
-- Flyway Version: V1
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS iam_schema;
SET search_path TO iam_schema, public;

-- 1. Company / Tenant Master
CREATE TABLE IF NOT EXISTS company (
    company_id SERIAL PRIMARY KEY,
    company_code VARCHAR(50) NOT NULL UNIQUE,
    company_name VARCHAR(150) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 2. Centre Master
CREATE TABLE IF NOT EXISTS centre_master (
    centre_id SERIAL PRIMARY KEY,
    company_id INT NOT NULL REFERENCES company(company_id),
    centre_code VARCHAR(50) NOT NULL,
    centre_name VARCHAR(150) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_centre_company_code UNIQUE (company_id, centre_code)
);

-- 3. Designation / Role Master
CREATE TABLE IF NOT EXISTS designation_master (
    designation_id SERIAL PRIMARY KEY,
    company_id INT NOT NULL REFERENCES company(company_id),
    designation_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_designation_company UNIQUE (company_id, designation_name)
);

-- 4. Employee / User Master
CREATE TABLE IF NOT EXISTS employee_master (
    employee_id SERIAL PRIMARY KEY,
    company_id INT NOT NULL REFERENCES company(company_id),
    employee_code VARCHAR(50) NOT NULL,
    title VARCHAR(20),
    name VARCHAR(150) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    password_algo VARCHAR(50) NOT NULL DEFAULT 'ARGON2ID',
    password_salt VARCHAR(100),
    email VARCHAR(150),
    mobile VARCHAR(20),
    default_centre_id INT NOT NULL REFERENCES centre_master(centre_id),
    designation_id INT NOT NULL REFERENCES designation_master(designation_id),
    rate_type_id INT DEFAULT 1,
    can_settlement BOOLEAN NOT NULL DEFAULT FALSE,
    can_refund BOOLEAN NOT NULL DEFAULT FALSE,
    can_discount_after_bill BOOLEAN NOT NULL DEFAULT FALSE,
    save_amendment BOOLEAN NOT NULL DEFAULT FALSE,
    block_excel_printout BOOLEAN NOT NULL DEFAULT FALSE,
    block_lab_report_printout BOOLEAN NOT NULL DEFAULT FALSE,
    amr_access BOOLEAN NOT NULL DEFAULT FALSE,
    unmasking BOOLEAN NOT NULL DEFAULT FALSE,
    is_super_admin BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_password_changed BOOLEAN NOT NULL DEFAULT FALSE,
    created_by INT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_employee_company_code UNIQUE (company_id, employee_code),
    CONSTRAINT uk_employee_company_user UNIQUE (company_id, username)
);

-- 5. Multi-Centre Access Mapping
CREATE TABLE IF NOT EXISTS centre_access (
    id SERIAL PRIMARY KEY,
    company_id INT NOT NULL REFERENCES company(company_id),
    employee_id INT NOT NULL REFERENCES employee_master(employee_id),
    centre_id INT NOT NULL REFERENCES centre_master(centre_id),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_centre_access UNIQUE (company_id, employee_id, centre_id)
);

-- 6. Menu Master Hierarchy
CREATE TABLE IF NOT EXISTS menumaster (
    id SERIAL PRIMARY KEY,
    company_id INT NOT NULL REFERENCES company(company_id),
    menu_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    priority INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS menumasterpriority (
    id SERIAL PRIMARY KEY,
    menu_id INT NOT NULL REFERENCES menumaster(id),
    company_id INT NOT NULL REFERENCES company(company_id),
    priority INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_menupriority UNIQUE (menu_id, company_id)
);

CREATE TABLE IF NOT EXISTS pagemasters (
    page_id SERIAL PRIMARY KEY,
    menu_id INT NOT NULL REFERENCES menumaster(id),
    company_id INT NOT NULL REFERENCES company(company_id),
    display VARCHAR(150) NOT NULL,
    url VARCHAR(255) NOT NULL,
    priority INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS pagemasterspriority (
    id SERIAL PRIMARY KEY,
    page_id INT NOT NULL REFERENCES pagemasters(page_id),
    company_id INT NOT NULL REFERENCES company(company_id),
    priority INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_pagepriority UNIQUE (page_id, company_id)
);

CREATE TABLE IF NOT EXISTS page_accesses (
    id SERIAL PRIMARY KEY,
    company_id INT NOT NULL REFERENCES company(company_id),
    menu_id INT NOT NULL REFERENCES menumaster(id),
    page_id INT NOT NULL REFERENCES pagemasters(page_id),
    designation_id INT NOT NULL REFERENCES designation_master(designation_id),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_page_access UNIQUE (company_id, page_id, designation_id)
);

CREATE TABLE IF NOT EXISTS page_accesses_favourite (
    id SERIAL PRIMARY KEY,
    company_id INT NOT NULL REFERENCES company(company_id),
    employee_id INT NOT NULL REFERENCES employee_master(employee_id),
    page_id INT NOT NULL REFERENCES pagemasters(page_id),
    priority INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_page_favourite UNIQUE (company_id, employee_id, page_id)
);

CREATE TABLE IF NOT EXISTS companyflag (
    flag_id INT NOT NULL,
    company_id INT NOT NULL REFERENCES company(company_id),
    flag_name VARCHAR(100) NOT NULL,
    flag_value INT NOT NULL DEFAULT 1,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (flag_id, company_id)
);

-- 7. User Login Audit History
CREATE TABLE IF NOT EXISTS users_login_detail (
    id BIGSERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    employee_id INT NOT NULL,
    centre_id INT NOT NULL,
    designation_id INT NOT NULL,
    login_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    logout_time TIMESTAMPTZ,
    ip_address VARCHAR(50),
    status VARCHAR(50) NOT NULL DEFAULT 'SUCCESS'
);

-- 8. 21 CFR Part 11 Hash-Chained Audit Trail (Append-Only)
CREATE TABLE IF NOT EXISTS audit_trail_events (
    id BIGSERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL, -- CREATE, UPDATE, APPROVE, AMEND, DELETE
    previous_value TEXT,
    new_value TEXT,
    change_reason VARCHAR(255),
    user_id INT NOT NULL,
    user_ip VARCHAR(50),
    timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    previous_hash VARCHAR(64) NOT NULL,
    current_hash VARCHAR(64) NOT NULL,
    signature VARCHAR(255)
);

-- Indexes for Sub-Millisecond Seek Operations
CREATE INDEX idx_emp_lookup ON employee_master (company_id, username, is_active);
CREATE INDEX idx_emp_code ON employee_master (company_id, employee_code);
CREATE INDEX idx_login_audit ON users_login_detail (company_id, employee_id, login_time DESC);
CREATE INDEX idx_audit_chain ON audit_trail_events (company_id, entity_name, entity_id, id ASC);
CREATE INDEX idx_page_access_lookup ON page_accesses (company_id, designation_id, page_id);
