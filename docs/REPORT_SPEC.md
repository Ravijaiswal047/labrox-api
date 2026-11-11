# LabRox Clinical Diagnostic Report Specification
## City X-Ray & Scan Clinic / Star Imaging Standard Format

**Document ID:** SPEC-REP-CITYXRAY-20261111  
**Format Standard:** City X-Ray & Scan Clinic / Star Imaging & Path Lab Reference Architecture  
**Standard Compliance:** NABL (ISO 15189:2022), CAP (College of American Pathologists), 21 CFR Part 11, ICMR Guidelines, IT Act 2000 Section 65B  
**Target Output:** Pixel-Perfect A4 High-Density Vector Clinical Laboratory Report PDF  
**Implementation Service:** `labrox-reporting-service` (`PdfReportGeneratorService.java`)  
**Status:** APPROVED & IN PRODUCTION  

---

## 1. Page Geometry & Layout Dimensions

| Parameter | Specification | Point Equivalent | Metric Equivalent |
|---|---|---|---|
| **Page Size** | ISO A4 (Portrait) | $595.0 \times 842.0 \text{ pt}$ | $210 \times 297 \text{ mm}$ |
| **Left Margin** | ISO/NABL Compliant Margin | $36.0 \text{ pt}$ | $12.7 \text{ mm}$ |
| **Right Margin** | ISO/NABL Compliant Margin | $36.0 \text{ pt}$ | $12.7 \text{ mm}$ |
| **Top Margin** | Header Alignment Margin | $28.0 \text{ pt}$ | $10.0 \text{ mm}$ |
| **Bottom Margin** | Grounded Running Footer Area | $40.0 \text{ pt}$ | $14.1 \text{ mm}$ |
| **Printable Width** | Usable Content Area | $523.0 \text{ pt}$ | $184.6 \text{ mm}$ |

---

## 2. City X-Ray & Star Imaging Visual Architecture

### 2.1 Hallmark Clinical Color Palette
- **City Royal Navy (`#003366` / RGB: 0, 51, 102):** Primary corporate brand identity, header ribbon, table column headers, department bar.
- **Medical Cyan (`#00A3E0` / RGB: 0, 163, 224):** Secondary accent ribbon, electronic verification highlights, badge accents.
- **Star Crimson (`#990000` / RGB: 153, 0, 0):** Secondary emergency / alert accent.
- **Accreditation Gold (`#C27803` / RGB: 194, 120, 3):** NABL accreditation badge, ISO 15189 compliance stars.
- **Identity Card Background (`#F6F9FC`):** Light slate background for the patient accession card.
- **Structural Border Slate (`#B4C8DC`):** Crisp medical border for the patient card and observation table.
- **Row Dividing Hairline (`#E1E8F0`):** Ultra-thin $0.5\text{ pt}$ horizontal dividers between observations (no cluttered vertical lines).
- **Subtle Ice Tint (`#FAFCFF`):** Alternating row background for optimal readability.
- **Section Sub-header Tint (`#EDF2F7`):** Background for table sub-panels (e.g. `DIFFERENTIAL LEUCOCYTE COUNT (DLC)`).
- **Clinical Flag High (`#B91C1C`):** Bold red text and capsule badges for abnormal high values (`▲ (H)`).
- **Clinical Flag Low (`#C2410C`):** Bold amber/orange text and capsule badges for abnormal low values (`▼ (L)`).
- **Clinical Flag Normal (`#166534`):** Emerald green badge for normal in-range results.

### 2.2 Typography Hierarchy
- **Header Facility Title:** $15.0\text{ pt}$ Bold Navy (`#003366`), Left.
- **Accreditation Badge:** $7.5\text{ pt}$ Bold Gold (`#C27803`), Left.
- **Tagline & Coordinates:** $7.2 - 7.5\text{ pt}$ Muted Slate (`#5A697D`), Right-aligned helpline and address.
- **Patient Demographics Labels:** $7.5\text{ pt}$ Bold Navy (`#003366`), Left.
- **Patient Demographics Values:** $8.0\text{ pt}$ Regular / $8.5\text{ pt}$ Bold (`#0F172A`), Left.
- **Department Header Ribbon:** $9.2\text{ pt}$ Bold White on Solid Navy (`#003366`).
- **Investigation Title:** $10.5\text{ pt}$ Bold Navy (`#003366`), Center, with technology subtitle.
- **Results Table Header:** $7.5\text{ pt}$ Bold White on Navy (`#003366`), Uppercase.
- **Observed Results:** $8.5\text{ pt}$ Bold (`#0F172A` normal / `#B91C1C` high / `#C2410C` low).
- **Units & Biological Reference Intervals:** $8.0\text{ pt}$ Regular (`#0F172A`).
- **Clinical Status Badges:** $7.0\text{ pt}$ Bold in dedicated colored capsules.
- **DLC Section Dividers:** $7.8\text{ pt}$ Bold Oblique Navy on Slate (`#EDF2F7`).
- **Methodology & Interpretation:** $7.2\text{ pt}$ Regular Slate.
- **3-Tier Multi-Signature:** $8.2\text{ pt}$ Bold Dark, $7.0\text{ pt}$ Muted Slate.
- **Grounded Running Footer & Page Counter:** $7.2\text{ pt}$ Regular Slate.

