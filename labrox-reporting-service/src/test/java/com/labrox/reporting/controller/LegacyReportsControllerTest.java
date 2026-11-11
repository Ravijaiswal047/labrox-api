package com.labrox.reporting.controller;

import com.labrox.reporting.dto.ReportDataDto;
import com.labrox.reporting.service.PdfReportGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class LegacyReportsControllerTest {

    private PdfReportGeneratorService pdfService;
    private LegacyReportsController reportsController;
    private LegacyCommonReportsController commonReportsController;

    @BeforeEach
    void setUp() {
        pdfService = Mockito.mock(PdfReportGeneratorService.class);
        reportsController = new LegacyReportsController(pdfService);
        commonReportsController = new LegacyCommonReportsController();
    }

    @Test
    @DisplayName("Reports: DoctorReport returns PDF URL in legacy envelope")
    void testDoctorReport() {
        ResponseEntity<Map<String, Object>> res = reportsController.doctorReport(new HashMap<>());
        assertEquals(200, res.getStatusCode().value());
        assertTrue((Boolean) res.getBody().get("status"));
        assertEquals("File upload Successfully.", res.getBody().get("message"));
        assertNotNull(res.getBody().get("Url"));
    }

    @Test
    @DisplayName("Reports: GetSingleReceiptData returns receipt lines and reprint URL")
    void testGetSingleReceiptData() {
        Map<String, Object> req = new HashMap<>();
        req.put("ReceiptNo", "REC-20261104-00086");

        ResponseEntity<Map<String, Object>> res = reportsController.getSingleReceiptData(req);
        assertEquals(200, res.getStatusCode().value());
        assertTrue((Boolean) res.getBody().get("status"));

        List<?> list = (List<?>) responseList(res);
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    @DisplayName("Reports: Download PDF returns binary application/pdf content")
    void testDownloadPdf() {
        when(pdfService.generateDiagnosticReportPdf(any(ReportDataDto.class))).thenReturn("%PDF-1.4 mock pdf data".getBytes());

        ResponseEntity<byte[]> res = reportsController.downloadPdf("test.pdf");
        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
        assertTrue(res.getBody().length > 0);
    }

    @Test
    @DisplayName("CommonReports: GenerateReport returns success envelope with report Url")
    void testGenerateReport() {
        ResponseEntity<Map<String, Object>> res = commonReportsController.generateReport(new HashMap<>());
        assertEquals(200, res.getStatusCode().value());
        assertTrue((Boolean) res.getBody().get("status"));
        assertNotNull(res.getBody().get("Url"));
    }

    private List<?> responseList(ResponseEntity<Map<String, Object>> res) {
        return (List<?>) res.getBody().get("message");
    }
}
