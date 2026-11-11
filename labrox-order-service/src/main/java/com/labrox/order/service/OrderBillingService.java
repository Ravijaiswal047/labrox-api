package com.labrox.order.service;

import com.labrox.order.domain.LedgerTransaction;
import com.labrox.order.domain.PatientMaster;
import com.labrox.order.domain.Receipt;
import com.labrox.order.dto.RegistrationRequestDTO;
import com.labrox.order.repository.LedgerTransactionRepository;
import com.labrox.order.repository.PatientMasterRepository;
import com.labrox.order.repository.ReceiptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderBillingService {

    private final PatientMasterRepository patientRepository;
    private final LedgerTransactionRepository ltRepository;
    private final ReceiptRepository receiptRepository;

    public OrderBillingService(
            PatientMasterRepository patientRepository,
            LedgerTransactionRepository ltRepository,
            ReceiptRepository receiptRepository) {
        this.patientRepository = patientRepository;
        this.ltRepository = ltRepository;
        this.receiptRepository = receiptRepository;
    }

    @Transactional
    public Map<String, Object> registerPatientAndBookWorkOrder(RegistrationRequestDTO request, Integer companyId, Integer userId) {
        final int targetCompanyId = (companyId != null) ? companyId : 1;
        final int targetUserId = (userId != null) ? userId : 1;

        RegistrationRequestDTO.PatientDataDto pDto = request.getPatientData();
        if (pDto == null) {
            throw new IllegalArgumentException("Patient data is required");
        }

        RegistrationRequestDTO.LTDataDto ltDto = request.getLtData();

        // 1. Resolve or Create Patient (UHID)
        String resolvedName = resolvePatientName(pDto, ltDto);
        PatientMaster patient;
        if (pDto.getPatientID() != null && !pDto.getPatientID().trim().isEmpty()) {
            Optional<PatientMaster> existing = patientRepository.findByCompanyIdAndUhid(targetCompanyId, pDto.getPatientID().trim());
            if (existing.isPresent()) {
                patient = existing.get();
                if (patient.getName() == null || patient.getName().trim().isEmpty() || patient.getName().equalsIgnoreCase("Unknown")) {
                    patient.setName(resolvedName);
                    patient = patientRepository.save(patient);
                }
            } else {
                patient = createNewPatient(pDto, ltDto, targetCompanyId);
            }
        } else {
            patient = createNewPatient(pDto, ltDto, targetCompanyId);
        }

        // 2. Create Ledger Transaction (Work Order)
        String workOrderNo = generateWorkOrderNo(targetCompanyId);

        BigDecimal gross = (ltDto != null && ltDto.getGrossAmount() != null) ? ltDto.getGrossAmount() : BigDecimal.ZERO;
        BigDecimal disc = (ltDto != null && ltDto.getDiscountOnTotal() != null) ? ltDto.getDiscountOnTotal() : BigDecimal.ZERO;
        BigDecimal net = gross.subtract(disc);
        BigDecimal adj = (ltDto != null && ltDto.getAdjustment() != null) ? ltDto.getAdjustment() : BigDecimal.ZERO;
        BigDecimal bal = net.subtract(adj);

        LedgerTransaction lt = LedgerTransaction.builder()
                .companyId(targetCompanyId)
                .ledgerTransactionNo(workOrderNo)
                .patientId(patient.getId())
                .centreId((ltDto != null && ltDto.getCentreID() != null) ? ltDto.getCentreID() : 1)
                .rateTypeId((ltDto != null && ltDto.getRateTypeID() != null) ? ltDto.getRateTypeID() : 1)
                .doctorId((ltDto != null) ? ltDto.getDoctorID() : 1)
                .doctorName((ltDto != null && ltDto.getDoctorName() != null) ? ltDto.getDoctorName() : "Self")
                .grossAmount(gross)
                .discountAmount(disc)
                .discountReason((ltDto != null) ? ltDto.getDiscountReason() : "")
                .netAmount(net)
                .adjustment(adj)
                .balanceAmount(bal)
                .isCredit(ltDto != null && ltDto.getIsCredit() != null && ltDto.getIsCredit() == 1)
                .status("REGISTERED")
                .createdBy(targetUserId)
                .createdAt(OffsetDateTime.now())
                .build();

        lt = ltRepository.save(lt);

        // 3. Process Receipts (Cashier POS)
        String receiptNo = null;
        if (request.getRcData() != null && !request.getRcData().isEmpty()) {
            for (RegistrationRequestDTO.RcDataDto rc : request.getRcData()) {
                if (rc.getAmountPaid() != null && rc.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
                    receiptNo = generateReceiptNo(targetCompanyId);
                    Receipt r = Receipt.builder()
                            .companyId(targetCompanyId)
                            .receiptNo(receiptNo)
                            .ledgerTransactionNo(workOrderNo)
                            .patientId(patient.getId())
                            .amountPaid(rc.getAmountPaid())
                            .paymentMode(rc.getPaymentMode() != null ? rc.getPaymentMode() : "Cash")
                            .bankName(rc.getBankName())
                            .cardNo(rc.getCardNo())
                            .transactionNo(rc.getTransactionNo())
                            .centreId(lt.getCentreId())
                            .createdBy(targetUserId)
                            .createdAt(OffsetDateTime.now())
                            .build();
                    receiptRepository.save(r);
                }
            }
        }

        // 4. Dispatch to Phlebotomy / Sample Collection Service
        try {
            forwardToSampleService(patient, lt, request.getPlo());
        } catch (Exception ignored) {}

        Map<String, Object> result = new HashMap<>();
        result.put("PatientID", patient.getUhid());
        result.put("patientID", patient.getUhid());
        result.put("LedgerTransactionNo", workOrderNo);
        result.put("ledgerTransactionNo", workOrderNo);
        result.put("VisitNo", workOrderNo);
        result.put("visitNo", workOrderNo);
        result.put("LedgerTransactionID", lt.getId());
        result.put("ledgerTransactionID", lt.getId());
        result.put("ReceiptNo", receiptNo != null ? receiptNo : "");
        result.put("GrossAmount", gross);
        result.put("DiscountAmount", disc);
        result.put("NetAmount", net);
        result.put("Adjustment", adj);
        result.put("BalanceAmount", bal);
        result.put("PatientName", patient.getName());
        result.put("PName", patient.getName());
        result.put("Mobile", patient.getMobile());
        result.put("Status", "SUCCESS");

        return result;
    }

    private void forwardToSampleService(PatientMaster patient, LedgerTransaction lt, java.util.List<RegistrationRequestDTO.PLODto> plo) {
        try {
            Map<String, Object> payload = new HashMap<>();

            Map<String, Object> patientData = new HashMap<>();
            patientData.put("PatientID", patient.getUhid());
            patientData.put("PName", patient.getName());
            patientData.put("PatientName", patient.getName());
            patientData.put("Title", patient.getTitle());
            patientData.put("Gender", patient.getGender());
            patientData.put("Age", (patient.getAgeYear() != null ? patient.getAgeYear() : 30) + " Y");
            patientData.put("AgeYear", patient.getAgeYear());
            patientData.put("Mobile", patient.getMobile());
            patientData.put("City", patient.getCity());
            patientData.put("State", patient.getState());
            payload.put("PatientData", patientData);

            Map<String, Object> ltData = new HashMap<>();
            ltData.put("LedgerTransactionID", lt.getId());
            ltData.put("LedgerTransactionNo", lt.getLedgerTransactionNo());
            ltData.put("VisitNo", lt.getLedgerTransactionNo());
            ltData.put("PName", patient.getName());
            ltData.put("PatientName", patient.getName());
            ltData.put("DoctorID", lt.getDoctorId());
            ltData.put("DoctorName", lt.getDoctorName());
            ltData.put("CentreID", lt.getCentreId());
            ltData.put("GrossAmount", lt.getGrossAmount());
            ltData.put("NetAmount", lt.getNetAmount());
            payload.put("LTData", ltData);

            java.util.List<Map<String, Object>> ploList = new java.util.ArrayList<>();
            if (plo != null && !plo.isEmpty()) {
                for (RegistrationRequestDTO.PLODto p : plo) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("InvestigationID", p.getInvestigationID());
                    item.put("ItemName", p.getItemName());
                    item.put("TestName", p.getItemName());
                    item.put("Rate", p.getRate());
                    item.put("SampleTypeID", p.getSampleTypeID());
                    ploList.add(item);
                }
            }
            payload.put("PLO", ploList);

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String json = mapper.writeValueAsString(payload);

            java.net.URL url = new java.net.URL("http://localhost:8083/api/v1/SC/RegisterWorkOrder");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            try (java.io.OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                org.slf4j.LoggerFactory.getLogger(OrderBillingService.class).info("Successfully dispatched work order to sample-service: {}", lt.getLedgerTransactionNo());
            } else {
                org.slf4j.LoggerFactory.getLogger(OrderBillingService.class).warn("Failed to dispatch work order to sample-service, HTTP response code: {}", code);
            }
            conn.disconnect();
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(OrderBillingService.class).error("Error dispatching work order to sample-service: {}", e.getMessage());
        }
    }

    private PatientMaster createNewPatient(RegistrationRequestDTO.PatientDataDto pDto, RegistrationRequestDTO.LTDataDto ltDto, Integer companyId) {
        String uhid = generateUhid(companyId);
        int totalAgeDays = (pDto.getTotalAgeInDays() != null && pDto.getTotalAgeInDays() > 0)
                ? pDto.getTotalAgeInDays()
                : (pDto.getAgeYear() != null ? pDto.getAgeYear() * 365 : 30 * 365);

        String resolvedName = resolvePatientName(pDto, ltDto);

        PatientMaster p = PatientMaster.builder()
                .companyId(companyId)
                .uhid(uhid)
                .title(pDto.getTitle() != null ? pDto.getTitle() : "Mr.")
                .name(resolvedName)
                .gender(pDto.getGender() != null ? pDto.getGender() : "Male")
                .ageYear(pDto.getAgeYear() != null ? pDto.getAgeYear() : 30)
                .ageMonth(pDto.getAgeMonth() != null ? pDto.getAgeMonth() : 0)
                .ageDays(pDto.getAgeDays() != null ? pDto.getAgeDays() : 0)
                .totalAgeInDays(totalAgeDays)
                .mobile(pDto.getMobile() != null ? pDto.getMobile().trim() : "0000000000")
                .email(pDto.getEmail())
                .address(pDto.getStreetName() != null ? pDto.getStreetName() : pDto.getHouseNo())
                .city(pDto.getCity())
                .state(pDto.getState())
                .pincode(pDto.getPinCode())
                .isVip(pDto.getVip() != null && pDto.getVip() == 1)
                .active(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        return patientRepository.save(p);
    }

    private String resolvePatientName(RegistrationRequestDTO.PatientDataDto pDto, RegistrationRequestDTO.LTDataDto ltDto) {
        if (pDto != null) {
            if (pDto.getPName() != null && !pDto.getPName().trim().isEmpty() && !pDto.getPName().trim().equalsIgnoreCase("Unknown")) {
                return pDto.getPName().trim();
            }
            if (pDto.getPatientName() != null && !pDto.getPatientName().trim().isEmpty() && !pDto.getPatientName().trim().equalsIgnoreCase("Unknown")) {
                return pDto.getPatientName().trim();
            }
            StringBuilder sb = new StringBuilder();
            if (pDto.getTitle() != null && !pDto.getTitle().trim().isEmpty()) {
                sb.append(pDto.getTitle().trim()).append(" ");
            }
            if (pDto.getFirstName() != null && !pDto.getFirstName().trim().isEmpty()) {
                sb.append(pDto.getFirstName().trim());
            }
            if (pDto.getMiddleName() != null && !pDto.getMiddleName().trim().isEmpty()) {
                sb.append(" ").append(pDto.getMiddleName().trim());
            }
            if (pDto.getLastName() != null && !pDto.getLastName().trim().isEmpty()) {
                sb.append(" ").append(pDto.getLastName().trim());
            }
            String constructed = sb.toString().trim();
            if (!constructed.isEmpty() && !constructed.equalsIgnoreCase(pDto.getTitle() != null ? pDto.getTitle().trim() : "")) {
                return constructed;
            }
        }
        if (ltDto != null) {
            if (ltDto.getPName() != null && !ltDto.getPName().trim().isEmpty() && !ltDto.getPName().trim().equalsIgnoreCase("Unknown")) {
                return ltDto.getPName().trim();
            }
            if (ltDto.getPatientName() != null && !ltDto.getPatientName().trim().isEmpty() && !ltDto.getPatientName().trim().equalsIgnoreCase("Unknown")) {
                return ltDto.getPatientName().trim();
            }
        }
        return "Walk-in Patient";
    }

    private String generateUhid(Integer companyId) {
        long count = patientRepository.countByCompanyId(companyId) + 1;
        int year = LocalDate.now().getYear();
        return String.format("LR%d-%06d", year, count);
    }

    private String generateWorkOrderNo(Integer companyId) {
        long count = ltRepository.countByCompanyId(companyId) + 1;
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("WO-%s-%05d", dateStr, count);
    }

    private String generateReceiptNo(Integer companyId) {
        long count = receiptRepository.countByCompanyId(companyId) + 1;
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("REC-%s-%05d", dateStr, count);
    }
}
