# LabRox Enterprise Backend Services & API Gateway Launcher
Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "Starting LabRox Enterprise Backend Services & API Gateway..." -ForegroundColor Cyan
Write-Host "===============================================================================" -ForegroundColor Cyan

$baseDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# Locate Maven
$mvnCmd = "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.0.1\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"
if (-not (Test-Path $mvnCmd)) {
    $mvnCmd = "mvn"
}

$services = @(
    @{ Name = "LabRox IAM Service"; Port = 8081; Dir = "labrox-iam-service" },
    @{ Name = "LabRox Test Catalog Service"; Port = 8082; Dir = "labrox-test-catalog-service" },
    @{ Name = "LabRox Sample Service"; Port = 8083; Dir = "labrox-sample-service" },
    @{ Name = "LabRox Diagnostic Service"; Port = 8084; Dir = "labrox-diagnostic-service" },
    @{ Name = "LabRox QC Service"; Port = 8085; Dir = "labrox-qc-service" },
    @{ Name = "LabRox Approval Service"; Port = 8086; Dir = "labrox-approval-service" },
    @{ Name = "LabRox Order Service"; Port = 8089; Dir = "labrox-order-service" },
    @{ Name = "LabRox Reporting Service"; Port = 8091; Dir = "labrox-reporting-service" },
    @{ Name = "LabRox API Gateway"; Port = 8088; Dir = "labrox-gateway-service" }
)

foreach ($svc in $services) {
    Write-Host "Launching $($svc.Name) on Port $($svc.Port)..." -ForegroundColor Green
    $command = "Set-Location '$baseDir\$($svc.Dir)'; & '$mvnCmd' spring-boot:run -Dspring-boot.run.profiles=dev"
    Start-Process -FilePath "powershell.exe" -ArgumentList "-NoExit", "-Command", $command
}

Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "All LabRox backend microservices launched. Gateway active on http://localhost:8088" -ForegroundColor Yellow
Write-Host "===============================================================================" -ForegroundColor Cyan
