-- LabRox Enterprise LIMS - Multi-Schema Database Initialization
CREATE SCHEMA IF NOT EXISTS iam_schema;
CREATE SCHEMA IF NOT EXISTS catalog_schema;
CREATE SCHEMA IF NOT EXISTS sample_schema;
CREATE SCHEMA IF NOT EXISTS diagnostic_schema;
CREATE SCHEMA IF NOT EXISTS qc_schema;
CREATE SCHEMA IF NOT EXISTS workflow_schema;
CREATE SCHEMA IF NOT EXISTS cron_schema;
CREATE SCHEMA IF NOT EXISTS order_schema;
CREATE SCHEMA IF NOT EXISTS reporting_schema;

-- 1. IAM SCHEMA INITIALIZATION
SET search_path TO iam_schema, public;

CREATE TABLE IF NOT EXISTS company (
    company_id INT PRIMARY KEY,
    company_code VARCHAR(50) NOT NULL UNIQUE,
    company_name VARCHAR(200) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS centres (
    centre_id INT PRIMARY KEY,
    company_id INT NOT NULL REFERENCES company(company_id),
    centre_code VARCHAR(50) NOT NULL UNIQUE,
    centre VARCHAR(200) NOT NULL,
    data_type VARCHAR(50) DEFAULT 'Centre',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    barcode_logic VARCHAR(10) DEFAULT '1',
    visit_type VARCHAR(20) DEFAULT 'OPD',
    hide_amount INT DEFAULT 0,
    set_mrp INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS designations (
    designation_id INT PRIMARY KEY,
    company_id INT NOT NULL REFERENCES company(company_id),
    designation_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS employee_master (
    employee_id BIGSERIAL PRIMARY KEY,
    company_id INT NOT NULL REFERENCES company(company_id),
    employee_code VARCHAR(50) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    password_algo VARCHAR(50) NOT NULL DEFAULT 'ARGON2ID',
    password_salt VARCHAR(255),
    name VARCHAR(200) NOT NULL,
    mobile VARCHAR(20),
    email VARCHAR(100),
    designation_id INT NOT NULL REFERENCES designations(designation_id),
    default_centre_id INT NOT NULL REFERENCES centres(centre_id),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_otp_login INT DEFAULT 0,
    show_dashboard INT DEFAULT 1,
    is_store_dashboard INT DEFAULT 0,
    theme VARCHAR(50) DEFAULT 'sky_blue_theme',
    font_size INT DEFAULT 13,
    login_count INT DEFAULT 1,
    super_admin INT DEFAULT 1,
    is_authorized_approval INT DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS menumaster (
    id INT PRIMARY KEY,
    menu_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS menumasterpriority (
    company_id INT NOT NULL,
    menu_id INT NOT NULL REFERENCES menumaster(id),
    priority INT NOT NULL,
    PRIMARY KEY(company_id, menu_id)
);

CREATE TABLE IF NOT EXISTS pagemasters (
    page_id INT PRIMARY KEY,
    menu_id INT NOT NULL REFERENCES menumaster(id),
    display VARCHAR(100) NOT NULL,
    url VARCHAR(200) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS pagemasterspriority (
    company_id INT NOT NULL,
    page_id INT NOT NULL REFERENCES pagemasters(page_id),
    priority INT NOT NULL,
    PRIMARY KEY(company_id, page_id)
);

CREATE TABLE IF NOT EXISTS audit_trail_events (
    event_id BIGSERIAL PRIMARY KEY,
    company_id INT NOT NULL,
    user_id BIGINT,
    username VARCHAR(100),
    centre_id INT,
    action_type VARCHAR(50) NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    details TEXT,
    ip_address VARCHAR(50),
    device_info VARCHAR(200),
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    signature_hash VARCHAR(64) NOT NULL
);

-- SEED CORE MASTER DATA
INSERT INTO company (company_id, company_code, company_name, is_active)
VALUES (1, 'LABROX', 'LabRox Healthcare & Diagnostics', TRUE)
ON CONFLICT (company_id) DO NOTHING;

INSERT INTO centres (centre_id, company_id, centre_code, centre, is_active)
VALUES (1, 1, 'MRL', 'Main Reference Lab', TRUE)
ON CONFLICT (centre_id) DO NOTHING;

INSERT INTO designations (designation_id, company_id, designation_name, is_active)
VALUES (1, 1, 'Super Administrator', TRUE)
ON CONFLICT (designation_id) DO NOTHING;

-- Default super admin (Password: Admin@123 with SHA-256 hash e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7)
INSERT INTO employee_master (
    employee_id, company_id, employee_code, username, password_hash, password_algo,
    name, mobile, email, designation_id, default_centre_id, is_active, super_admin, is_authorized_approval
) VALUES (
    1, 1, 'EMP0001', 'admin', 'e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7', 'SHA-256',
    'System Administrator', '9999999999', 'admin@labrox.com', 1, 1, TRUE, 1, 1
) ON CONFLICT (employee_id) DO NOTHING;

-- Navigation Menus
INSERT INTO menumaster (id, menu_name, is_active) VALUES
(1, 'Patient Registration', TRUE),
(2, 'Sample Logistics', TRUE),
(3, 'Diagnostics & Entry', TRUE),
(4, 'Medical Approval', TRUE),
(5, 'Reporting & Dispatch', TRUE),
(6, 'Quality Control', TRUE),
(7, 'Master Catalog', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO menumasterpriority (company_id, menu_id, priority) VALUES
(1, 1, 1), (1, 2, 2), (1, 3, 3), (1, 4, 4), (1, 5, 5), (1, 6, 6), (1, 7, 7)
ON CONFLICT (company_id, menu_id) DO NOTHING;

INSERT INTO pagemasters (page_id, menu_id, display, url, is_active) VALUES
(101, 1, 'Patient Registration', '/PatientRegistration', TRUE),
(102, 1, 'Work Order Booking', '/WorkOrder', TRUE),
(201, 2, 'Sample Collection', '/SampleCollection', TRUE),
(202, 2, 'Sample Accessioning', '/SampleReceiveBarcode', TRUE),
(203, 2, 'Department Receive', '/DepartmentReceive', TRUE),
(301, 3, 'Diagnostic Result Entry', '/ResultEntry', TRUE),
(401, 4, 'Doctor Approval', '/DoctorApproval', TRUE),
(501, 5, 'Doctor & Patient Reports', '/Reports', TRUE),
(601, 6, 'Westgard IQC', '/QC', TRUE),
(701, 7, 'Investigation Master', '/Investigation', TRUE),
(702, 7, 'Rate List Master', '/RateList', TRUE)
ON CONFLICT (page_id) DO NOTHING;

INSERT INTO pagemasterspriority (company_id, page_id, priority) VALUES
(1, 101, 1), (1, 102, 2), (1, 201, 1), (1, 202, 2), (1, 203, 3),
(1, 301, 1), (1, 401, 1), (1, 501, 1), (1, 601, 1), (1, 701, 1), (1, 702, 2)
ON CONFLICT (company_id, page_id) DO NOTHING;
