# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - 2026-11-11

### Added
- Cloud-native Java 21 / Spring Boot 3 microservices backend.
- High-performance Spring Cloud Gateway (`labrox-gateway-service`) on port 8088.
- 21 CFR Part 11 compliant audit logging, electronic signatures, and IAM service (`labrox-iam-service`).
- Clinical order and patient UHID registration service (`labrox-order-service`).
- Phlebotomy barcode accessioning, SRA sorting, and department receive (`labrox-sample-service`).
- Multi-parameter clinical diagnostic result entry and validation (`labrox-diagnostic-service`).
- Westgard 6-rule quality control and Levey-Jennings charting engine (`labrox-qc-service`).
- Multi-level medical doctor report approval engine (`labrox-approval-service`).
- High-fidelity A4 clinical laboratory PDF report generation (`labrox-reporting-service`).
- Master test catalog and dynamic reference ranges (`labrox-test-catalog-service`).
- Clustered background Quartz task scheduler (`labrox-cron-orchestrator`).
- Complete Docker Compose local stack with PostgreSQL, Redis, and MinIO.
