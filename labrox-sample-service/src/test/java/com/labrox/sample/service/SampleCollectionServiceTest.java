package com.labrox.sample.service;

import com.labrox.sample.domain.SampleCollection;
import com.labrox.sample.dto.SampleCollectRequest;
import com.labrox.sample.dto.SampleReceiveRequest;
import com.labrox.sample.dto.SampleRejectRequest;
import com.labrox.sample.repository.SampleCollectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SampleCollectionServiceTest {

    @Mock
    private SampleCollectionRepository sampleRepository;

    @Mock
    private BarcodeGeneratorService barcodeGenerator;

    @InjectMocks
    private SampleCollectionService sampleService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Collect sample produces COLLECTED status and assigns barcode")
    void testCollectSample() {
        when(barcodeGenerator.generateBarcode()).thenReturn("S26307123456");
        when(sampleRepository.save(any(SampleCollection.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SampleCollectRequest req = SampleCollectRequest.builder()
                .companyId(1)
                .orderId("ORD-9901")
                .patientId(501)
                .patientName("John Doe")
                .sampleTypeId(1)
                .sampleTypeName("EDTA Blood")
                .tubeColor("Purple/EDTA")
                .collectorId(10)
                .build();

        SampleCollection result = sampleService.collectSample(req);

        assertNotNull(result);
        assertEquals("S26307123456", result.getBarcodeNo());
        assertEquals("COLLECTED", result.getStatus());
        assertEquals("Purple/EDTA", result.getTubeColor());
    }

    @Test
    @DisplayName("Receive sample updates status to RECEIVED and sets receiver timestamp")
    void testReceiveSample() {
        SampleCollection existing = SampleCollection.builder()
                .id(1L)
                .companyId(1)
                .barcodeNo("S26307123456")
                .status("COLLECTED")
                .build();

        when(sampleRepository.findByCompanyIdAndBarcodeNo(1, "S26307123456")).thenReturn(Optional.of(existing));
        when(sampleRepository.save(any(SampleCollection.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SampleReceiveRequest req = SampleReceiveRequest.builder()
                .companyId(1)
                .barcodeNo("S26307123456")
                .receiverId(25)
                .build();

        Optional<SampleCollection> result = sampleService.receiveSample(req);

        assertTrue(result.isPresent());
        assertEquals("RECEIVED", result.get().getStatus());
        assertEquals(25, result.get().getReceivedBy());
        assertNotNull(result.get().getReceivedTime());
    }

    @Test
    @DisplayName("Reject sample marks status as REJECTED with reason and remarks")
    void testRejectSample() {
        SampleCollection existing = SampleCollection.builder()
                .id(1L)
                .companyId(1)
                .barcodeNo("S26307123456")
                .status("COLLECTED")
                .build();

        when(sampleRepository.findByCompanyIdAndBarcodeNo(1, "S26307123456")).thenReturn(Optional.of(existing));
        when(sampleRepository.save(any(SampleCollection.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SampleRejectRequest req = SampleRejectRequest.builder()
                .companyId(1)
                .barcodeNo("S26307123456")
                .reasonId(1) // Hemolyzed
                .remarks("Grossly hemolyzed, red redraw requested")
                .rejectedBy(25)
                .build();

        Optional<SampleCollection> result = sampleService.rejectSample(req);

        assertTrue(result.isPresent());
        assertEquals("REJECTED", result.get().getStatus());
        assertEquals(1, result.get().getRejectionReasonId());
        assertEquals("Grossly hemolyzed, red redraw requested", result.get().getRejectionRemarks());
    }
}
