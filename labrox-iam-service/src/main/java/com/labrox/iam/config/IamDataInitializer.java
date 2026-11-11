package com.labrox.iam.config;

import com.labrox.iam.domain.CentreMaster;
import com.labrox.iam.domain.DesignationMaster;
import com.labrox.iam.domain.Employee;
import com.labrox.iam.repository.CentreMasterRepository;
import com.labrox.iam.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class IamDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(IamDataInitializer.class);

    private final EmployeeRepository employeeRepository;
    private final CentreMasterRepository centreMasterRepository;
    private final JdbcTemplate jdbcTemplate;

    public IamDataInitializer(
            EmployeeRepository employeeRepository,
            CentreMasterRepository centreMasterRepository,
            JdbcTemplate jdbcTemplate) {
        this.employeeRepository = employeeRepository;
        this.centreMasterRepository = centreMasterRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        log.info("Initializing LabRox IAM Master Data...");

        try {
            // 1. Ensure schemas exist
            jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS iam_schema");

            // 2. Ensure company table
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS company (
                    company_id INT PRIMARY KEY,
                    company_code VARCHAR(50) NOT NULL,
                    company_name VARCHAR(150) NOT NULL,
                    is_active BOOLEAN NOT NULL DEFAULT TRUE
                )
            """);
            jdbcTemplate.execute("""
                MERGE INTO company (company_id, company_code, company_name, is_active)
                KEY (company_id)
                VALUES (1, 'LABROX', 'LabRox Healthcare & Diagnostics', TRUE)
            """);

            // 3. Ensure legacy 'centres' table for LegacyCentreController
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS centres (
                    centre_id INT PRIMARY KEY,
                    company_id INT DEFAULT 1,
                    centre_code VARCHAR(50) DEFAULT 'MRL',
                    centre VARCHAR(150) NOT NULL,
                    data_type VARCHAR(50) DEFAULT 'Centre',
                    barcode_logic VARCHAR(10) DEFAULT '1',
                    visit_type VARCHAR(20) DEFAULT 'Walkin',
                    hide_amount INT DEFAULT 0,
                    set_mrp INT DEFAULT 0,
                    is_active BOOLEAN NOT NULL DEFAULT TRUE
                )
            """);
            jdbcTemplate.execute("""
                MERGE INTO centres (centre_id, company_id, centre_code, centre, data_type, barcode_logic, visit_type, hide_amount, set_mrp, is_active)
                KEY (centre_id)
                VALUES (1, 1, 'MRL', 'Main Reference Lab', 'Centre', '1', 'Walkin', 0, 0, TRUE)
            """);

            // 4. Ensure centre_master JPA entity record
            if (centreMasterRepository.findAll().isEmpty()) {
                CentreMaster centre = CentreMaster.builder()
                        .companyId(1)
                        .centreCode("MRL")
                        .centreName("Main Reference Lab")
                        .active(true)
                        .build();
                centreMasterRepository.save(centre);
                log.info("Default CentreMaster created: MRL - Main Reference Lab");
            }

            // 5. Ensure designation table / JPA record
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS designation_master (
                    designation_id INT PRIMARY KEY,
                    company_id INT DEFAULT 1,
                    designation_name VARCHAR(100) NOT NULL,
                    is_active BOOLEAN NOT NULL DEFAULT TRUE
                )
            """);
            jdbcTemplate.execute("""
                MERGE INTO designation_master (designation_id, company_id, designation_name, is_active)
                KEY (designation_id)
                VALUES (1, 1, 'Super Administrator', TRUE)
            """);

            // 6. Ensure admin user exists in employee_master
            Optional<Employee> adminOpt = employeeRepository.findByUsernameAndActiveTrue("admin");
            if (adminOpt.isEmpty()) {
                Employee admin = Employee.builder()
                        .companyId(1)
                        .employeeCode("EMP0001")
                        .title("Mr.")
                        .name("System Administrator")
                        .username("admin")
                        .passwordHash("e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7") // Admin@123
                        .passwordAlgo("SHA-256")
                        .email("admin@labrox.com")
                        .mobile("9999999999")
                        .defaultCentreId(1)
                        .designationId(1)
                        .rateTypeId(1)
                        .canSettlement(true)
                        .canRefund(true)
                        .canDiscountAfterBill(true)
                        .saveAmendment(true)
                        .blockExcelPrintout(false)
                        .blockLabReportPrintout(false)
                        .amrAccess(true)
                        .unmasking(true)
                        .superAdmin(true)
                        .active(true)
                        .passwordChanged(true)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();
                employeeRepository.save(admin);
                log.info("Default SuperAdmin seeded: admin / Admin@123");
            }

            // 7. Ensure menu tables exist and have navigation items
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS menumaster (
                    id INT PRIMARY KEY,
                    menu_name VARCHAR(100) NOT NULL,
                    description VARCHAR(255),
                    priority INT DEFAULT 0,
                    is_active BOOLEAN NOT NULL DEFAULT TRUE
                )
            """);
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS menumasterpriority (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    menu_id INT NOT NULL,
                    company_id INT NOT NULL,
                    priority INT DEFAULT 0,
                    is_active BOOLEAN NOT NULL DEFAULT TRUE
                )
            """);
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS pagemasters (
                    page_id INT PRIMARY KEY,
                    menu_id INT NOT NULL,
                    display VARCHAR(150) NOT NULL,
                    url VARCHAR(255) NOT NULL,
                    priority INT DEFAULT 0,
                    is_active BOOLEAN NOT NULL DEFAULT TRUE
                )
            """);
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS pagemasterspriority (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    page_id INT NOT NULL,
                    company_id INT NOT NULL,
                    priority INT DEFAULT 0,
                    is_active BOOLEAN NOT NULL DEFAULT TRUE
                )
            """);
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS page_accesses (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    company_id INT NOT NULL,
                    menu_id INT,
                    page_id INT NOT NULL,
                    designation_id INT NOT NULL,
                    is_active BOOLEAN NOT NULL DEFAULT TRUE
                )
            """);
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS page_accesses_favourite (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    company_id INT NOT NULL,
                    employee_id INT NOT NULL,
                    page_id INT NOT NULL,
                    priority INT DEFAULT 0,
                    is_active BOOLEAN NOT NULL DEFAULT TRUE
                )
            """);

            // Seed menus
            int[][] menus = {
                {1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5}, {6, 6}, {7, 7}
            };
            String[] menuNames = {
                "Patient Registration", "Sample Logistics", "Diagnostics & Entry",
                "Medical Approval", "Reporting & Dispatch", "Quality Control", "Master Catalog"
            };
            for (int i = 0; i < menus.length; i++) {
                int menuId = menus[i][0];
                int prio = menus[i][1];
                String name = menuNames[i];
                jdbcTemplate.update("MERGE INTO menumaster (id, menu_name, priority, is_active) KEY (id) VALUES (?, ?, ?, TRUE)",
                        menuId, name, prio);
            }

            // Seed pages
            Object[][] pages = {
                {101, 1, "Patient Registration", "/PatientRegistration", 1},
                {102, 1, "Work Order Booking", "/WorkOrder", 2},
                {201, 2, "Sample Collection", "/SampleCollection", 1},
                {202, 2, "Department Receive", "/DepartmentReceive", 2},
                {301, 3, "Result Entry", "/ResultEntry", 1},
                {401, 4, "Doctor Approval", "/Approval", 1},
                {501, 5, "Lab Reports", "/CombineLabReport", 1},
                {502, 5, "Dispatch Report", "/DispatchReport", 2},
                {601, 6, "QC LJ Charts", "/QualityControl", 1},
                {701, 7, "Test Catalog Master", "/InvestigationMaster", 1},
                {702, 7, "Centre Master", "/CentreMaster", 2}
            };
            for (Object[] p : pages) {
                jdbcTemplate.update("MERGE INTO pagemasters (page_id, menu_id, display, url, priority, is_active) KEY (page_id) VALUES (?, ?, ?, ?, ?, TRUE)",
                        p[0], p[1], p[2], p[3], p[4]);
            }

            log.info("LabRox IAM Master Data Initialization completed successfully.");

        } catch (Exception ex) {
            log.warn("Non-fatal note during IAM data initialization: {}", ex.getMessage());
        }
    }
}
