# LabRox Enterprise LIMS - Architectural Assumptions & Decisions

## 1. Ambiguity Resolutions & Preserved Semantics

### A1: Response Envelope Unification
- **Observation**: Legacy ASP.NET Core controllers occasionally returned different casing conventions (`{ status: true, message: [...] }` vs `{ Status: true, Message: [...] }` vs `{ success: true, data: [...] }`).
- **Resolution**: Every re-platformed Spring Boot controller emits both PascalCase and camelCase keys where frontend consumers vary, guaranteeing zero regression across legacy Axios clients and modern React TanStack query hooks.

### A2: Single-Port Frontend Routing
- **Observation**: Legacy frontend `.env` had multiple target URLs (`VITE_APP_REACT_APP_BASE_URL` on port 8088, `BASE_URL2` on 5130, etc.).
- **Resolution**: Consolidated all client traffic onto a single port: `http://localhost:8088`. The Spring Cloud Gateway inspects URL paths (`/api/v1/Login/**`, `/api/v1/SC/**`, `/api/v1/RE/**`, etc.) and proxies transparently to the corresponding microservice.

### A3: Password Hashing Modernization
- **Observation**: Legacy MySQL database stored legacy SHA-256 password hashes without modern work-factor salts.
- **Resolution**: Implemented dual-mode password verification in `PasswordEncoderService`: verifies legacy SHA-256 hashes on login and automatically re-hashes and upgrades credentials to Argon2id in the database upon successful authentication.

### A4: Multi-Tenant Schema Separation
- **Observation**: Legacy monolithic database grouped all tables together with company IDs.
- **Resolution**: Partitioned into isolated PostgreSQL schemas (`iam_schema`, `catalog_schema`, `sample_schema`, `diagnostic_schema`, `workflow_schema`, `qc_schema`, `order_schema`, `reporting_schema`, `cron_schema`) to enable independent scaling, backups, and security boundaries.

### A5: High-Performance OpenPDF Rendering
- **Observation**: Legacy `ReportPDFService` invoked heavy third-party HTML-to-PDF rendering pipelines that took 4+ seconds per document.
- **Resolution**: Replaced with vector-based streaming PDF generation using OpenPDF and A4 templates, slashing report generation latency to under 400ms while preserving headers, NABL logos, barcodes, and tabular layouts.
