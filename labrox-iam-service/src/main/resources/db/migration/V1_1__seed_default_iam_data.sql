-- Flyway Migration V1.1: Seed Standard Enterprise Master Data for LabRox
-- Seeds default company, superadmin user, main lab centre, and core navigation menus

INSERT INTO company (company_id, company_code, company_name, is_active, created_at)
VALUES (1, 'LABROX', 'LabRox Healthcare & Diagnostics', TRUE, NOW())
ON CONFLICT (company_id) DO UPDATE SET company_name = EXCLUDED.company_name;

INSERT INTO centres (centre_id, company_id, centre_code, centre, data_type, is_active, barcode_logic, visit_type, hide_amount, set_mrp)
VALUES (1, 1, 'MRL', 'Main Reference Lab', 'Centre', TRUE, '1', 'OPD', 0, 0)
ON CONFLICT (centre_id) DO UPDATE SET centre = EXCLUDED.centre;

INSERT INTO designations (designation_id, company_id, designation_name, is_active)
VALUES (1, 1, 'Super Administrator', TRUE)
ON CONFLICT (designation_id) DO UPDATE SET designation_name = EXCLUDED.designation_name;

-- Seed default admin user (Password: Admin@123 with legacy SHA-256 hash e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7)
INSERT INTO employee_master (
    employee_id, company_id, employee_code, username, password_hash, password_algo,
    name, mobile, email, designation_id, default_centre_id, is_active, is_otp_login,
    show_dashboard, is_store_dashboard, theme, font_size, login_count, super_admin, is_authorized_approval
)
VALUES (
    1, 1, 'EMP0001', 'admin', 'e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7', 'SHA-256',
    'System Administrator', '9999999999', 'admin@labrox.com', 1, 1, TRUE, 0,
    1, 0, 'sky_blue_theme', 13, 1, 1, 1
)
ON CONFLICT (employee_id) DO UPDATE SET
    username = EXCLUDED.username,
    password_hash = EXCLUDED.password_hash;

-- Seed Menus
INSERT INTO menumaster (id, menu_name, is_active) VALUES
(1, 'Patient Registration', TRUE),
(2, 'Sample Logistics', TRUE),
(3, 'Diagnostics & Entry', TRUE),
(4, 'Medical Approval', TRUE),
(5, 'Reporting & Dispatch', TRUE),
(6, 'Quality Control', TRUE),
(7, 'Master Catalog', TRUE)
ON CONFLICT (id) DO UPDATE SET menu_name = EXCLUDED.menu_name;

INSERT INTO menumasterpriority (company_id, menu_id, priority) VALUES
(1, 1, 1),
(1, 2, 2),
(1, 3, 3),
(1, 4, 4),
(1, 5, 5),
(1, 6, 6),
(1, 7, 7)
ON CONFLICT (company_id, menu_id) DO UPDATE SET priority = EXCLUDED.priority;

-- Seed Pages
INSERT INTO pagemasters (page_id, menu_id, display, url, is_active) VALUES
(101, 1, 'Patient Registration', '/PatientRegistration', TRUE),
(102, 1, 'Work Order Booking', '/WorkOrder', TRUE),
(201, 2, 'Sample Collection', '/SampleCollection', TRUE),
(202, 2, 'Department Receive', '/DepartmentReceive', TRUE),
(301, 3, 'Result Entry', '/ResultEntry', TRUE),
(401, 4, 'Doctor Approval', '/Approval', TRUE),
(501, 5, 'Lab Reports', '/CombineLabReport', TRUE),
(502, 5, 'Dispatch Report', '/DispatchReport', TRUE),
(601, 6, 'QC LJ Charts', '/QualityControl', TRUE),
(701, 7, 'Test Catalog Master', '/InvestigationMaster', TRUE),
(702, 7, 'Centre Master', '/CentreMaster', TRUE)
ON CONFLICT (page_id) DO UPDATE SET display = EXCLUDED.display;

INSERT INTO pagemasterspriority (company_id, page_id, priority, is_active) VALUES
(1, 101, 1, TRUE),
(1, 102, 2, TRUE),
(1, 201, 1, TRUE),
(1, 202, 2, TRUE),
(1, 301, 1, TRUE),
(1, 401, 1, TRUE),
(1, 501, 1, TRUE),
(1, 502, 2, TRUE),
(1, 601, 1, TRUE),
(1, 701, 1, TRUE),
(1, 702, 2, TRUE)
ON CONFLICT (company_id, page_id) DO UPDATE SET priority = EXCLUDED.priority;

-- Grant Super Administrator full access to all pages
INSERT INTO page_accesses (company_id, designation_id, page_id, menuid, is_active) VALUES
(1, 1, 101, 1, TRUE),
(1, 1, 102, 1, TRUE),
(1, 1, 201, 2, TRUE),
(1, 1, 202, 2, TRUE),
(1, 1, 301, 3, TRUE),
(1, 1, 401, 4, TRUE),
(1, 1, 501, 5, TRUE),
(1, 1, 502, 5, TRUE),
(1, 1, 601, 6, TRUE),
(1, 1, 701, 7, TRUE),
(1, 1, 702, 7, TRUE)
ON CONFLICT (company_id, designation_id, page_id) DO NOTHING;

-- Seed Company Flag (FlagID 117 for checklist accept, FlagID 87 for OPD rate)
INSERT INTO companyflag (flag_id, company_id, flag_name, flag_value, is_active) VALUES
(117, 1, 'CompanychecklistAccept', 1, TRUE),
(87, 1, 'OPDConsultationRateTypeID', 1, TRUE)
ON CONFLICT (flag_id, company_id) DO NOTHING;
