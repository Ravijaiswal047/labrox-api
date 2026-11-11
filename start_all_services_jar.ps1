# LabRox Enterprise Backend Direct JAR Launcher (Fast Production Mode)
Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "Starting LabRox Enterprise Backend (Direct JARs with Java 17)..." -ForegroundColor Cyan
Write-Host "===============================================================================" -ForegroundColor Cyan

$baseDir = Split-Path -Parent $MyInvocation.MyCommand.Path

$services = @(
    @{ Name = "LabRox IAM Service"; Port = 8081; Jar = "labrox-iam-service\target\labrox-iam-service-1.0.0-SNAPSHOT.jar" },
    @{ Name = "LabRox Test Catalog Service"; Port = 8082; Jar = "labrox-test-catalog-service\target\labrox-test-catalog-service-1.0.0-SNAPSHOT.jar" },
    @{ Name = "LabRox Sample Service"; Port = 8083; Jar = "labrox-sample-service\target\labrox-sample-service-1.0.0-SNAPSHOT.jar" },
    @{ Name = "LabRox Diagnostic Service"; Port = 8084; Jar = "labrox-diagnostic-service\target\labrox-diagnostic-service-1.0.0-SNAPSHOT.jar" },
    @{ Name = "LabRox QC Service"; Port = 8085; Jar = "labrox-qc-service\target\labrox-qc-service-1.0.0-SNAPSHOT.jar" },
    @{ Name = "LabRox Approval Service"; Port = 8086; Jar = "labrox-approval-service\target\labrox-approval-service-1.0.0-SNAPSHOT.jar" },
    @{ Name = "LabRox Order Service"; Port = 8089; Jar = "labrox-order-service\target\labrox-order-service-1.0.0-SNAPSHOT.jar" },
    @{ Name = "LabRox Reporting Service"; Port = 8091; Jar = "labrox-reporting-service\target\labrox-reporting-service-1.0.0-SNAPSHOT.jar" },
    @{ Name = "LabRox API Gateway"; Port = 8088; Jar = "labrox-gateway-service\target\labrox-gateway-service-1.0.0-SNAPSHOT.jar" }
)

foreach ($svc in $services) {
    Write-Host "Launching $($svc.Name) on Port $($svc.Port)..." -ForegroundColor Green
    $jarPath = "$baseDir\$($svc.Jar)"
    $jarExec = $jarPath -replace '\.jar$', '-exec.jar'
    if (Test-Path $jarExec) { $jarPath = $jarExec }
    Start-Process -FilePath "powershell.exe" -ArgumentList "-NoExit", "-Command", "java -Dspring.profiles.active=dev -jar '$jarPath'"
}

Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "All LabRox backend microservices launched. Gateway active on http://localhost:8088" -ForegroundColor Yellow
Write-Host "===============================================================================" -ForegroundColor Cyan
