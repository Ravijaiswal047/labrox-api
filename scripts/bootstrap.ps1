# LabRox Enterprise LIMS - Windows Bootstrap Script
Write-Host "===============================================================================" -ForegroundColor Cyan
Write-Host "LabRox LIMS Enterprise Bootstrap (Java 21 / Spring Boot 3 / React 18)" -ForegroundColor Cyan
Write-Host "===============================================================================" -ForegroundColor Cyan

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$rootDir = Split-Path -Parent $scriptDir
$backendDir = Join-Path $rootDir "labrox-backend"
$frontendDir = Join-Path $rootDir "labrox-frontend"

# Step 1: Detect Maven, Java, and Node
$mvnBin = "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.0.1\plugins\maven-plugin\lib\maven3\bin"
$nodeBin = "C:\Program Files\nodejs"
$env:PATH = "$mvnBin;$nodeBin;$env:PATH"
if (Test-Path "$mvnBin\mvn.cmd") {
    $mvnCmd = "$mvnBin\mvn.cmd"
} else {
    $mvnCmd = "mvn"
}

Write-Host "`n[Step 1/4] Verifying Toolchain..." -ForegroundColor Yellow
& $mvnCmd -version
java -version

# Step 2: Build and Package Backend
Write-Host "`n[Step 2/4] Building Backend Microservices..." -ForegroundColor Yellow
Set-Location $backendDir
& $mvnCmd clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "Maven build failed!" -ForegroundColor Red
    exit 1
}

# Step 3: Check Frontend Dependencies
Write-Host "`n[Step 3/4] Checking Frontend Build..." -ForegroundColor Yellow
Set-Location $frontendDir
if (-not (Test-Path "node_modules")) {
    Write-Host "Installing frontend dependencies..." -ForegroundColor Green
    npm ci --legacy-peer-deps
}

# Step 4: Launching System
Write-Host "`n[Step 4/4] Starting Microservices via Launcher..." -ForegroundColor Yellow
Set-Location $backendDir
& ".\start_all_services_jar.ps1"

Write-Host "`n===============================================================================" -ForegroundColor Cyan
Write-Host "LabRox LIMS Bootstrap Complete!" -ForegroundColor Green
Write-Host "Gateway:  http://localhost:8088" -ForegroundColor Green
Write-Host "Frontend: http://localhost:3000" -ForegroundColor Green
Write-Host "Default Login: admin / Admin@123" -ForegroundColor Green
Write-Host "===============================================================================" -ForegroundColor Cyan
