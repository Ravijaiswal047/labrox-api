# LabRox Enterprise LIMS - STRIDE Threat Model & Security Specification

## 1. Compliance Standard
- **21 CFR Part 11**: Electronic Records, Electronic Signatures, Immutable Audit Trails.
- **OWASP ASVS Level 2**: Identity, Access Control, Cryptography, Data Protection, API Security.
- **HIPAA**: Protected Health Information (PHI) encryption at rest and in transit.

---

## 2. STRIDE Threat Analysis per Microservice

### S — Spoofing Identity
- **Threat**: Attackers intercepting or forging credentials to impersonate pathologists or sign test results.
- **Mitigation**:
  - Argon2id password hashing with unique per-user salts.
  - Short-lived JWT bearer tokens (15-minute expiration) signed via HS512 / RS256.
  - 21 CFR Part 11 compliant re-authentication prompt required for all diagnostic approvals.
  - Multi-factor authentication (MFA / OTP) step before high-privilege actions.

### T — Tampering with Data
- **Threat**: Malicious modification of patient diagnostic values, reference ranges, or billing entries.
- **Mitigation**:
  - Append-only hash-chained audit trail (`audit_trail_events`) with SHA-256 signatures per entry.
  - Versioned result entry: updates create audit records rather than overwriting historical rows.
  - Immutable report archiving in MinIO/S3 with checksum verification.

### R — Repudiation
- **Threat**: A medical doctor claiming they did not authorize a released diagnostic report.
- **Mitigation**:
  - Cryptographic digital signing on approval (`LegacyDoctorApprovalController`).
  - Strict capture of Doctor Employee ID, timestamp, client IP, user-agent, and digital signature hash embedded in the generated PDF.

### I — Information Disclosure
- **Threat**: Leaking patient demographics, medical history, or diagnostic results to unauthorized parties.
- **Mitigation**:
  - Role-Based Access Control (RBAC) + Attribute-Based Access Control (ABAC) filtered at Gateway and JPA repository level.
  - Masking of phone numbers and patient identifiers on public screens.
  - TLS 1.3 enforced for all microservice communications.

### D — Denial of Service (DoS)
- **Threat**: Flooding result search or report generation to exhaust server resources.
- **Mitigation**:
  - Gateway rate limiting with Redis token bucket algorithm.
  - Streaming PDF generation with bounded memory buffers (OpenPDF / JasperReports).
  - Virtualized server-side pagination for heavy queries (e.g. `PatientSearch`, `GetResultEntry`).

### E — Elevation of Privilege
- **Threat**: Phlebotomist or technician modifying their JWT claims to gain SuperAdmin or Approver rights.
- **Mitigation**:
  - Stateless server-side claim re-validation in `@PreAuthorize` security interceptors.
  - Strict separation of IAM database (`iam_schema`) from clinical results (`diagnostic_schema`).
