# LabRox LIMS Enterprise — Disaster Recovery & Business Continuity Plan
**High Availability, Point-In-Time Recovery (PITR) & Geo-Redundancy Architecture**
*Target: RPO ≤ 5 Minutes | RTO ≤ 30 Minutes | 21 CFR Part 11 Regulatory Compliance*

---

## 1. Objectives & Business Impact Analysis (BIA)
A diagnostic laboratory operates 24/7/365 to deliver critical medical findings, including emergency and panic alert values. Prolonged downtime threatens patient safety and clinical accreditation (NABL/CAP/ISO 15189).

| Metric | Target SLA | Justification |
|:---|:---:|:---|
| **RPO (Recovery Point Objective)** | **$\le 5$ Minutes** | Continuous WAL archiving guarantees near-zero clinical data loss. |
| **RTO (Recovery Time Objective)** | **$\le 30$ Minutes** | Automated Patroni failover restores write capabilities in $< 30$ seconds; full cold recovery in $< 30$ minutes. |
| **Audit Trail Recovery** | **$0$ Data Loss** | Synchronous replication enforced on `iam_schema.system_audit_trail` and approvals. |

---

## 2. Infrastructure & Replication Topology

```
+-----------------------------------------------------------------------------------+
|                              PRIMARY REGION (AZ-1)                                |
|                                                                                   |
|  +---------------------+      Sync WAL      +----------------------------------+  |
|  | PostgreSQL Primary  | -----------------> | PostgreSQL Standby (Sync AZ-2)   |  |
|  +---------------------+                    +----------------------------------+  |
|             |                                                                     |
|             | Continuous WAL Stream                                               |
|             v                                                                     |
|  +---------------------+      Cross-Region S3 Sync      +----------------------+  |
|  | MinIO / S3 Storage  | -----------------------------> | MinIO DR Secondary   |  |
|  | (Backups & Reports) |                                | (Secondary Region)   |  |
|  +---------------------+                                +----------------------+  |
+-----------------------------------------------------------------------------------+
```

### 2.1 Database High Availability (Patroni + PostgreSQL 16)
- **Primary Node:** Accepts all read/write transactions.
- **Synchronous Standby (AZ-2):** Guarantees zero transaction loss for regulated audit and electronic signatures (`synchronous_commit = on`).
- **Asynchronous Read Replica (AZ-3):** Dedicated to heavy reporting, analytics, and bulk query offloading.
- **Automated Failover:** Orchestrated via Patroni and etcd/Consul consensus. If the primary becomes unresponsive for 10 seconds, the synchronous standby is automatically promoted without human intervention.

### 2.2 Kafka & Event Streaming Redundancy
- **Topology:** 3-broker distributed Kafka cluster.
- **Topic Configuration:**
  - `replication.factor = 3`
  - `min.insync.replicas = 2`
  - `acks = all`
- Ensures every audit event and sample status transition is persisted on at least two independent physical nodes before acknowledging the producer.

---

## 3. Backup Strategy & Point-in-Time Recovery (PITR)

### 3.1 Backup Schedule
1. **Continuous WAL Archiving:** PostgreSQL `archive_command` transmits Write-Ahead Logs to encrypted object storage immediately upon segment rotation (every 16MB or max 5 minutes).
2. **Daily Physical Base Backups:** Performed via `pg_basebackup` every night at 02:00 AM UTC with LZ4 compression.
3. **Weekly Cold Snapshot:** Full cloud block-level snapshot of database volumes retained for 7 years to satisfy GxP / 21 CFR Part 11 records retention mandates.

### 3.2 Point-In-Time Recovery (PITR) Procedure
In the event of accidental administrative corruption or catastrophe, the database can be restored to any arbitrary second:

```bash
# 1. Stop PostgreSQL service
systemctl stop postgresql

# 2. Restore latest clean base backup to data directory
tar -xvf /backup/base_backup_20261103.tar.lz4 -C /var/lib/postgresql/16/main/

# 3. Configure recovery target timestamp in postgresql.conf:
cat <<EOF >> /var/lib/postgresql/16/main/postgresql.conf
restore_command = 'aws s3 cp s3://labrox-wal-archive/%f %p'
recovery_target_time = '2026-11-03 14:22:15 UTC'
recovery_target_action = 'promote'
EOF

# 4. Start PostgreSQL to trigger automated WAL roll-forward
systemctl start postgresql
```

---

## 4. Disaster Recovery Scenarios & Execution Playbooks

### Playbook A: Primary Database Crash (Automated)
- **Detection:** Patroni heartbeat loss (> 10s).
- **Automated Action:**
  1. Standby node in AZ-2 acquires leader lock.
  2. Standby promoted to read/write primary.
  3. Spring Cloud / HikariCP connection pool automatically reconnects to new primary IP.
- **RTO:** $< 30$ seconds. **RPO:** $0$ seconds.

### Playbook B: Entire Primary Region Outage (Geo-Failover)
- **Trigger:** Cloud provider catastrophic failure across primary data center.
- **Manual Action:**
  1. Declare disaster; notify Laboratory Director and Operations Lead.
  2. Direct Route 53 / Cloudflare DNS to DR secondary region.
  3. Promote DR PostgreSQL standby using latest synced WAL archive.
  4. Launch LabRox backend microservices stack in secondary region via Terraform / Helm.
  5. Execute automated smoke tests and verify 21 CFR Part 11 hash integrity.
- **RTO:** $< 25$ minutes. **RPO:** $\le 3$ minutes.

---

## 5. Annual Disaster Recovery Drill Schedule
1. **Q1:** Unannounced primary node power-kill test (validate Patroni automated failover).
2. **Q2:** PITR restoration exercise into isolated sandbox environment.
3. **Q3:** MinIO object storage ransomware simulation & immutable WORM recovery test.
4. **Q4:** Full-scale Geo-Region cutover rehearsal.
