# LabRox Enterprise LIMS — API Microservices (`labrox-api`)

[![CI/CD](https://github.com/Ravijaiswal047/labrox-api/actions/workflows/ci.yml/badge.svg)](https://github.com/Ravijaiswal047/labrox-api/actions)
[![Compliance](https://img.shields.io/badge/21%20CFR%20Part%2011-Compliant-blue.svg)](docs/ARCHITECTURE.md)
[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://openjdk.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

A cloud-native, high-throughput Laboratory Information Management System (LIMS) backend engineered for modern diagnostic chains, reference laboratories, and hospital networks. Built with Java 21, Spring Boot 3, PostgreSQL, Redis, and MinIO under 21 CFR Part 11 and GxP standards.

---

## ⚡ 5-Command Quickstart

```bash
# 1. Clone repository
git clone https://github.com/Ravijaiswal047/labrox-api.git && cd labrox-api

# 2. Setup environment configuration
cp .env.example .env

# 3. Boot infrastructure dependencies (PostgreSQL, Redis, MinIO)
docker compose up -d

# 4. Build and test microservices
mvn clean verify -DskipTests=false

# 5. Start unified API Gateway and core services
./scripts/bootstrap.sh
```

---

## 🏛️ Microservices Architecture

| Service | Port | Description |
| :--- | :--- | :--- |
| **labrox-gateway-service** | `8088` | Spring Cloud Gateway (Single unified entry point, CORS, Rate Limiting) |
| **labrox-iam-service** | `8081` | Authentication, Argon2id passwords, JWT tokens, RBAC, Menus, Centers |
| **labrox-test-catalog-service** | `8082` | Test profiles, investigation packages, reference ranges, rate lists |
| **labrox-sample-service** | `8083` | Phlebotomy barcode queue, sample collection, SRA sorting, Dept Receive |
| **labrox-diagnostic-service** | `8084` | Clinical parameter result entry, delta checks, analyzer interfaces |
| **labrox-qc-service** | `8085` | Westgard 6-rule engine, Levey-Jennings QC charts, control lot tracking |
| **labrox-approval-service** | `8086` | 21 CFR Part 11 electronic signatures, auto-approval rules, status changes |
| **labrox-order-service** | `8089` | Patient UHID registration, work order booking, cashier POS billing |
| **labrox-reporting-service** | `8091` | High-fidelity A4 PDF clinical reports, invoice receipts, worklists |
| **labrox-cron-orchestrator** | `8087` | Clustered Quartz background tasks, SMS/WhatsApp queues, TAT monitor |
| **labrox-common** | Library | Shared DTOs, security filters, audit aspects, exception handling |

---

## 📖 Technical Documentation

- [System Architecture Specification](docs/ARCHITECTURE.md)
- [Legacy Feature Parity Matrix](docs/PARITY_MATRIX.md)
- [Architectural Optimizations](docs/OPTIMIZATIONS.md)
- [Threat Model & Security Hardening](docs/THREAT_MODEL.md)
- [Operational Runbook](docs/RUNBOOK.md)
- [Disaster Recovery & Business Continuity](docs/DISASTER_RECOVERY.md)
- [Git Workflow & Branching Guidelines](docs/GITHUB_WORKFLOW.md)

---

## 🔒 Security & Compliance

This platform enforces FDA 21 CFR Part 11 requirements including immutable audit trails (`labrox-common`), cryptographic commit and tag signing, and automated CodeQL and dependency scanning. Report vulnerabilities privately to `security@labrox.io`.
