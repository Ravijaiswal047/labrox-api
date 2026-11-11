# LabRox Clinical Diagnostic Report Specification

**Document ID:** SPEC-REP-20261111  
**Standard Compliance:** NABL (ISO 15189:2022), CAP (College of American Pathologists), 21 CFR Part 11, ICMR Guidelines  
**Target Output:** Pixel-Perfect A4 High-Density Vector Clinical Laboratory Report PDF  
**Implementation Service:** `labrox-reporting-service` (`PdfReportGeneratorService.java`)  
**Status:** APPROVED & IN PRODUCTION  

---

## 1. Page Geometry & Layout Dimensions

| Parameter | Specification | Point Equivalent | Metric Equivalent |
|---|---|---|---|
| **Page Size** | ISO A4 (Portrait) | $595.0 \times 842.0 \text{ pt}$ | $210 \times 297 \text{ mm}$ |
| **Left Margin** | Strict Minimum $\ge 15\text{ mm}$ | $42.5 \text{ pt}$ | $15.0 \text{ mm}$ |
| **Right Margin** | Strict Minimum $\ge 15\text{ mm}$ | $42.5 \text{ pt}$ | $15.0 \text{ mm}$ |
| **Top Margin** | Clinical Benchmark Margin | $32.0 \text{ pt}$ | $11.3 \text{ mm}$ |
| **Bottom Margin** | Grounded Running Footer Area | $45.0 \text{ pt}$ | $15.9 \text{ mm}$ |
| **Printable Width** | Usable Content Area | $510.0 \text{ pt}$ | $180.0 \text{ mm}$ |

---

## 2. Color Palette & Typography Hierarchy

### 2.1 Color Palette
- **Deep Clinical Navy (`#1A365D`):** Primary branding, section banners, table header text, signature roles.
- **Sapphire Blue (`#2563EB`):** Secondary highlights, electronic verification hyperlinks.
- **Warm Amber Gold (`#B45309`):** NABL accreditation badge, ISO compliance certifications.
- **Slate Border (`#CBD5E1`):** Structural borders for demographics and results headers ($0.5\text{ pt}$).
- **Subtle Row Border (`#E2E8F0`):** Horizontal row dividing rules.
- **Alternating Row Fill (`#F8FAFC` & `#FFFFFF`):** High-readability zebra striping.
- **Abnormal Panic Red (`#DC2626`):** Out-of-range clinical flags (HIGH, LOW, PANIC) and values.
- **Normal Green (`#166534`):** In-range clinical flags (NORMAL).
- **Dark Neutral Text (`#0F172A`):** Observed values and patient information.
- **Muted Neutral Text (`#64748B`):** Units, methodology notes, running legal disclaimers.

### 2.2 Typography Hierarchy (Standard Helvetica)
- **Facility Letterhead Title:** $14.5\text{ pt}$ Bold (`#1A365D`), Center.
- **Accreditation Subtitle:** $8.0\text{ pt}$ Bold (`#B45309`), Center.
- **Facility Address & Contact:** $7.2\text{ pt}$ Regular (`#64748B`), Center.
- **Department & Test Banner:** $9.5\text{ pt}$ Bold (`#FFFFFF`), Center, with `#1A365D` solid background.
- **Demographic Field Labels:** $7.5\text{ pt}$ Bold (`#1A365D`), Left.
- **Demographic Values:** $8.0\text{ pt}$ Regular / $8.5\text{ pt}$ Bold (`#0F172A`), Left.
- **Results Table Header:** $7.5\text{ pt}$ Bold (`#1A365D`), Uppercase.
- **Observed Results:** $8.5\text{ pt}$ Bold (`#0F172A` normal / `#DC2626` abnormal).
- **Units & Reference Intervals:** $8.0\text{ pt}$ Regular (`#334155`).
- **Status / Flag Badge:** $7.2\text{ pt}$ Bold (`#166534` normal / `#DC2626` abnormal).
- **Methodology & Remarks:** $7.0\text{ pt}$ Regular (`#64748B`).
- **Digital Signatures:** $8.0\text{ pt}$ Bold (`#0F172A`), $6.8\text{ pt}$ Regular (`#64748B`).
- **Grounded Running Footer & Page Numbering:** $7.5\text{ pt}$ Regular (`#64748B`).

---

