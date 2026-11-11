# ADR-001: Decomposition of Monolithic .NET LIMS into Domain Microservices

## Status
Accepted

## Context
The legacy LIMS was a monolithic .NET 8 / C# application containing 203 controllers and direct SQL queries to an AWS RDS MySQL database. Peak hours caused thread exhaustion during bulk report generation and delta-check calculations, degrading registration and result entry throughput.

## Decision
Decompose the system into domain-driven microservices:
1. `labrox-gateway-service`: API Gateway & routing (Port 8088)
2. `labrox-iam-service`: Identity, Authentication, Menus, Centers (Port 8081)
3. `labrox-test-catalog-service`: Master Catalog, Rate Lists (Port 8082)
4. `labrox-sample-service`: Phlebotomy, Accessioning, Rejections (Port 8083)
5. `labrox-diagnostic-service`: Diagnostic Result Entry, Delta Checks (Port 8084)
6. `labrox-qc-service`: Westgard Rules, IQC, Levey-Jennings (Port 8085)
7. `labrox-approval-service`: Doctor Approvals, 21 CFR Part 11 Signatures (Port 8086)
8. `labrox-cron-orchestrator`: Clustered Quartz Automations (Port 8087)
9. `labrox-order-service`: Patient Registration, POS Billing (Port 8089)
10. `labrox-reporting-service`: A4 Diagnostic PDF Reports & Invoices (Port 8091)

## Consequences
- Independent scaling of CPU-intensive reporting without starving real-time cashier registration.
- Resilient domain failure boundaries: if notification or reporting is busy, result entry continues unabated.
