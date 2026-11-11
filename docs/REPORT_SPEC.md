# LabRox Clinical Diagnostic Report Specification
## Exact Labrox Diagnostics Reference Format

**Document ID:** SPEC-REP-LABROX-20261111  
**Format Standard:** Labrox Diagnostics Reference Format (matching sample report)  
**Standard Compliance:** NABL (ISO 15189:2022), CAP, 21 CFR Part 11, ICMR Guidelines, IT Act 2000 Section 65B  
**Target Output:** Pixel-Perfect A4 High-Density Vector Clinical Laboratory Report PDF  
**Implementation Service:** `labrox-reporting-service` (`PdfReportGeneratorService.java`)  
**Status:** APPROVED & IN PRODUCTION  

---

## 1. Page Geometry & Layout Dimensions

| Parameter | Specification | Point Equivalent | Metric Equivalent |
|---|---|---|---|
| **Page Size** | ISO A4 (Portrait) | $595.0 \times 842.0 \text{ pt}$ | $210 \times 297 \text{ mm}$ |
| **Left Margin** | Clean Document Margin | $34.0 \text{ pt}$ | $12.0 \text{ mm}$ |
| **Right Margin** | Clean Document Margin | $34.0 \text{ pt}$ | $12.0 \text{ mm}$ |
| **Top Margin** | Header Alignment Margin | $24.0 \text{ pt}$ | $8.5 \text{ mm}$ |
| **Bottom Margin** | Grounded Running Footer Area | $48.0 \text{ pt}$ | $17.0 \text{ mm}$ |
| **Printable Width** | Usable Content Area | $527.0 \text{ pt}$ | $186.0 \text{ mm}$ |

---

## 2. Visual Architecture & Component Layout

### 2.1 Hallmark Clinical Color Palette
- **Teal Primary (`#007A78` / RGB: 0, 122, 120):** Primary brand title, ribbon background, footer background.
- **Teal Light (`#00A8A0` / RGB: 0, 168, 160):** Faint repeated background text in ribbon.
- **Cyan Accent (`#00B4D8` / RGB: 0, 180, 216):** "Laboratory Test Report" rounded pill badge and footer map pin.
- **Red Tagline (`#B91C1C` / RGB: 185, 28, 28):** "Dedicated To Quality. Committed To Care." subtitle.
- **Grey Swoosh (`#6E7882` / RGB: 110, 120, 130):** Elegant vector arc swoosh across the top header.
- **Dark Border (`#3C3C3C` / RGB: 60, 60, 60):** $0.8\text{ pt}$ rectangular border for the patient demographics box.
- **Hairline Divider (`#B4B4B4` / RGB: 180, 180, 180):** $0.8\text{ pt}$ top and bottom horizontal rules for the results table.
- **Vertical Watermark (`#D7DCE1` / RGB: 215, 220, 225):** Faint $18\text{ pt}$ uppercase vertical watermark running along the right margin.

### 2.2 Top Header Section
- **Left Column:**
  - Logo Box: `✦ LABROX` in $22\text{ pt}$ bold teal (`#007A78`).
  - Sub-brand: `DIAGNOSTICS` in $9.5\text{ pt}$ bold teal.
  - Tagline: `Dedicated To Quality. Committed To Care.` in $6.2\text{ pt}$ bold red.
  - Full Legal Entity: `Labrox Diagnostics Pvt. Ltd.` in $14\text{ pt}$ bold teal.
- **Right Column:**
  - Phone: `☎  011 42646464, +91-9899090037`
  - Toll Free: `☎  Toll Free No - 1800 123 4385`
  - Email: `✉  info@labroxdiagnostics.com`
- **Vector Swoosh:** Curved arc spanning from the brand logo over to the contact coordinates.
- **Teal Ribbon Bar:** Repeated `Labrox Diagnostics Pvt. Ltd.` in subtle white/teal with a right-aligned Cyan pill badge: `Laboratory Test Report`.

---

## 3. Patient Demographics & Accession Box

Single rectangular container with $0.8\text{ pt}$ dark border containing a 3-part layout:
- **Left Column (48%):**
  - `Patient Name: Mrs. ANJUMA BIBI`
  - `Age/Gender : 55 Y/Female`
  - `Refer Client : LRPLSL266 SOUVIK ROY`
  - `Refered By : N/A`
  - `Doctor Name: Dr. R KR DARIPA`
  - `SampleType : Serum - SL260287`
- **Middle Column (37%):**
  - `PateintID : LRPL/02206585`
  - `Visit No : SIL2610030424`
  - `Collected On : 03/Oct/2026 08:56 PM`
  - `Received On : 03/Oct/2026 09:13 PM`
  - `Reported Date : 03/Oct/2026 10:25 PM`
- **Right Column (15%):**
  - High-density vector Code 128 barcode stacked horizontally on the right edge.

---

## 4. Results Table & Department Structure

- **Department Header:** Centered bold uppercase (e.g. `BIOCHEMISTRY`, `IMMUNOLOGY`) with a solid black underline.
- **4-Column Observation Table:**
  - `Test Name` (Left, 40%)
  - `Result` (Center, 18%, Bold)
  - `Unit` (Left, 16%)
  - `Bio. Ref. Interval` (Left, 26%)
- **Table Styling:**
  - Top & bottom single grey horizontal hairline ($0.8\text{ pt}$).
  - Clean white space between columns (zero vertical lines).
  - Centered bold underlined test group title (e.g. `CREATININE-SERUM`, `THYROID STIMULATING HORMONE (TSH)`).
  - Test name with secondary method subtitle in small grey font directly underneath (e.g. `Jaffe Kinetic`, `ECLIA`).

---

## 5. Watermark, Signatures & Grounded Footer

1. **Right Margin Vertical Watermark:**
   - Text: `LABROX DIAGNOSTICS PVT LTD`
   - Position: Running vertically along the right page margin at $90^\circ$ rotation in faint grey (`#D7DCE1`).
2. **Signatures Block:**
   - **Left:** Consultant Pathologist (`DR. S. BHATTACHARJEE, MBBS, MD PATHOLOGY, Reg. No. WBMC69081`).
   - **Center:** Consultant Biochemist with digital signature scribble (`DR. RITAM BANERJEE, MBBS, MD BIOCHEMISTRY, Reg. No. WBMC65404`).
   - **Right:** High-resolution vector QR Code (generated via ZXing) + dynamic `Page X of Y` directly underneath.
3. **Bottom Footer Bar:**
   - Solid Teal banner (`#007A78`) across the bottom edge.
   - Left: Location map pin icon with `Find us on`.
   - Center: `Labrox Diagnostics Pvt. Ltd.` (Bold White).
   - Address Line 1: `Regional Lab Siliguri - 1st Floor, Mudhar Arcade, Guru Nanak Sarani-3, Punjabi Para, Siliguri, Pincode : 734001`.
   - Address Line 2: `Central Lab - New Delhi, Other Regional Labs - J&K | Haryana | Rajasthan | U.P. | Maharashtra | Kolkata`.
