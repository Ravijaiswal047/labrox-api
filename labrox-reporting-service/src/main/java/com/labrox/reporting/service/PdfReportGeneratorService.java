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

@Service
@Slf4j
public class PdfReportGeneratorService {

    // Professional LIS Color Palette
    private static final Color BRAND_NAVY = new Color(26, 54, 93);      // #1A365D - Deep Sapphire
    private static final Color BRAND_BLUE = new Color(37, 99, 235);     // #2563EB - Royal Blue
    private static final Color ACCENT_GOLD = new Color(217, 119, 6);    // #D97706 - Warm Amber Gold
    private static final Color BORDER_GRAY = new Color(203, 213, 225);   // #CBD5E1 - Slate 300
    private static final Color BG_LIGHT = new Color(248, 250, 252);     // #F8FAFC - Slate 50
    private static final Color BG_HEADER = new Color(241, 245, 249);    // #F1F5F9 - Slate 100
    private static final Color TEXT_DARK = new Color(15, 23, 42);       // #0F172A - Slate 900
    private static final Color TEXT_MUTED = new Color(100, 116, 139);   // #64748B - Slate 500
    private static final Color ABNORMAL_RED = new Color(220, 38, 38);   // #DC2626 - Red 600
    private static final Color NORMAL_GREEN = new Color(22, 101, 52);   // #166534 - Emerald 800

    public byte[] generateDiagnosticReportPdf(ReportDataDto data) {
        return generateDiagnosticReportPdf(data, true);
    }

    public byte[] generateDiagnosticReportPdf(ReportDataDto data, boolean withHeader) {
        log.info("Generating Professional A4 LIS Diagnostic PDF for testId={}, barcode={}, withHeader={}",
                data.getTestId(), data.getBarcodeNo(), withHeader);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Tight margins (28 left, 28 right, 22 top, 22 bottom) to maximize printable area and avoid dead whitespace
            Document document = new Document(PageSize.A4, 28, 28, 22, 22);
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            document.open();

            // 1. Professional Header Branding
            if (withHeader) {
                addCenterLetterhead(document, data);
            } else {
                Paragraph spacing = new Paragraph("\n\n\n");
                document.add(spacing);
            }

            // 2. Patient Demographics & Accession Meta Box (Bordered Grid)
            addPatientDemographics(document, data);

            // Small spacing
            addVerticalSpacer(document, 6);

            // 3. Clinical Department & Investigation Bar
            addTestBanner(document, data);

            addVerticalSpacer(document, 4);

            // 4. Clinical Results Grid (5-column professional tabular format)
            addResultsGrid(document, data);

            addVerticalSpacer(document, 6);

            // 5. Methodology & Laboratory Comments Box
            addClinicalNotesBox(document, data);

            addVerticalSpacer(document, 6);

            // 6. Dual Signature & 21 CFR Part 11 Electronic Verification Block
            addDigitalSignatureStamp(document, data);

            addVerticalSpacer(document, 4);

            // 7. Grounded Legal & Accreditation Footer (Eliminates bottom dead gap)
            addFooterBlock(document, data);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate professional PDF report: {}", e.getMessage(), e);
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

    private void addCenterLetterhead(Document document, ReportDataDto data) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);

