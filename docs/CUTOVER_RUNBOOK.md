# LabRox LIMS Enterprise — Production Cutover Runbook
**Zero-Downtime Maintenance Window, Validation & Rollback Protocol**
*Target: ≤ 45-Minute Total Maintenance Window | Zero Clinical Record Loss*

---

## 1. Cutover Overview & Key Stakeholders
This runbook governs the production cutover from the legacy .NET 8 / MySQL LIMS (`nanjappa_api_v9`) to the new cloud-native Java 21 / Spring Boot 3.3 / PostgreSQL 16 microservices platform (`LabRox`).

- **Cutover Commander:** Principal Software Architect
- **Lead Database Administrator:** PostgreSQL Specialist
- **Lead QA / GxP Validation Engineer:** 21 CFR Part 11 Validation Lead
- **Clinical Laboratory Director:** Sign-off Authority for Patient Care Release

---

## 2. Phase-by-Phase Cutover Timeline

### Phase I: T-7 Days (Staging Rehearsal & Dry-Run)
- [ ] Restore full sanitized production database snapshot into Staging.
- [ ] Run `legacy_migration_pipeline.py` and measure exact elapsed runtime.
- [ ] Execute automated `k6_lims_benchmark.js` against Staging (verify $p95 < 400\text{ ms}$).
- [ ] Verify 21 CFR Part 11 hash-chain integrity check on all historical records.
- [ ] Issue clinical staff briefing and scheduled maintenance notice (Saturday 22:00 to 23:00).

### Phase II: T-2 Hours (Pre-Cutover Preparation)
- [ ] Verify health and readiness of target PostgreSQL 16 cluster, Redis, Kafka, and MinIO.
- [ ] Verify target backend containers are primed:
  ```bash
  docker-compose -f docker-compose.prod.yml ps
  ```
- [ ] Display global maintenance warning banner on legacy frontend:
  *"Scheduled maintenance will begin in 120 minutes. Please complete pending approvals."*
- [ ] Verify automated database backup of legacy MySQL completed and verified.

### Phase III: T-0 (Maintenance Window Execution — Max 45 Mins)

| Step | Time | Action | Command / Verification | Owner |
|:---:|:---:|:---|:---|:---:|
| **1** | T+00 | Activate Legacy Read-Only Mode | Set `SET GLOBAL read_only = ON;` in legacy MySQL. | DBA |
| **2** | T+03 | Stop Legacy Background Workers | Terminate legacy .NET Quartz cronjobs and HL7 listeners. | DevOps |
| **3** | T+05 | Execute Final Delta Migration | `python legacy_migration_pipeline.py --mode=delta` | Migration Lead |
| **4** | T+15 | Reconcile Row Counts & Checksums | Execute `SELECT count(*) FROM ...` across all schemas. 100% parity required. | DBA |
| **5** | T+20 | Reset Primary Key Sequences | Execute `SELECT setval(...)` for all PostgreSQL `BIGSERIAL` columns. | DBA |
| **6** | T+25 | Start Modern LabRox Services | `docker-compose -f docker-compose.prod.yml up -d` | DevOps |
| **7** | T+28 | Execute Live Smoke Test Suite | Run Automated Smoke Test Script (Auth, Accession, Result, Approval, Report). | QA Lead |
| **8** | T+35 | DNS Switchover | Update Cloudflare / Route 53 `A` / `CNAME` records to modern Gateway IP. | Network Lead |
| **9** | T+40 | Verify External Traffic Routing | Monitor live HTTP traffic on Spring Cloud Gateway. | DevOps |
| **10**| T+45 | Declare Successful Cutover | Deactivate maintenance banner. Notify Laboratory Director. | Commander |

---

## 3. Automated Smoke Test Checklist
Immediately following step 6, QA executes the automated end-to-end sanity verification:
1. **Health Check:** `GET /actuator/health` returns `{"status": "UP"}` across all 10 microservices.
2. **Authentication:** Pathologist login with Argon2id credentials returns valid JWT and dynamic menu.
3. **Phlebotomy:** Create accession for test patient, verify unique 12-char barcode generated (`S26...`).
4. **Result Entry:** Enter glucose and HbA1c, verify automated delta check evaluates `DELTA_NORMAL`.
5. **Pathologist Approval:** Sign off report using 4-digit PIN; verify 21 CFR Part 11 SHA-256 hash created.
6. **Report Generation:** Download official A4 PDF; verify NABL letterhead, abnormal highlighting, and QR stamp.
7. **Document Verification:** Query `/api/v1/reports/verify/{token}`; verify returns `authentic: true`.

---

## 4. Rollback Protocol & Decision Criteria

### Rollback Decision Triggers (Non-Negotiable)
The Cutover Commander will immediately abort and initiate rollback if ANY of the following occur:
1. Migration data discrepancy $> 0$ unresolvable rows after 15 minutes.
2. Critical failure in result entry or report approval that cannot be hotfixed in $\le 10$ minutes.
3. System response time $p95 > 2000\text{ ms}$ for $> 5$ consecutive minutes under live traffic.
4. Total elapsed maintenance time exceeds 50 minutes without clear resolution in sight.

### Rollback Execution Steps (Target Duration: ≤ 10 Mins)
1. Revert DNS records to point back to legacy .NET API and React JS servers.
2. Disable read-only mode in legacy MySQL: `SET GLOBAL read_only = OFF;`.
3. Restart legacy .NET background workers and Quartz services.
4. Notify lab staff to resume operations on legacy system.
5. Export PostgreSQL transaction logs for post-mortem analysis.

---

## 5. Post-Cutover Hypercare (T+1h to T+48h)
- 24/7 War Room staffed by Tech Lead, Database Specialist, and Support Engineer.
- Real-time monitoring of:
  - Spring Boot Actuator metrics via Prometheus / Grafana.
  - PostgreSQL active connections, lock waits, and cache hit ratios ($> 99\%$).
  - Kafka consumer group lag for audit and notifications.
  - User feedback channels for instant bug triaging.
