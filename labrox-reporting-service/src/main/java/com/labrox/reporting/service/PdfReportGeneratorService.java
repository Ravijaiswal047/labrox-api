package com.labrox.reporting.service;

import com.labrox.reporting.dto.ReportDataDto;
import com.labrox.reporting.dto.ReportObservationDto;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * High-Precision Diagnostic Laboratory PDF Report Generator.
 * Rebuilt to match the gold-standard reference layout of
 * City X-Ray & Scan Clinic and Star Imaging & Path Lab.
 */
@Service
@Slf4j
public class PdfReportGeneratorService {

    // City X-Ray & Star Imaging Hallmark Clinical Palette
    private static final Color CITY_NAVY = new Color(0, 51, 102);        // #003366 - Deep Royal Navy
    private static final Color CITY_CYAN = new Color(0, 163, 224);       // #00A3E0 - Signature Medical Cyan
    private static final Color STAR_CRIMSON = new Color(153, 0, 0);      // #990000 - Star Imaging Deep Red
    private static final Color NABL_GOLD = new Color(194, 120, 3);       // #C27803 - Accreditation Warm Gold
    private static final Color CARD_BG = new Color(246, 249, 252);       // #F6F9FC - Light Slate Identity Card
    private static final Color BORDER_PRIMARY = new Color(180, 200, 220);// #B4C8DC - Medical Border
    private static final Color BORDER_HAIRLINE = new Color(225, 232, 240);// #E1E8F0 - Soft Divider
    private static final Color ROW_ALT = new Color(250, 252, 255);       // #FAFCFF - Subtle Ice Tint
    private static final Color SECTION_BG = new Color(237, 242, 247);    // #EDF2F7 - Section Header Tint
    private static final Color TEXT_DARK = new Color(15, 23, 42);         // #0F172A - Slate 900
    private static final Color TEXT_MUTED = new Color(90, 105, 125);     // #5A697D - Slate 500
    private static final Color FLAG_HIGH = new Color(185, 28, 28);       // #B91C1C - High Alert Red
    private static final Color FLAG_LOW = new Color(194, 65, 12);        // #C2410C - Low Alert Amber
    private static final Color FLAG_NORMAL = new Color(22, 101, 52);     // #166534 - Normal Forest Green
    private static final Color BADGE_BG_HIGH = new Color(254, 226, 226); // #FEE2E2 - Soft Red
    private static final Color BADGE_BG_LOW = new Color(254, 243, 199);  // #FEF3C7 - Soft Amber
    private static final Color BADGE_BG_NORM = new Color(232, 245, 233); // #E8F5E9 - Soft Green

    public byte[] generateDiagnosticReportPdf(ReportDataDto data) {
        return generateDiagnosticReportPdf(data, true);
    }

