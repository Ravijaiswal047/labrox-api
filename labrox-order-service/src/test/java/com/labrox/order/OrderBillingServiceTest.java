package com.labrox.order;

import com.labrox.order.domain.LedgerTransaction;
import com.labrox.order.domain.PatientMaster;
import com.labrox.order.domain.Receipt;
import com.labrox.order.dto.RegistrationRequestDTO;
import com.labrox.order.repository.LedgerTransactionRepository;
import com.labrox.order.repository.PatientMasterRepository;
import com.labrox.order.repository.ReceiptRepository;
import com.labrox.order.service.OrderBillingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class OrderBillingServiceTest {

    private PatientMasterRepository patientRepository;
    private LedgerTransactionRepository ltRepository;
    private ReceiptRepository receiptRepository;
    private OrderBillingService service;

    @BeforeEach
    void setUp() {
        patientRepository = Mockito.mock(PatientMasterRepository.class);
        ltRepository = Mockito.mock(LedgerTransactionRepository.class);
        receiptRepository = Mockito.mock(ReceiptRepository.class);
        service = new OrderBillingService(patientRepository, ltRepository, receiptRepository);
    }

    @Test
    @DisplayName("Billing & Registration: Complete workflow with UHID generation, Work Order booking and Receipt collection")
    void testRegisterPatientAndBookWorkOrder_Success() {
        when(patientRepository.countByCompanyId(1)).thenReturn(42L);
        when(ltRepository.countByCompanyId(1)).thenReturn(108L);
        when(receiptRepository.countByCompanyId(1)).thenReturn(85L);

        PatientMaster mockSavedPatient = PatientMaster.builder()
                .id(101L)
                .companyId(1)
                .uhid("LR2026-000043")
                .name("John Doe")
                .mobile("9876543210")
                .build();
        when(patientRepository.save(any(PatientMaster.class))).thenReturn(mockSavedPatient);

        LedgerTransaction mockSavedLt = LedgerTransaction.builder()
                .id(202L)
                .ledgerTransactionNo("WO-20261104-00109")
                .patientId(101L)
                .grossAmount(BigDecimal.valueOf(1500.00))
                .discountAmount(BigDecimal.valueOf(150.00))
                .netAmount(BigDecimal.valueOf(1350.00))
                .adjustment(BigDecimal.valueOf(1350.00))
                .balanceAmount(BigDecimal.ZERO)
                .build();
        when(ltRepository.save(any(LedgerTransaction.class))).thenReturn(mockSavedLt);

        Receipt mockSavedReceipt = Receipt.builder()
                .id(303L)
                .receiptNo("REC-20261104-00086")
                .amountPaid(BigDecimal.valueOf(1350.00))
                .build();
        when(receiptRepository.save(any(Receipt.class))).thenReturn(mockSavedReceipt);

        RegistrationRequestDTO request = RegistrationRequestDTO.builder()
                .patientData(RegistrationRequestDTO.PatientDataDto.builder()
                        .title("Mr.")
                        .pName("John Doe")
                        .gender("Male")
                        .ageYear(35)
                        .mobile("9876543210")
                        .build())
                .ltData(RegistrationRequestDTO.LTDataDto.builder()
                        .doctorID(1)
                        .doctorName("Dr. Rajesh Sharma")
                        .centreID(1)
                        .rateTypeID(1)
                        .grossAmount(BigDecimal.valueOf(1500.00))
                        .discountOnTotal(BigDecimal.valueOf(150.00))
                        .discountReason("Senior Citizen")
                        .netAmount(BigDecimal.valueOf(1350.00))
                        .adjustment(BigDecimal.valueOf(1350.00))
                        .build())
                .plo(List.of(
                        RegistrationRequestDTO.PLODto.builder()
                                .investigationID(1)
                                .itemName("Complete Blood Count (CBC)")
                                .barcodeNo("BAR1001")
                                .rate(BigDecimal.valueOf(500.00))
                                .amount(BigDecimal.valueOf(450.00))
                                .build(),
                        RegistrationRequestDTO.PLODto.builder()
                                .investigationID(2)
                                .itemName("Lipid Profile")
                                .barcodeNo("BAR1002")
                                .rate(BigDecimal.valueOf(1000.00))
                                .amount(BigDecimal.valueOf(900.00))
                                .build()
                ))
                .rcData(List.of(
                        RegistrationRequestDTO.RcDataDto.builder()
                                .amountPaid(BigDecimal.valueOf(1350.00))
                                .paymentMode("UPI")
                                .transactionNo("UPI-REF-998877")
                                .build()
                ))
                .build();

        Map<String, Object> result = service.registerPatientAndBookWorkOrder(request, 1, 1);

        assertNotNull(result);
        assertEquals("LR2026-000043", result.get("PatientID"));
        assertTrue(((String) result.get("LedgerTransactionNo")).startsWith("WO-"));
        assertTrue(((String) result.get("ReceiptNo")).startsWith("REC-"));
        assertEquals(0, ((BigDecimal) result.get("GrossAmount")).compareTo(BigDecimal.valueOf(1500.00)));
        assertEquals(0, ((BigDecimal) result.get("DiscountAmount")).compareTo(BigDecimal.valueOf(150.00)));
        assertEquals(0, ((BigDecimal) result.get("NetAmount")).compareTo(BigDecimal.valueOf(1350.00)));
        assertEquals(0, ((BigDecimal) result.get("Adjustment")).compareTo(BigDecimal.valueOf(1350.00)));
        assertEquals(0, ((BigDecimal) result.get("BalanceAmount")).compareTo(BigDecimal.ZERO));
        assertEquals("John Doe", result.get("PatientName"));
        assertEquals("SUCCESS", result.get("Status"));
    }
}
