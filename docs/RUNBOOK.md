# LabRox Enterprise LIMS - Operational Runbook

## 1. Local Development Operation

### Prerequisites
- Java 17 or Java 21 LTS installed (`java -version`)
- Maven 3.9+ or IntelliJ IDEA built-in Maven
- Node.js 18+ or 20 LTS installed (`node -v`)

### Fast Startup (No Docker Needed - Standalone JAR Mode)
1. Open PowerShell or Terminal at `D:\LabRox\labrox-backend`.
2. Run `.\start_all_services_jar.ps1` (or double-click `start_all_services_jar.bat`).
3. In a separate terminal at `D:\LabRox\labrox-frontend`, run:
   ```bash
   npm run dev
   ```
4. Access `http://localhost:3000` in Google Chrome or Edge.
5. Login credentials:
   - Username: `admin`
   - Password: `Admin@123`

---

## 2. Docker Compose Production Deployment

1. Navigate to `D:\LabRox\infra`.
2. Launch the complete containerized stack:
   ```bash
   docker compose up -d --build
   ```
3. Check status of all containers:
   ```bash
   docker compose ps
   ```
4. View gateway logs:
   ```bash
   docker compose logs -f labrox-gateway-service
   ```
5. Teardown with volume retention:
   ```bash
   docker compose down
   ```

---

## 3. Microservice Ports & Health Endpoints

| Microservice | Base Port | Health Endpoint |
| :--- | :--- | :--- |
| Unified API Gateway | `8088` | `http://localhost:8088/actuator/health` |
| IAM & Audit Service | `8081` | `http://localhost:8081/actuator/health` |
| Test Catalog Service | `8082` | `http://localhost:8082/actuator/health` |
| Sample Logistics Service | `8083` | `http://localhost:8083/actuator/health` |
| Diagnostic Service | `8084` | `http://localhost:8084/actuator/health` |
| Quality Control Service | `8085` | `http://localhost:8085/actuator/health` |
| Approval Service | `8086` | `http://localhost:8086/actuator/health` |
| Order & Billing Service | `8089` | `http://localhost:8089/actuator/health` |
| Reporting Service | `8091` | `http://localhost:8091/actuator/health` |
| Cron Orchestrator | `8087` | `http://localhost:8087/actuator/health` |

---

## 4. Troubleshooting & Diagnostic Commands

- **Port in Use Conflict:**
  ```powershell
  Get-NetTCPConnection -LocalPort 8088 | Select-Object OwningProcess
  Stop-Process -Id <PID> -Force
  ```
- **Verify Gateway Route Mapping:**
  ```powershell
  Invoke-RestMethod -Uri "http://localhost:8088/actuator/gateway/routes" | ConvertTo-Json -Depth 5
  ```
- **Trigger Smoke Tests:**
  ```powershell
  powershell -ExecutionPolicy Bypass -File "D:\LabRox\scripts\smoke-test.ps1"
  ```