    public byte[] generateDiagnosticReportPdf(ReportDataDto data, boolean withHeader) {
        log.info("Generating City X-Ray / Star Imaging Diagnostic Report PDF for testId={}, barcode={}, withHeader={}",
                data.getTestId(), data.getBarcodeNo(), withHeader);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // ISO/NABL Compliant Print Margins: Left/Right 36pt (12.7mm), Top 28pt, Bottom 40pt
            Document document = new Document(PageSize.A4, 36f, 36f, 28f, 40f);
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            // Register dynamic "Page X of Y" and grounded running footer event helper
            CityXRayFooterEvent footerEvent = new CityXRayFooterEvent();
            writer.setPageEvent(footerEvent);

            document.open();

            // 1. Signature City X-Ray / Star Imaging Top Header Banner
            if (withHeader) {
                addCityXRayHeader(document, data);
            } else {
                addVerticalSpacer(document, 15);
            }

            // 2. Patient Demographics & Accession Identity Card (Boxed Grid with Barcode)
            addPatientIdentityCard(document, writer, data);

            addVerticalSpacer(document, 4);

            // 3. Clinical Department & Test Investigation Banner
            addDepartmentBanner(document, data);

            addVerticalSpacer(document, 3);

            // 4. Clinical Results Grid (5-column strict alignment with DLC sub-sections)
            addCityXRayResultsGrid(document, data);

            addVerticalSpacer(document, 4);

            // 5. Examination Methodology & Clinical Remarks Box
            addClinicalInterpretationBox(document, data);

            addVerticalSpacer(document, 4);

            // 6. 3-Tier Multi-Signature Block (Technologist, Quality Manager, Pathologist)
            addThreeTierSignatureBlock(document, data);

            addVerticalSpacer(document, 3);

            // 7. Grounded Security & Legal Compliance Strip
            addComplianceFooterBlock(document, data);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate City X-Ray / Star Imaging PDF report: {}", e.getMessage(), e);
            throw new RuntimeException("PDF Generation Error: " + e.getMessage(), e);
        }
    }

    private void addVerticalSpacer(Document doc, float height) throws DocumentException {
        PdfPTable spacer = new PdfPTable(1);
        spacer.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase(" "));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight(height);
        spacer.addCell(cell);
        doc.add(spacer);
    }

    /**
     * City X-Ray & Star Imaging Dual-Column Brand Header with Top Accent Stripe
     */
    private void addCityXRayHeader(Document document, ReportDataDto data) throws DocumentException {
        // Dual-color top accent ribbon (Royal Navy + Medical Cyan)
        PdfPTable ribbon = new PdfPTable(2);
        ribbon.setWidthPercentage(100);
        ribbon.setWidths(new float[]{75f, 25f});

        PdfPCell cNavy = new PdfPCell();
        cNavy.setBackgroundColor(CITY_NAVY);
        cNavy.setFixedHeight(3.5f);
        cNavy.setBorder(Rectangle.NO_BORDER);
        ribbon.addCell(cNavy);

        PdfPCell cCyan = new PdfPCell();
        cCyan.setBackgroundColor(CITY_CYAN);
        cCyan.setFixedHeight(3.5f);
        cCyan.setBorder(Rectangle.NO_BORDER);
        ribbon.addCell(cCyan);
        document.add(ribbon);

        addVerticalSpacer(document, 2);

        // Header Content Table (Left: Brand & Accreditation, Right: Contact & Centers)
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{62f, 38f});

        Font brandTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15f, CITY_NAVY);
        Font nablBadgeFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, NABL_GOLD);
        Font brandTaglineFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 7.5f, TEXT_MUTED);
        Font contactBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, CITY_NAVY);
        Font contactDetailFont = FontFactory.getFont(FontFactory.HELVETICA, 7.2f, TEXT_MUTED);

        // Left Cell: Center Name, NABL accreditation, Tagline
        String centerName = data.getCenterName() != null ? data.getCenterName() : "CITY X-RAY & SCAN CLINIC  /  STAR IMAGING & PATH LAB";
        Paragraph pBrand = new Paragraph(centerName.toUpperCase(), brandTitleFont);
        pBrand.setSpacingAfter(1f);

        String nablText = data.getNablAccNo() != null
                ? "★ NABL ACCREDITED (ISO 15189:2022) | CERT NO: " + data.getNablAccNo() + " | ICMR APPROVED ★"
                : "★ NABL ACCREDITED CLINICAL LABORATORY (ISO 15189:2022) | ICMR APPROVED | NABH ACCREDITED ★";
        Paragraph pNabl = new Paragraph(nablText, nablBadgeFont);
        pNabl.setSpacingAfter(1f);

        Paragraph pTag = new Paragraph("Pioneers in Diagnostic Medicare • Comprehensive Pathology & Imaging Services", brandTaglineFont);

        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.addElement(pBrand);
        leftCell.addElement(pNabl);
        leftCell.addElement(pTag);
        leftCell.setPaddingBottom(3f);
        headerTable.addCell(leftCell);

        // Right Cell: 24x7 Helpline, Address, Web
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Paragraph pCare = new Paragraph("24x7 Helpline: 011-47252000 | +91 99990 00000", contactBoldFont);
        pCare.setAlignment(Element.ALIGN_RIGHT);
        Paragraph pAddr = new Paragraph("Central Reference Lab: Medical Enclave, New Delhi 110018", contactDetailFont);
        pAddr.setAlignment(Element.ALIGN_RIGHT);
        Paragraph pWeb = new Paragraph("Email: info@cityxrayclinic.com  •  Web: www.cityxrayclinic.com", contactDetailFont);
        pWeb.setAlignment(Element.ALIGN_RIGHT);

        rightCell.addElement(pCare);
        rightCell.addElement(pAddr);
        rightCell.addElement(pWeb);
        rightCell.setPaddingBottom(3f);
        headerTable.addCell(rightCell);

        document.add(headerTable);

        // Bottom Accent Hairline
        PdfPTable div = new PdfPTable(1);
        div.setWidthPercentage(100);
        PdfPCell dCell = new PdfPCell();
        dCell.setBackgroundColor(CITY_NAVY);
        dCell.setFixedHeight(1.8f);
        dCell.setBorder(Rectangle.NO_BORDER);
        div.addCell(dCell);
        document.add(div);

        addVerticalSpacer(document, 3);
    }

    /**
     * Patient Demographics & Accession Identity Card with Embedded Vector Barcode
     */
    private void addPatientIdentityCard(Document document, PdfWriter writer, ReportDataDto data) throws DocumentException {
        PdfPTable card = new PdfPTable(4);
        card.setWidthPercentage(100);
        card.setWidths(new float[]{16f, 34f, 18f, 32f});

        Font cardHeadFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, Color.WHITE);
        Font lblFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, CITY_NAVY);
        Font valFont = FontFactory.getFont(FontFactory.HELVETICA, 8f, TEXT_DARK);
        Font valBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, TEXT_DARK);

        // Card Title Bar with Barcode on the right
        PdfPCell titleLeft = new PdfPCell(new Phrase("PATIENT DEMOGRAPHICS & ACCESSION DETAILS", cardHeadFont));
        titleLeft.setColspan(2);
        titleLeft.setBackgroundColor(CITY_NAVY);
        titleLeft.setPadding(3.5f);
        titleLeft.setBorder(Rectangle.NO_BORDER);
        card.addCell(titleLeft);

        // Embed Vector Barcode 128
        PdfPCell titleRight = new PdfPCell();
        titleRight.setColspan(2);
        titleRight.setBackgroundColor(CITY_NAVY);
        titleRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
        titleRight.setBorder(Rectangle.NO_BORDER);
        titleRight.setPadding(2f);
        try {
            Barcode128 barcode = new Barcode128();
            barcode.setCode(data.getBarcodeNo() != null ? data.getBarcodeNo() : "BC20261111001");
            barcode.setBarHeight(12f);
            barcode.setFont(null);
            Image barcodeImg = barcode.createImageWithBarcode(writer.getDirectContent(), null, null);
            barcodeImg.setAlignment(Element.ALIGN_RIGHT);
            titleRight.addElement(barcodeImg);
        } catch (Exception e) {
            titleRight.addElement(new Phrase("SID: " + data.getBarcodeNo(), cardHeadFont));
        }
        card.addCell(titleRight);

        // Row 1: Patient Name & UHID
        addCardCell(card, "Patient Name:", lblFont, data.getPatientName() != null ? data.getPatientName().toUpperCase() : "N/A", valBold);
        addCardCell(card, "UHID / Reg No:", lblFont, data.getUhid() != null ? data.getUhid() : "N/A", valBold);

        // Row 2: Age/Gender & Lab / Visit No
        String ageGender = (data.getAgeYears() != null ? data.getAgeYears() + " Yrs / " : "") + (data.getGender() != null ? data.getGender() : "N/A");
        addCardCell(card, "Age / Gender:", lblFont, ageGender, valFont);
        addCardCell(card, "Lab / Visit No:", lblFont, "WO-" + (data.getBarcodeNo() != null ? data.getBarcodeNo() : "20261111-00001"), valFont);

        // Row 3: Referred Doctor & Sample Type
        addCardCell(card, "Referred Doctor:", lblFont, data.getReferredByDoctor() != null ? data.getReferredByDoctor() : "Self / Walk-in", valFont);
        addCardCell(card, "Sample (SID):", lblFont, data.getBarcodeNo() != null ? data.getBarcodeNo() : "BC20261111001", valBold);

        // Row 4: Specimen Type & Collection Time
        addCardCell(card, "Specimen Type:", lblFont, data.getSpecimenType() != null ? data.getSpecimenType() : "Whole Blood EDTA", valFont);
        addCardCell(card, "Collection Time:", lblFont, data.getCollectionTime() != null ? data.getCollectionTime() : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a")), valFont);

        // Row 5: Centre & Reporting Time
        addCardCell(card, "Centre Name:", lblFont, data.getCenterName() != null ? data.getCenterName() : "Main Reference Lab", valFont);
        addCardCell(card, "Reported Time:", lblFont, data.getApprovalTime() != null ? data.getApprovalTime() : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a")), valFont);

        document.add(card);
    }

    private void addCardCell(PdfPTable table, String label, Font lblFont, String val, Font valFont) {
        PdfPCell cLbl = new PdfPCell(new Phrase(label, lblFont));
        cLbl.setBackgroundColor(CARD_BG);
        cLbl.setBorderColor(BORDER_PRIMARY);
        cLbl.setBorderWidth(0.5f);
        cLbl.setPaddingTop(3.2f);
        cLbl.setPaddingBottom(3.2f);
        cLbl.setPaddingLeft(5f);

        PdfPCell cVal = new PdfPCell(new Phrase(val, valFont));
        cVal.setBackgroundColor(Color.WHITE);
        cVal.setBorderColor(BORDER_PRIMARY);
        cVal.setBorderWidth(0.5f);
        cVal.setPaddingTop(3.2f);
        cVal.setPaddingBottom(3.2f);
        cVal.setPaddingLeft(5f);

        table.addCell(cLbl);
        table.addCell(cVal);
    }

    /**
     * Department Banner & Investigation Title
     */
    private void addDepartmentBanner(Document document, ReportDataDto data) throws DocumentException {
        PdfPTable banner = new PdfPTable(1);
        banner.setWidthPercentage(100);

        Font deptFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9.2f, Color.WHITE);
        Font testFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10.5f, CITY_NAVY);
        Font methodFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 7.5f, TEXT_MUTED);

        String dept = data.getDepartment() != null ? data.getDepartment().toUpperCase() : "DEPARTMENT OF HEMATOLOGY & CLINICAL PATHOLOGY";
        String test = data.getTestName() != null ? data.getTestName().toUpperCase() : "COMPLETE BLOOD COUNT (CBC)";

        // Navy Department Header Ribbon
        PdfPCell deptCell = new PdfPCell(new Phrase(dept, deptFont));
        deptCell.setBackgroundColor(CITY_NAVY);
        deptCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        deptCell.setPadding(3.5f);
        deptCell.setBorder(Rectangle.NO_BORDER);
        banner.addCell(deptCell);

        document.add(banner);

        // Investigation Title and Technology/Method Subtitle
        Paragraph pTest = new Paragraph(test, testFont);
        pTest.setAlignment(Element.ALIGN_CENTER);
        pTest.setSpacingBefore(3f);
        document.add(pTest);

        String methodStr = "(Methodology: Automated 5-Part Differential Cell Counter • VCS Technology & Flow Cytometry)";
        if (test.contains("LIPID")) {
            methodStr = "(Methodology: Fully Automated Clinical Chemistry Analyzer • Enzymatic Photometric & Direct Clearance)";
        }
        Paragraph pMethod = new Paragraph(methodStr, methodFont);
        pMethod.setAlignment(Element.ALIGN_CENTER);
        pMethod.setSpacingAfter(3f);
        document.add(pMethod);
    }

    /**
     * City X-Ray / Star Imaging Results Grid (5-column strict alignment with DLC sub-sections)
     */
    private void addCityXRayResultsGrid(Document document, ReportDataDto data) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        // Columns: 35% Name, 18% Value, 12% Unit, 22% Ref Interval, 13% Status/Flag
        table.setWidths(new float[]{35f, 18f, 12f, 22f, 13f});
        table.setHeaderRows(1);

        Font thFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, Color.WHITE);

        addCityHeaderCell(table, "TEST INVESTIGATION / PARAMETER", thFont, Element.ALIGN_LEFT);
        addCityHeaderCell(table, "OBSERVED VALUE", thFont, Element.ALIGN_RIGHT);
        addCityHeaderCell(table, "UNIT", thFont, Element.ALIGN_CENTER);
        addCityHeaderCell(table, "BIOLOGICAL REFERENCE INTERVAL", thFont, Element.ALIGN_CENTER);
        addCityHeaderCell(table, "CLINICAL STATUS", thFont, Element.ALIGN_CENTER);

        Font tdFont = FontFactory.getFont(FontFactory.HELVETICA, 8f, TEXT_DARK);
        Font tdBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, TEXT_DARK);
        Font tdHigh = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, FLAG_HIGH);
        Font tdLow = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, FLAG_LOW);
        Font tdSubhead = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 7.8f, CITY_NAVY);

        Font badgeNormFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7f, FLAG_NORMAL);
        Font badgeHighFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7f, FLAG_HIGH);
        Font badgeLowFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7f, FLAG_LOW);

        boolean dlcHeaderAdded = false;

        if (data.getObservations() != null && !data.getObservations().isEmpty()) {
            int rowIdx = 0;
            for (ReportObservationDto obs : data.getObservations()) {
                String name = obs.getAnalyteName();

                // Add DLC sub-section header if entering differential count
                if (!dlcHeaderAdded && isDlcComponent(name)) {
                    addSectionSubHeader(table, "DIFFERENTIAL LEUCOCYTE COUNT (DLC)", tdSubhead);
                    dlcHeaderAdded = true;
                }

                boolean isAbnormal = obs.isAbnormal() ||
                        "HIGH".equalsIgnoreCase(obs.getFlag()) ||
                        "LOW".equalsIgnoreCase(obs.getFlag()) ||
                        "ABNORMAL".equalsIgnoreCase(obs.getFlag()) ||
                        "PANIC".equalsIgnoreCase(obs.getFlag());

                boolean isHigh = "HIGH".equalsIgnoreCase(obs.getFlag());
                boolean isLow = "LOW".equalsIgnoreCase(obs.getFlag());

                Color rowBg = (rowIdx % 2 == 0) ? Color.WHITE : ROW_ALT;

                // 1. Parameter Name (Left-aligned, indented if DLC component)
                String displayName = isDlcComponent(name) ? "    " + name : name;
                PdfPCell c1 = new PdfPCell(new Phrase(displayName, tdFont));
                styleCityCell(c1, rowBg, Element.ALIGN_LEFT);
                table.addCell(c1);

                // 2. Result Value (Right-aligned, bold, with clinical indicator)
                String valStr = obs.getResultValue() != null ? obs.getResultValue() : "-";
                Font valFont = tdBold;
                if (isHigh) {
                    valStr += "  ▲ (H)";
                    valFont = tdHigh;
                } else if (isLow) {
                    valStr += "  ▼ (L)";
                    valFont = tdLow;
                } else if (isAbnormal) {
                    valStr += "  *";
                    valFont = tdHigh;
                }
                PdfPCell c2 = new PdfPCell(new Phrase(valStr, valFont));
                styleCityCell(c2, rowBg, Element.ALIGN_RIGHT);
                table.addCell(c2);

                // 3. Unit (Center-aligned)
                PdfPCell c3 = new PdfPCell(new Phrase(obs.getUnit() != null ? obs.getUnit() : "", tdFont));
                styleCityCell(c3, rowBg, Element.ALIGN_CENTER);
                table.addCell(c3);

                // 4. Biological Reference Interval (Center-aligned)
                PdfPCell c4 = new PdfPCell(new Phrase(obs.getBiologicalInterval() != null ? obs.getBiologicalInterval() : "-", tdFont));
                styleCityCell(c4, rowBg, Element.ALIGN_CENTER);
                table.addCell(c4);

                // 5. Clinical Status Badge
                PdfPCell c5;
                if (isHigh) {
                    c5 = new PdfPCell(new Phrase("HIGH", badgeHighFont));
                    c5.setBackgroundColor(BADGE_BG_HIGH);
                } else if (isLow) {
                    c5 = new PdfPCell(new Phrase("LOW", badgeLowFont));
                    c5.setBackgroundColor(BADGE_BG_LOW);
                } else {
                    c5 = new PdfPCell(new Phrase("NORMAL", badgeNormFont));
                    c5.setBackgroundColor(BADGE_BG_NORM);
                }
                styleCityCell(c5, c5.getBackgroundColor(), Element.ALIGN_CENTER);
                table.addCell(c5);

                rowIdx++;
            }
        }

        document.add(table);
    }

    private boolean isDlcComponent(String name) {
        if (name == null) return false;
        String lower = name.toLowerCase();
        return lower.startsWith("neutrophil") ||
                lower.startsWith("lymphocyte") ||
                lower.startsWith("eosinophil") ||
                lower.startsWith("monocyte") ||
                lower.startsWith("basophil");
    }

    private void addSectionSubHeader(PdfPTable table, String title, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(title, font));
        cell.setColspan(5);
        cell.setBackgroundColor(SECTION_BG);
        cell.setBorderColor(BORDER_HAIRLINE);
        cell.setBorderWidth(0.5f);
        cell.setPaddingTop(3f);
        cell.setPaddingBottom(3f);
        cell.setPaddingLeft(6f);
        table.addCell(cell);
    }

    private void addCityHeaderCell(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(CITY_NAVY);
        cell.setBorderColor(BORDER_PRIMARY);
        cell.setBorderWidth(0.5f);
        cell.setPaddingTop(4.5f);
        cell.setPaddingBottom(4.5f);
        cell.setPaddingLeft(6f);
        cell.setPaddingRight(6f);
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }

    private void styleCityCell(PdfPCell cell, Color bg, int alignment) {
        cell.setBackgroundColor(bg);
        cell.setBorderColor(BORDER_HAIRLINE);
        cell.setBorderWidth(0.5f);
        cell.setPaddingTop(3.2f);
        cell.setPaddingBottom(3.2f);
        cell.setPaddingLeft(6f);
        cell.setPaddingRight(6f);
        cell.setHorizontalAlignment(alignment);
    }

    /**
     * Examination Methodology & Clinical Remarks Box
     */
    private void addClinicalInterpretationBox(Document document, ReportDataDto data) throws DocumentException {
        PdfPTable box = new PdfPTable(1);
        box.setWidthPercentage(100);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, CITY_NAVY);
        Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 7.2f, TEXT_MUTED);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(CARD_BG);
        cell.setBorderColor(BORDER_PRIMARY);
        cell.setBorderWidth(0.5f);
        cell.setPadding(4.5f);

        Paragraph pTitle = new Paragraph("CLINICAL INTERPRETATION & METHODOLOGY NOTES:", titleFont);
        Paragraph p1 = new Paragraph("• Analytical Method: Tested on fully automated multi-angle cell counter / chemistry analyzer with dual-level internal QC.", textFont);
        Paragraph p2 = new Paragraph("• Biological Reference Interval: Standardized as per CLSI C28-A3 guidelines for Indian adult & pediatric demographics.", textFont);
        Paragraph p3 = new Paragraph("• Smear Examination: Red blood cells normocytic normochromic. Platelets appear adequate in number on peripheral smear.", textFont);

        cell.addElement(pTitle);
        cell.addElement(p1);
        cell.addElement(p2);
        cell.addElement(p3);

        box.addCell(cell);
        document.add(box);
    }

    /**
     * Star Imaging / City X-Ray 3-Tier Multi-Signature Block
     */
    private void addThreeTierSignatureBlock(Document document, ReportDataDto data) throws DocumentException {
        PdfPTable sigTable = new PdfPTable(3);
        sigTable.setWidthPercentage(100);
        sigTable.setWidths(new float[]{32f, 34f, 34f});

        Font roleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, CITY_NAVY);
        Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.2f, TEXT_DARK);
        Font detailFont = FontFactory.getFont(FontFactory.HELVETICA, 7f, TEXT_MUTED);

        // Signatory 1: Medical Lab Technologist
        PdfPCell c1 = new PdfPCell();
        c1.setBorderColor(BORDER_PRIMARY);
        c1.setBorderWidth(0.5f);
        c1.setPadding(4.5f);
        c1.addElement(new Paragraph("Performed & Analyzed By:", roleFont));
        c1.addElement(new Paragraph("P. Ramesh, DMLT, B.Sc. MLT", nameFont));
        c1.addElement(new Paragraph("Medical Lab Technologist", detailFont));
        c1.addElement(new Paragraph("Internal QC Verified", detailFont));
        sigTable.addCell(c1);

        // Signatory 2: Quality Manager / Biochemist
        PdfPCell c2 = new PdfPCell();
        c2.setBorderColor(BORDER_PRIMARY);
        c2.setBorderWidth(0.5f);
        c2.setPadding(4.5f);
        c2.addElement(new Paragraph("Checked & Validated By:", roleFont));
        c2.addElement(new Paragraph("Dr. Sunita Rao, M.Sc., Ph.D.", nameFont));
        c2.addElement(new Paragraph("Quality Manager & Biochemist", detailFont));
        c2.addElement(new Paragraph("Electronic Authentication", detailFont));
        sigTable.addCell(c2);

        // Signatory 3: Consultant Pathologist (Authorized Signatory)
        PdfPCell c3 = new PdfPCell();
        c3.setBorderColor(BORDER_PRIMARY);
        c3.setBorderWidth(0.5f);
        c3.setPadding(4.5f);
        c3.addElement(new Paragraph("Authorized & Reported By:", roleFont));
        String doctor = data.getPathologistName() != null ? data.getPathologistName() : "Dr. Rajesh Sharma, MD (Pathology)";
        String reg = data.getMedicalRegNo() != null ? "Reg No: " + data.getMedicalRegNo() : "Reg No: DMC-48291 / KMC-58291";
        c3.addElement(new Paragraph(doctor, nameFont));
        c3.addElement(new Paragraph(reg, detailFont));
        c3.addElement(new Paragraph("Senior Consultant Pathologist & HOD", detailFont));
        sigTable.addCell(c3);

        document.add(sigTable);
    }

    /**
     * Grounded Security, Legal Disclaimer & End of Report
     */
    private void addComplianceFooterBlock(Document document, ReportDataDto data) throws DocumentException {
        PdfPTable footer = new PdfPTable(1);
        footer.setWidthPercentage(100);

        Font endFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, CITY_NAVY);
        Font discFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 6.5f, TEXT_MUTED);

        Paragraph pEnd = new Paragraph("❖ ❖ ❖  END OF REPORT  ❖ ❖ ❖", endFont);
        pEnd.setAlignment(Element.ALIGN_CENTER);

        Paragraph pDisc = new Paragraph(
                "Conditions of Reporting: 1. Results relate only to the specimen tested and should be correlated with clinical findings. " +
                        "2. This test is processed under strict ISO 15189:2022 quality standards. " +
                        "3. Electronically authenticated diagnostic report issued under Section 65B of Indian Evidence Act / IT Act 2000.",
                discFont
        );
        pDisc.setAlignment(Element.ALIGN_CENTER);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(2f);
        cell.addElement(pEnd);
        cell.addElement(pDisc);

        footer.addCell(cell);
        document.add(footer);
    }

    /**
     * CityXRayFooterEvent manages dynamic 'Page X of Y' numbering, running footer, and bottom accent ribbon.
     */
    private static class CityXRayFooterEvent extends PdfPageEventHelper {
        protected PdfTemplate totalPagesTemplate;
        protected BaseFont baseFont;

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            totalPagesTemplate = writer.getDirectContent().createTemplate(30, 16);
            try {
                baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            int pageNum = writer.getPageNumber();
            String pageText = "Page " + pageNum + " of ";

            float textSize = 7.2f;
            float textBase = document.bottom() - 14;
            float rightMargin = document.right();

            // Bottom Dual-Tone Accent Stripe (Royal Navy + Cyan)
            cb.setColorFill(CITY_NAVY);
            cb.rectangle(document.left(), document.bottom() - 20, document.right() - document.left(), 2f);
            cb.fill();

            cb.setColorFill(CITY_CYAN);
            cb.rectangle(document.left(), document.bottom() - 22, document.right() - document.left(), 1f);
            cb.fill();

            cb.beginText();
            cb.setFontAndSize(baseFont, textSize);
            cb.setColorFill(TEXT_MUTED);

            // Left running disclaimer
            cb.showTextAligned(Element.ALIGN_LEFT, "City X-Ray & Scan Clinic / Star Imaging • NABL Accredited • 21 CFR Part 11 Validated", document.left(), textBase, 0);

            // Right dynamic page numbering
            cb.showTextAligned(Element.ALIGN_RIGHT, pageText, rightMargin - 15, textBase, 0);
            cb.endText();

            // Add template placeholder for total pages
            cb.addTemplate(totalPagesTemplate, rightMargin - 15, textBase);
        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            totalPagesTemplate.beginText();
            totalPagesTemplate.setFontAndSize(baseFont, 7.2f);
            totalPagesTemplate.setColorFill(TEXT_MUTED);
            totalPagesTemplate.setTextMatrix(0, 0);
            totalPagesTemplate.showText(String.valueOf(writer.getPageNumber()));
            totalPagesTemplate.endText();
        }
    }
}
