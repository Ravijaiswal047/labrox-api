# ADR-002: Preservation of Legacy Response Envelopes & OWASP ASVS L2 Hardening

## Status
Accepted

## Context
The legacy React frontend (`fe_v9`) expects specific response envelopes: `{ status: true, message: [...] }` or `{ success: true, data: [...] }` with PascalCase model keys. Redesigning client data parsing across hundreds of components would introduce severe regression risks.

## Decision
1. Standardize all Spring Boot controllers on the legacy response envelope format.
2. Upgrade security layer to OWASP ASVS Level 2:
   - Argon2id password hashing replacing plain SHA-256.
   - JWT tokens signed with HS512.
   - 21 CFR Part 11 electronic signatures for medical result validation.
   - Tamper-evident hash-chained audit log for all clinical events.

## Consequences
- Guaranteed zero frontend regression.
- Enterprise-grade regulatory compliance suitable for CAP/NABL laboratory accreditation.
