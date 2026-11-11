package com.labrox.iam;

import com.labrox.iam.domain.AuditTrailEvent;
import com.labrox.iam.dto.AuditChainVerificationResponse;
import com.labrox.iam.dto.AuditLogRequest;
import com.labrox.iam.repository.AuditTrailRepository;
import com.labrox.iam.service.AuditTrailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuditTrailServiceTest {

    @Mock
    private AuditTrailRepository auditRepository;

    @InjectMocks
    private AuditTrailService auditTrailService;

    private List<AuditTrailEvent> inMemoryChain;

    @BeforeEach
    void setUp() {
        inMemoryChain = new ArrayList<>();
    }

    @Test
    @DisplayName("21 CFR Part 11: Validates cryptographic hash chain across sequential events")
    void testAuditHashChainIntegrity() {
        when(auditRepository.findLatestByEntity(anyInt(), anyString(), anyString()))
                .thenAnswer(inv -> inMemoryChain.isEmpty() ? Optional.empty() : Optional.of(inMemoryChain.get(inMemoryChain.size() - 1)));

        when(auditRepository.save(any(AuditTrailEvent.class)))
                .thenAnswer(inv -> {
                    AuditTrailEvent e = inv.getArgument(0);
                    e.setId((long) (inMemoryChain.size() + 1));
                    inMemoryChain.add(e);
                    return e;
                });

        when(auditRepository.findByCompanyIdAndEntityNameAndEntityIdOrderByIdAsc(anyInt(), anyString(), anyString()))
                .thenReturn(inMemoryChain);

        // Step 1: Create Result Entry
        AuditTrailEvent e1 = auditTrailService.recordAudit(AuditLogRequest.builder()
                .companyId(1006)
                .entityName("patient_labinvestigation_opd")
                .entityId("TEST-9901")
                .action("CREATE")
                .newValue("Hemoglobin=14.2")
                .userId(101)
                .build());

        assertEquals("0000000000000000000000000000000000000000000000000000000000000000", e1.getPreviousHash());
        assertNotNull(e1.getCurrentHash());

        // Step 2: Pathologist Result Amendment
        AuditTrailEvent e2 = auditTrailService.recordAudit(AuditLogRequest.builder()
                .companyId(1006)
                .entityName("patient_labinvestigation_opd")
                .entityId("TEST-9901")
                .action("AMEND")
                .previousValue("Hemoglobin=14.2")
                .newValue("Hemoglobin=12.1")
                .changeReason("Clerical error correction by pathologist")
                .userId(202)
                .build());

        assertEquals(e1.getCurrentHash(), e2.getPreviousHash(), "Second event must point to first event hash");

        // Verify Chain
        AuditChainVerificationResponse verifyRes = auditTrailService.verifyChain(1006, "patient_labinvestigation_opd", "TEST-9901");
        assertTrue(verifyRes.isValid(), "Clean audit chain must pass 100% verification");
        assertEquals(2, verifyRes.getTotalRecordsVerified());
    }

    @Test
    @DisplayName("21 CFR Part 11: Accurately detects data tampering in audit record payload")
    void testAuditTamperingDetection() {
        AuditTrailEvent e1 = AuditTrailEvent.builder()
                .id(1L)
                .companyId(1006)
                .entityName("patient_labinvestigation_opd")
                .entityId("TEST-9901")
                .action("CREATE")
                .previousValue(null)
                .newValue("Hemoglobin=14.2")
                .userId(101)
                .timestamp(Instant.now())
                .previousHash("0000000000000000000000000000000000000000000000000000000000000000")
                .currentHash("fake_unverified_hash_or_tampered")
                .build();

        inMemoryChain.add(e1);
        when(auditRepository.findByCompanyIdAndEntityNameAndEntityIdOrderByIdAsc(anyInt(), anyString(), anyString()))
                .thenReturn(inMemoryChain);

        AuditChainVerificationResponse verifyRes = auditTrailService.verifyChain(1006, "patient_labinvestigation_opd", "TEST-9901");
        assertFalse(verifyRes.isValid(), "Tampered record must fail verification");
        assertEquals(1L, verifyRes.getCorruptedRecordId());
    }
}

