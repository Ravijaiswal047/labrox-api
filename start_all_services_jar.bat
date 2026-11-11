@echo off
echo ===============================================================================
echo Starting LabRox Enterprise Backend (Direct JARs with Java 17)...
echo ===============================================================================

set "BASE_DIR=%~dp0"

echo [1/9] Starting IAM & Authentication Service (Port 8081)...
set "JAR=%BASE_DIR%labrox-iam-service\target\labrox-iam-service-1.0.0-SNAPSHOT-exec.jar"
if not exist "%JAR%" set "JAR=%BASE_DIR%labrox-iam-service\target\labrox-iam-service-1.0.0-SNAPSHOT.jar"
start "LabRox IAM Service (8081)" cmd /k "java -Dspring.profiles.active=dev -jar "%JAR%""

echo [2/9] Starting Test Catalog & Master Data Service (Port 8082)...
set "JAR=%BASE_DIR%labrox-test-catalog-service\target\labrox-test-catalog-service-1.0.0-SNAPSHOT-exec.jar"
if not exist "%JAR%" set "JAR=%BASE_DIR%labrox-test-catalog-service\target\labrox-test-catalog-service-1.0.0-SNAPSHOT.jar"
start "LabRox Test Catalog Service (8082)" cmd /k "java -Dspring.profiles.active=dev -jar "%JAR%""

echo [3/9] Starting Sample Logistics & Accessioning Service (Port 8083)...
set "JAR=%BASE_DIR%labrox-sample-service\target\labrox-sample-service-1.0.0-SNAPSHOT-exec.jar"
if not exist "%JAR%" set "JAR=%BASE_DIR%labrox-sample-service\target\labrox-sample-service-1.0.0-SNAPSHOT.jar"
start "LabRox Sample Service (8083)" cmd /k "java -Dspring.profiles.active=dev -jar "%JAR%""

echo [4/9] Starting Diagnostic Result Entry & Delta Check Service (Port 8084)...
set "JAR=%BASE_DIR%labrox-diagnostic-service\target\labrox-diagnostic-service-1.0.0-SNAPSHOT-exec.jar"
if not exist "%JAR%" set "JAR=%BASE_DIR%labrox-diagnostic-service\target\labrox-diagnostic-service-1.0.0-SNAPSHOT.jar"
start "LabRox Diagnostic Service (8084)" cmd /k "java -Dspring.profiles.active=dev -jar "%JAR%""

echo [5/9] Starting QC & Westgard Service (Port 8085)...
set "JAR=%BASE_DIR%labrox-qc-service\target\labrox-qc-service-1.0.0-SNAPSHOT-exec.jar"
if not exist "%JAR%" set "JAR=%BASE_DIR%labrox-qc-service\target\labrox-qc-service-1.0.0-SNAPSHOT.jar"
start "LabRox QC Service (8085)" cmd /k "java -Dspring.profiles.active=dev -jar "%JAR%""

echo [6/9] Starting Doctor Approval & 21 CFR Part 11 Service (Port 8086)...
set "JAR=%BASE_DIR%labrox-approval-service\target\labrox-approval-service-1.0.0-SNAPSHOT-exec.jar"
if not exist "%JAR%" set "JAR=%BASE_DIR%labrox-approval-service\target\labrox-approval-service-1.0.0-SNAPSHOT.jar"
start "LabRox Approval Service (8086)" cmd /k "java -Dspring.profiles.active=dev -jar "%JAR%""

echo [7/9] Starting Patient Registration & Cashier POS Billing Service (Port 8089)...
set "JAR=%BASE_DIR%labrox-order-service\target\labrox-order-service-1.0.0-SNAPSHOT-exec.jar"
if not exist "%JAR%" set "JAR=%BASE_DIR%labrox-order-service\target\labrox-order-service-1.0.0-SNAPSHOT.jar"
start "LabRox Order Service (8089)" cmd /k "java -Dspring.profiles.active=dev -jar "%JAR%""

echo [8/9] Starting Clinical PDF Reporting Service (Port 8091)...
set "JAR=%BASE_DIR%labrox-reporting-service\target\labrox-reporting-service-1.0.0-SNAPSHOT-exec.jar"
if not exist "%JAR%" set "JAR=%BASE_DIR%labrox-reporting-service\target\labrox-reporting-service-1.0.0-SNAPSHOT.jar"
start "LabRox Reporting Service (8091)" cmd /k "java -Dspring.profiles.active=dev -jar "%JAR%""

echo [9/9] Starting Spring Cloud Unified API Gateway (Port 8088)...
set "JAR=%BASE_DIR%labrox-gateway-service\target\labrox-gateway-service-1.0.0-SNAPSHOT-exec.jar"
if not exist "%JAR%" set "JAR=%BASE_DIR%labrox-gateway-service\target\labrox-gateway-service-1.0.0-SNAPSHOT.jar"
start "LabRox API Gateway (8088)" cmd /k "java -Dspring.profiles.active=dev -jar "%JAR%""

echo ===============================================================================
echo All LabRox backend microservices launched. Gateway active on http://localhost:8088
echo ===============================================================================