## 3. Results Grid Table Specification

The clinical observation grid is rendered using an auto-repeating header table (`setHeaderRows(1)`), ensuring multi-page reports maintain full column context.

```
+------------------------------------+------------------+--------------+-----------------------+--------------+
| TEST INVESTIGATION / PARAMETER     |  OBSERVED VALUE  |     UNIT     |  REFERENCE INTERVAL   | STATUS/FLAG  |
| (Left-aligned, 34%)                | (Right-al, 16%)  | (Center, 14%)|     (Center, 22%)     | (Center, 14%)|
+------------------------------------+------------------+--------------+-----------------------+--------------+
| Hemoglobin (Hb)                    |          11.2  ▼ | g/dL         | 13.0 - 17.0           |     LOW      |
| Total Leukocyte Count (WBC)        |         14500  ▲ | /cumm        | 4000 - 11000          |     HIGH     |
| Platelet Count                     |        210000    | /cumm        | 150000 - 450000       |    NORMAL    |
+------------------------------------+------------------+--------------+-----------------------+--------------+
```

### Column Alignments & Formatting Rules:
1. **Column 1 — Test Investigation / Parameter:** Left-aligned ($34\%$), padding left $6\text{ pt}$. Autowraps long analyte names without clipping.
2. **Column 2 — Observed Value:** Strict Right-aligned ($16\%$), padding right $6\text{ pt}$. Formatted with high-contrast bold typography and dynamic directional indicators (`▲` for HIGH, `▼` for LOW).
3. **Column 3 — Unit of Measure:** Strict Center-aligned ($14\%$). Standardized SI and conventional clinical units (`g/dL`, `/cumm`, `mg/dL`, `%`, `fL`).
4. **Column 4 — Biological Reference Interval:** Strict Center-aligned ($22\%$). Age- and gender-stratified ranges.
5. **Column 5 — Status / Flag:** Strict Center-aligned ($14\%$). Color-coded clinical status capsule.

---

## 4. Multi-Page Overflow & Dynamic Page Numbering

Multi-page reports (e.g. 24-analyte Comprehensive Metabolic Panels) feature:
1. **Repeated Table Header:** `table.setHeaderRows(1)` automatically duplicates column headers across subsequent pages.
2. **Dynamic "Page X of Y":** Generated via OpenPDF `PdfPageEventHelper` using a two-pass `PdfTemplate` XObject. Accurately renders total page counts (e.g. `Page 1 of 2`, `Page 2 of 2`) on the bottom right of every page without hardcoding.
3. **Running Quality Disclaimer:** Running on the bottom left across all pages:
   `LabRox Enterprise LIS | NABL (ISO 15189:2022) | 21 CFR Part 11 Validated`
4. **Signatures Pinned to Final Page:** The 3-column verification block (Technologist QC, 21 CFR Part 11 Electronic Token & Hash, Authorized Signatory Doctor) and conditions of reporting are rendered on the terminal page.

---

## 5. Verification Test Cases

| Case | Test Name | Parameters | Pages | Output File | Verification |
|---|---|---|---|---|---|
| **1** | Complete Blood Count (CBC) | 13 analytes | 1 Page | `test_case_1_cbc.pdf` | Abnormal indicators (`▲`, `▼`), tight margins |
| **2** | Lipid Profile Extended | 6 analytes | 1 Page | `test_case_2_lipid.pdf` | Risk stratification intervals, calculated ratios |
| **3** | Master Metabolic Panel | 24 analytes | 2 Pages | `test_case_3_multipage.pdf` | Repeated table headers, `Page 1 of 2` & `Page 2 of 2` |

---

## 6. Regulatory Verification Checklist

- [x] **ISO 15189:2022 §5.8:** Clear identification of patient, accession number, specimen type, collection date/time, and reporting date/time.
- [x] **21 CFR Part 11 §11.50:** Digital signature manifest with signatory name, qualification, registration number, verification token, and SHA-256 hash.
- [x] **CAP All Common Checklist:** Clear biological reference intervals and unambiguous flagging of abnormal values.
- [x] **Print Margins:** All margins $\ge 15\text{ mm}$ ($42.5\text{ pt}$).
- [x] **Zero White Gap Dead Space:** Content sections dynamically spaced and balanced to eliminate trailing dead whitespace.