---

## 3. Results Grid Table Specification

The clinical observation grid is rendered using an auto-repeating header table (`setHeaderRows(1)`), ensuring multi-page reports maintain full column context.

```
+------------------------------------+------------------+--------------+-----------------------+--------------+
| TEST INVESTIGATION / PARAMETER     |  OBSERVED VALUE  |     UNIT     |  REFERENCE INTERVAL   |CLINICAL STATUS
| (Left-aligned, 35%)                | (Right-al, 18%)  | (Center, 12%)|     (Center, 22%)     | (Center, 13%)|
+------------------------------------+------------------+--------------+-----------------------+--------------+
| Hemoglobin (Hb)                    |      11.2  ▼ (L) | g/dL         | 13.0 - 17.0           |     LOW      |
| Total Leukocyte Count (WBC)        |     14500  ▲ (H) | /cumm        | 4000 - 11000          |     HIGH     |
| Platelet Count                     |    210000        | /cumm        | 150000 - 450000       |    NORMAL    |
| Packed Cell Volume (PCV)           |      34.5  ▼ (L) | %            | 40.0 - 50.0           |     LOW      |
+------------------------------------+------------------+--------------+-----------------------+--------------+
| DIFFERENTIAL LEUCOCYTE COUNT (DLC)                                                                          |
+------------------------------------+------------------+--------------+-----------------------+--------------+
|     Neutrophils                    |        78  ▲ (H) | %            | 40 - 75               |     HIGH     |
|     Lymphocytes                    |        18  ▼ (L) | %            | 20 - 45               |     LOW      |
|     Eosinophils                    |         2        | %            | 1 - 6                 |    NORMAL    |
+------------------------------------+------------------+--------------+-----------------------+--------------+
```

### Column Alignments & Formatting Rules:
1. **Column 1 — Test Investigation / Parameter:** Left-aligned ($35\%$), padding left $6\text{ pt}$. Autowraps long analyte names cleanly. DLC components are indented with 4 spaces under the DLC sub-header.
2. **Column 2 — Observed Value:** Strict Right-aligned ($18\%$), padding right $6\text{ pt}$. Formatted with bold typography and clear directional indicators (`▲ (H)` for HIGH, `▼ (L)` for LOW).
3. **Column 3 — Unit of Measure:** Strict Center-aligned ($12\%$). Standardized SI and conventional clinical units (`g/dL`, `/cumm`, `mg/dL`, `%`, `fL`).
4. **Column 4 — Biological Reference Interval:** Strict Center-aligned ($22\%$). Age- and gender-stratified ranges.
5. **Column 5 — Clinical Status:** Strict Center-aligned ($13\%$). Color-coded capsule badge (`NORMAL`, `HIGH`, `LOW`).

---

## 4. Multi-Tier Signatures & Security Verification (City X-Ray Standard)

City X-Ray & Star Imaging reports feature a **3-Tier Multi-Signature block** directly above the conditions of reporting:
1. **Performed & Analyzed By:** Medical Lab Technologist (`P. Ramesh, DMLT, B.Sc. MLT`).
2. **Checked & Validated By:** Quality Manager & Senior Biochemist (`Dr. Sunita Rao, M.Sc., Ph.D.`).
3. **Authorized & Reported By:** Senior Consultant Pathologist & HOD (`Dr. Rajesh Sharma, MD Pathology, Reg No: DMC-48291`).

---

## 5. Verification Test Cases

| Case | Test Name | Parameters | Pages | Output File | Verification |
|---|---|---|---|---|---|
| **1** | Complete Blood Count (CBC) | 13 analytes | 1 Page | `test_case_1_cbc.pdf` | Abnormal flags (`▲ (H)`, `▼ (L)`), DLC sub-panel, Code128 vector barcode |
| **2** | Comprehensive Lipid Profile | 6 analytes | 1 Page | `test_case_2_lipid.pdf` | Direct & calculated analytes, risk ratios, 3-tier signatures |
| **3** | Master Metabolic Panel | 24 analytes | 2 Pages | `test_case_3_multipage.pdf` | Auto-repeated headers, dynamic `Page 1 of 2` and `Page 2 of 2` |

---

## 6. Regulatory Verification Checklist

- [x] Left-align test and analyte names ($35\%$ width)
- [x] Right-align numeric results with bold styling & `▲ (H)` / `▼ (L)` flags ($18\%$ width)
- [x] Center-align units ($12\%$ width)
- [x] Center-align biological reference intervals ($22\%$ width)
- [x] Center-align color-coded clinical status badges ($13\%$ width)
- [x] Print margins $\ge 12.7\text{ mm}$ ($36\text{ pt}$) with zero bottom dead whitespace
- [x] Repeat table headers across page breaks (`table.setHeaderRows(1)`)
- [x] Two-pass dynamic page numbering ("Page X of Y") via `CityXRayFooterEvent`
- [x] Pathologist, Quality Manager, and Technologist signatures anchored to the final page
- [x] Native vector Code 128 barcode embedded in the patient identity card
