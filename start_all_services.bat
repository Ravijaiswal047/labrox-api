@echo off
echo ===============================================================================
echo Starting LabRox Enterprise Backend Services and API Gateway...
echo ===============================================================================

set "BASE_DIR=%~dp0"
set "MVN_CMD=C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.0.1\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"
if not exist "%MVN_CMD%" set "MVN_CMD=mvn"

echo [1/9] Starting IAM and Authentication Service (Port 8081)...
start "LabRox IAM Service (8081)" cmd /k "cd /d "%BASE_DIR%labrox-iam-service" && "%MVN_CMD%" spring-boot:run -Dspring-boot.run.profiles=dev"

echo [2/9] Starting Test Catalog and Master Data Service (Port 8082)...
start "LabRox Test Catalog Service (8082)" cmd /k "cd /d "%BASE_DIR%labrox-test-catalog-service" && "%MVN_CMD%" spring-boot:run -Dspring-boot.run.profiles=dev"

echo [3/9] Starting Sample Logistics and Accessioning Service (Port 8083)...
start "LabRox Sample Service (8083)" cmd /k "cd /d "%BASE_DIR%labrox-sample-service" && "%MVN_CMD%" spring-boot:run -Dspring-boot.run.profiles=dev"

echo [4/9] Starting Diagnostic Result Entry and Delta Check Service (Port 8084)...
start "LabRox Diagnostic Service (8084)" cmd /k "cd /d "%BASE_DIR%labrox-diagnostic-service" && "%MVN_CMD%" spring-boot:run -Dspring-boot.run.profiles=dev"

echo [5/9] Starting QC and Westgard Service (Port 8085)...
start "LabRox QC Service (8085)" cmd /k "cd /d "%BASE_DIR%labrox-qc-service" && "%MVN_CMD%" spring-boot:run -Dspring-boot.run.profiles=dev"

echo [6/9] Starting Doctor Approval and 21 CFR Part 11 Service (Port 8086)...
start "LabRox Approval Service (8086)" cmd /k "cd /d "%BASE_DIR%labrox-approval-service" && "%MVN_CMD%" spring-boot:run -Dspring-boot.run.profiles=dev"

echo [7/9] Starting Patient Registration and Cashier POS Billing Service (Port 8089)...
start "LabRox Order Service (8089)" cmd /k "cd /d "%BASE_DIR%labrox-order-service" && "%MVN_CMD%" spring-boot:run -Dspring-boot.run.profiles=dev"

echo [8/9] Starting Clinical PDF Reporting Service (Port 8091)...
start "LabRox Reporting Service (8091)" cmd /k "cd /d "%BASE_DIR%labrox-reporting-service" && "%MVN_CMD%" spring-boot:run -Dspring-boot.run.profiles=dev"

echo [9/9] Starting Spring Cloud Unified API Gateway (Port 8088)...
start "LabRox API Gateway (8088)" cmd /k "cd /d "%BASE_DIR%labrox-gateway-service" && "%MVN_CMD%" spring-boot:run -Dspring-boot.run.profiles=dev"

echo ===============================================================================
echo All LabRox backend microservices launched. Gateway active on http://localhost:8088
echo ===============================================================================