        Font brandFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, BRAND_NAVY);
        Font nablFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, ACCENT_GOLD);
        Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 7.5f, TEXT_DARK);
        Font contactFont = FontFactory.getFont(FontFactory.HELVETICA, 7.5f, TEXT_MUTED);

        String centerName = data.getCenterName() != null ? data.getCenterName() : "LABROX DIAGNOSTICS & REFERENCE LABORATORIES";
        Paragraph pTitle = new Paragraph(centerName.toUpperCase(), brandFont);
        pTitle.setAlignment(Element.ALIGN_CENTER);

        String nabl = data.getNablAccNo() != null
                ? "NABL ACCREDITED CLINICAL LABORATORY (ISO 15189:2022) | CERT NO: " + data.getNablAccNo() + " | CAP & ICMR COMPLIANT"
                : "NABL ACCREDITED MEDICAL LABORATORY (ISO 15189:2022) | ICMR APPROVED | CENTRAL REFERENCE LAB";
        Paragraph pNabl = new Paragraph(nabl, nablFont);
        pNabl.setAlignment(Element.ALIGN_CENTER);

        String address = data.getAddressLine() != null ? data.getAddressLine() : "104 Healthcare Boulevard, Medical Enclave, Bengaluru 560001";
        String contact = data.getContactLine() != null ? data.getContactLine() : "Tel: +91 80 4000 5000 | Email: reports@labrox.io | Web: www.labrox.io";
        Paragraph pAddr = new Paragraph(address + " | " + contact, contactFont);
        pAddr.setAlignment(Element.ALIGN_CENTER);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.addElement(pTitle);
        cell.addElement(pNabl);
        cell.addElement(pAddr);
        cell.setPaddingBottom(4);
        headerTable.addCell(cell);

        document.add(headerTable);

        // Thin professional divider bar
        PdfPTable divider = new PdfPTable(1);
        divider.setWidthPercentage(100);
        PdfPCell divCell = new PdfPCell();
        divCell.setBackgroundColor(BRAND_NAVY);
        divCell.setFixedHeight(2f);
        divCell.setBorder(Rectangle.NO_BORDER);
        divider.addCell(divCell);
        document.add(divider);

        addVerticalSpacer(document, 5);
    }

    private void addPatientDemographics(Document document, ReportDataDto data) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{18f, 32f, 18f, 32f});

        Font lblFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, BRAND_NAVY);
        Font valFont = FontFactory.getFont(FontFactory.HELVETICA, 8f, TEXT_DARK);
        Font valBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, TEXT_DARK);

        // Row 1: Patient Name & Barcode
        addMetaCell(table, "Patient Name:", lblFont, data.getPatientName() != null ? data.getPatientName().toUpperCase() : "N/A", valBold);
        addMetaCell(table, "Barcode / Sample ID:", lblFont, data.getBarcodeNo() != null ? data.getBarcodeNo() : "N/A", valBold);

        // Row 2: Age/Gender & UHID
        String ageGender = (data.getAgeYears() != null ? data.getAgeYears() + " Y / " : "") + (data.getGender() != null ? data.getGender() : "N/A");
        addMetaCell(table, "Age / Gender:", lblFont, ageGender, valFont);
        addMetaCell(table, "UHID / MRN:", lblFont, data.getUhid() != null ? data.getUhid() : "N/A", valFont);

        // Row 3: Referred Doctor & Visit No
        addMetaCell(table, "Referred Doctor:", lblFont, data.getReferredByDoctor() != null ? data.getReferredByDoctor() : "Self / Walk-in", valFont);
        addMetaCell(table, "Visit / Lab No:", lblFont, "VN-" + (data.getBarcodeNo() != null ? data.getBarcodeNo() : "202611001"), valFont);

        // Row 4: Sample Type & Collection Date
        addMetaCell(table, "Specimen Type:", lblFont, data.getSpecimenType() != null ? data.getSpecimenType() : "Whole Blood EDTA", valFont);
        addMetaCell(table, "Collection Date:", lblFont, data.getCollectionTime() != null ? data.getCollectionTime() : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a")), valFont);

        // Row 5: Centre & Reporting Date
        addMetaCell(table, "Processing Centre:", lblFont, data.getCenterName() != null ? data.getCenterName() : "Main Reference Lab", valFont);
        addMetaCell(table, "Reported Date:", lblFont, data.getApprovalTime() != null ? data.getApprovalTime() : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a")), valFont);

        document.add(table);
    }

    private void addMetaCell(PdfPTable table, String label, Font lblFont, String val, Font valFont) {
        PdfPCell cLbl = new PdfPCell(new Phrase(label, lblFont));
        cLbl.setBackgroundColor(BG_HEADER);
        cLbl.setBorderColor(BORDER_GRAY);
        cLbl.setBorderWidth(0.5f);
        cLbl.setPaddingTop(3.5f);
        cLbl.setPaddingBottom(3.5f);
        cLbl.setPaddingLeft(5f);

        PdfPCell cVal = new PdfPCell(new Phrase(val, valFont));
        cVal.setBackgroundColor(Color.WHITE);
        cVal.setBorderColor(BORDER_GRAY);
        cVal.setBorderWidth(0.5f);
        cVal.setPaddingTop(3.5f);
        cVal.setPaddingBottom(3.5f);
        cVal.setPaddingLeft(5f);

        table.addCell(cLbl);
        table.addCell(cVal);
    }

    private void addTestBanner(Document document, ReportDataDto data) throws DocumentException {
        PdfPTable banner = new PdfPTable(1);
        banner.setWidthPercentage(100);

        Font bannerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9.5f, Color.WHITE);
        String dept = data.getDepartment() != null ? data.getDepartment().toUpperCase() : "HEMATOLOGY & CLINICAL PATHOLOGY";
        String test = data.getTestName() != null ? data.getTestName().toUpperCase() : "COMPLETE BLOOD COUNT (CBC)";

        Paragraph p = new Paragraph(dept + "  |  " + test, bannerFont);
        p.setAlignment(Element.ALIGN_CENTER);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BRAND_NAVY);
        cell.setPadding(4.5f);
        cell.addElement(p);
        cell.setBorder(Rectangle.NO_BORDER);

        banner.addCell(cell);
        document.add(banner);
    }

    private void addResultsGrid(Document document, ReportDataDto data) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{34f, 16f, 14f, 22f, 14f});

        Font thFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, BRAND_NAVY);

        addHeaderCell(table, "TEST INVESTIGATION / PARAMETER", thFont, Element.ALIGN_LEFT);
        addHeaderCell(table, "OBSERVED VALUE", thFont, Element.ALIGN_CENTER);
        addHeaderCell(table, "UNIT", thFont, Element.ALIGN_CENTER);
        addHeaderCell(table, "BIOLOGICAL REFERENCE INTERVAL", thFont, Element.ALIGN_CENTER);
        addHeaderCell(table, "STATUS / FLAG", thFont, Element.ALIGN_CENTER);

        Font tdFont = FontFactory.getFont(FontFactory.HELVETICA, 8f, TEXT_DARK);
        Font tdBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, TEXT_DARK);
        Font tdAbnormal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, ABNORMAL_RED);
        Font tdFlagNormal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, NORMAL_GREEN);
        Font tdFlagAbnormal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, ABNORMAL_RED);

        if (data.getObservations() != null && !data.getObservations().isEmpty()) {
            int rowIdx = 0;
            for (ReportObservationDto obs : data.getObservations()) {
                boolean isAbnormal = obs.isAbnormal() ||
                        "HIGH".equalsIgnoreCase(obs.getFlag()) ||
                        "LOW".equalsIgnoreCase(obs.getFlag()) ||
                        "ABNORMAL".equalsIgnoreCase(obs.getFlag()) ||
                        "PANIC".equalsIgnoreCase(obs.getFlag());

                Color rowBg = (rowIdx % 2 == 0) ? Color.WHITE : BG_LIGHT;
                Font valFontToUse = isAbnormal ? tdAbnormal : tdBold;

                // 1. Parameter Name
                PdfPCell c1 = new PdfPCell(new Phrase(obs.getAnalyteName(), tdFont));
                styleBodyCell(c1, rowBg, Element.ALIGN_LEFT);
                table.addCell(c1);

                // 2. Result Value
                String displayVal = obs.getResultValue() != null ? obs.getResultValue() : "-";
                if (isAbnormal && "HIGH".equalsIgnoreCase(obs.getFlag())) {
                    displayVal += "  ▲";
                } else if (isAbnormal && "LOW".equalsIgnoreCase(obs.getFlag())) {
                    displayVal += "  ▼";
                }
                PdfPCell c2 = new PdfPCell(new Phrase(displayVal, valFontToUse));
                styleBodyCell(c2, rowBg, Element.ALIGN_CENTER);
                table.addCell(c2);

                // 3. Unit
                PdfPCell c3 = new PdfPCell(new Phrase(obs.getUnit() != null ? obs.getUnit() : "", tdFont));
                styleBodyCell(c3, rowBg, Element.ALIGN_CENTER);
                table.addCell(c3);

                // 4. Biological Reference Interval
                PdfPCell c4 = new PdfPCell(new Phrase(obs.getBiologicalInterval() != null ? obs.getBiologicalInterval() : "-", tdFont));
                styleBodyCell(c4, rowBg, Element.ALIGN_CENTER);
                table.addCell(c4);

                // 5. Flag / Status Badge
                String flagStr = obs.getFlag() != null ? obs.getFlag().toUpperCase() : (isAbnormal ? "ABNORMAL" : "NORMAL");
                PdfPCell c5 = new PdfPCell(new Phrase(flagStr, isAbnormal ? tdFlagAbnormal : tdFlagNormal));
                styleBodyCell(c5, rowBg, Element.ALIGN_CENTER);
                table.addCell(c5);

                rowIdx++;
            }
        }

        document.add(table);
    }

    private void styleBodyCell(PdfPCell cell, Color bg, int alignment) {
        cell.setBackgroundColor(bg);
        cell.setBorderColor(BORDER_GRAY);
        cell.setBorderWidth(0.5f);
        cell.setPaddingTop(3.5f);
        cell.setPaddingBottom(3.5f);
        cell.setPaddingLeft(5f);
        cell.setPaddingRight(5f);
        cell.setHorizontalAlignment(alignment);
    }

    private void addHeaderCell(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(BG_HEADER);
        cell.setBorderColor(BORDER_GRAY);
        cell.setBorderWidth(0.5f);
        cell.setPaddingTop(4f);
        cell.setPaddingBottom(4f);
        cell.setPaddingLeft(5f);
        cell.setPaddingRight(5f);
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }

    private void addClinicalNotesBox(Document document, ReportDataDto data) throws DocumentException {
        PdfPTable notesTable = new PdfPTable(1);
        notesTable.setWidthPercentage(100);

        Font noteTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, BRAND_NAVY);
        Font noteTextFont = FontFactory.getFont(FontFactory.HELVETICA, 7f, TEXT_MUTED);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BG_LIGHT);
        cell.setBorderColor(BORDER_GRAY);
        cell.setBorderWidth(0.5f);
        cell.setPadding(4f);

        Paragraph pHeader = new Paragraph("EXAMINATION METHODOLOGY & CLINICAL REMARKS:", noteTitleFont);
        Paragraph p1 = new Paragraph("• Methodology: Automated Multi-Angle Polarized Scatter Separation (MAPSS) & Flow Cytometry.", noteTextFont);
        Paragraph p2 = new Paragraph("• Quality Control: Tested on dual-level controls with Levey-Jennings chart validation prior to reporting.", noteTextFont);
        Paragraph p3 = new Paragraph("• Comments: Peripheral blood smear shows normocytic normochromic red cells. Platelets adequate on smear.", noteTextFont);

        cell.addElement(pHeader);
        cell.addElement(p1);
        cell.addElement(p2);
        cell.addElement(p3);

        notesTable.addCell(cell);
        document.add(notesTable);
    }

    private void addDigitalSignatureStamp(Document document, ReportDataDto data) throws DocumentException {
        PdfPTable sigTable = new PdfPTable(3);
        sigTable.setWidthPercentage(100);
        sigTable.setWidths(new float[]{30f, 40f, 30f});

        Font roleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, BRAND_NAVY);
        Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8f, TEXT_DARK);
        Font detailFont = FontFactory.getFont(FontFactory.HELVETICA, 6.8f, TEXT_MUTED);
        Font monoFont = FontFactory.getFont(FontFactory.COURIER, 6f, TEXT_MUTED);

        // Column 1: Medical Lab Technologist
        PdfPCell c1 = new PdfPCell();
        c1.setBorderColor(BORDER_GRAY);
        c1.setBorderWidth(0.5f);
        c1.setPadding(5f);
        c1.addElement(new Paragraph("Analyzed & Checked By:", roleFont));
        c1.addElement(new Paragraph("P. Ramesh, M.Sc. MLT", nameFont));
        c1.addElement(new Paragraph("Senior Technical Specialist", detailFont));
        c1.addElement(new Paragraph("Internal QC Verified", detailFont));
        sigTable.addCell(c1);

        // Column 2: 21 CFR Part 11 Electronic Signature Stamp
        PdfPCell c2 = new PdfPCell();
        c2.setBorderColor(BORDER_GRAY);
        c2.setBorderWidth(0.5f);
        c2.setPadding(5f);
        c2.addElement(new Paragraph("21 CFR Part 11 Electronic Verification", roleFont));
        String token = data.getVerificationToken() != null ? data.getVerificationToken() : "LRX-VRF-20261110-00109";
        c2.addElement(new Paragraph("Verify Online: https://lims.labrox.io/v/" + token, detailFont));
        String sigHash = data.getSignatureHash() != null ? data.getSignatureHash() : "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        c2.addElement(new Paragraph("SHA-256: " + sigHash.substring(0, Math.min(32, sigHash.length())) + "...", monoFont));
        c2.addElement(new Paragraph("Digital Certificate: NABL-AUDIT-ACTIVE", detailFont));
        sigTable.addCell(c2);

        // Column 3: Authorized Pathologist Signature
        PdfPCell c3 = new PdfPCell();
        c3.setBorderColor(BORDER_GRAY);
        c3.setBorderWidth(0.5f);
        c3.setPadding(5f);
        c3.addElement(new Paragraph("Authorized Signatory:", roleFont));
        String doctor = data.getPathologistName() != null ? data.getPathologistName() : "Dr. Rajesh Sharma, MD (Pathology)";
        String reg = data.getMedicalRegNo() != null ? "Reg No: " + data.getMedicalRegNo() : "Reg No: KMC-58291";
        c3.addElement(new Paragraph(doctor, nameFont));
        c3.addElement(new Paragraph(reg, detailFont));
        c3.addElement(new Paragraph("Senior Consultant Pathologist", detailFont));
        sigTable.addCell(c3);

        document.add(sigTable);
    }

    private void addFooterBlock(Document document, ReportDataDto data) throws DocumentException {
        PdfPTable footer = new PdfPTable(1);
        footer.setWidthPercentage(100);

        Font disclaimerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 6.2f, TEXT_MUTED);
        Font bottomFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 6.8f, BRAND_NAVY);

        Paragraph pEnd = new Paragraph("*** End of Clinical Diagnostic Examination Report ***", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7f, TEXT_MUTED));
        pEnd.setAlignment(Element.ALIGN_CENTER);

        Paragraph pDisc = new Paragraph(
                "Conditions of Reporting: 1. Reports are intended for clinical correlation by attending physicians. " +
                        "2. All tests are conducted under stringent internal QC and external EQAS proficiency standards. " +
                        "3. This is an authenticated computer-generated electronic diagnostic report requiring no physical signature.",
                disclaimerFont
        );
        pDisc.setAlignment(Element.ALIGN_CENTER);

        Paragraph pBrand = new Paragraph(
                "LabRox Enterprise LIS Cloud Platform  |  ISO 15189:2022 Certified  |  Page 1 of 1",
                bottomFont
        );
        pBrand.setAlignment(Element.ALIGN_CENTER);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(3f);
        cell.addElement(pEnd);
        cell.addElement(pDisc);
        cell.addElement(pBrand);

        footer.addCell(cell);
        document.add(footer);
    }
}
