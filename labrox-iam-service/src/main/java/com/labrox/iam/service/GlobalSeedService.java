package com.labrox.iam.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class GlobalSeedService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, List<Map<String, Object>>> globalsByType = new ConcurrentHashMap<>();
    private final List<Map<String, Object>> genderMappings = new CopyOnWriteArrayList<>();
    private final List<Map<String, Object>> centres = new CopyOnWriteArrayList<>();
    private final List<Map<String, Object>> rateTypes = new CopyOnWriteArrayList<>();
    private final List<Map<String, Object>> doctors = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("global_seed.json");
            if (!resource.exists()) {
                log.warn("global_seed.json not found on classpath!");
                initFallbacks();
                return;
            }

            try (InputStream is = resource.getInputStream()) {
                Map<String, Object> data = objectMapper.readValue(is, new TypeReference<Map<String, Object>>() {});

                // 1. Globals
                List<Map<String, Object>> rawGlobals = (List<Map<String, Object>>) data.get("globals");
                if (rawGlobals != null) {
                    for (Map<String, Object> item : rawGlobals) {
                        Object typeObj = item.get("FieldType");
                        if (typeObj != null) {
                            String type = typeObj.toString().trim().toLowerCase();
                            globalsByType.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(item);
                        }
                    }
                    log.info("Loaded {} global items across {} types from global_seed.json", rawGlobals.size(), globalsByType.size());
                }

                // 2. Gender mappings
                List<Map<String, Object>> rawGender = (List<Map<String, Object>>) data.get("genderMapping");
                if (rawGender != null) {
                    genderMappings.addAll(rawGender);
                    log.info("Loaded {} gender-title mappings from global_seed.json", genderMappings.size());
                }

                // 3. Centres
                List<Map<String, Object>> rawCentres = (List<Map<String, Object>>) data.get("centres");
                if (rawCentres != null) {
                    centres.addAll(rawCentres);
                    log.info("Loaded {} centres from global_seed.json", centres.size());
                }

                // 4. Rate types
                List<Map<String, Object>> rawRates = (List<Map<String, Object>>) data.get("rateTypes");
                if (rawRates != null) {
                    rateTypes.addAll(rawRates);
                    log.info("Loaded {} rate types from global_seed.json", rateTypes.size());
                }

                // 5. Doctors
                List<Map<String, Object>> rawDocs = (List<Map<String, Object>>) data.get("doctors");
                if (rawDocs != null) {
                    doctors.addAll(rawDocs);
                    log.info("Loaded {} referral doctors from global_seed.json", doctors.size());
                }
            }
        } catch (Exception ex) {
            log.error("Failed to load global_seed.json, initializing defaults: {}", ex.getMessage());
            initFallbacks();
        }
    }

    private void initFallbacks() {
        // Fallback for Title
        List<Map<String, Object>> titleList = List.of(
            Map.of("FieldID", 1, "FieldType", "Title", "FieldDisplay", "Mr.", "PrintSequence", 1, "IsActive", 1),
            Map.of("FieldID", 2, "FieldType", "Title", "FieldDisplay", "Mrs.", "PrintSequence", 2, "IsActive", 1),
            Map.of("FieldID", 3, "FieldType", "Title", "FieldDisplay", "Miss", "PrintSequence", 3, "IsActive", 1),
            Map.of("FieldID", 4, "FieldType", "Title", "FieldDisplay", "Ms.", "PrintSequence", 4, "IsActive", 1),
            Map.of("FieldID", 5, "FieldType", "Title", "FieldDisplay", "Master", "PrintSequence", 5, "IsActive", 1),
            Map.of("FieldID", 6, "FieldType", "Title", "FieldDisplay", "Baby", "PrintSequence", 6, "IsActive", 1),
            Map.of("FieldID", 7, "FieldType", "Title", "FieldDisplay", "Dr.", "PrintSequence", 7, "IsActive", 1),
            Map.of("FieldID", 8, "FieldType", "Title", "FieldDisplay", "Dr.(Mr)", "PrintSequence", 8, "IsActive", 1),
            Map.of("FieldID", 9, "FieldType", "Title", "FieldDisplay", "Dr.(Mrs)", "PrintSequence", 9, "IsActive", 1),
            Map.of("FieldID", 10, "FieldType", "Title", "FieldDisplay", "Prof.", "PrintSequence", 10, "IsActive", 1),
            Map.of("FieldID", 11, "FieldType", "Title", "FieldDisplay", "Other", "PrintSequence", 11, "IsActive", 1)
        );
        globalsByType.put("title", new CopyOnWriteArrayList<>(titleList));

        // Fallback for Gender
        List<Map<String, Object>> genderList = List.of(
            Map.of("FieldID", 1, "FieldType", "Gender", "FieldDisplay", "Male", "PrintSequence", 1, "IsActive", 1),
            Map.of("FieldID", 2, "FieldType", "Gender", "FieldDisplay", "Female", "PrintSequence", 2, "IsActive", 1),
            Map.of("FieldID", 3, "FieldType", "Gender", "FieldDisplay", "Transgender", "PrintSequence", 3, "IsActive", 1)
        );
        globalsByType.put("gender", new CopyOnWriteArrayList<>(genderList));

        // Fallback for PaymentMode
        List<Map<String, Object>> payList = List.of(
            Map.of("FieldID", 134, "PaymentModeID", 134, "FieldType", "PaymentMode", "FieldDisplay", "Cash", "PaymentMode", "Cash", "PrintSequence", 1, "IsActive", 1),
            Map.of("FieldID", 135, "PaymentModeID", 135, "FieldType", "PaymentMode", "FieldDisplay", "Credit Card", "PaymentMode", "Credit Card", "PrintSequence", 2, "IsActive", 1),
            Map.of("FieldID", 145, "PaymentModeID", 145, "FieldType", "PaymentMode", "FieldDisplay", "Cheque", "PaymentMode", "Cheque", "PrintSequence", 3, "IsActive", 1),
            Map.of("FieldID", 215, "PaymentModeID", 215, "FieldType", "PaymentMode", "FieldDisplay", "Credit", "PaymentMode", "Credit", "PrintSequence", 4, "IsActive", 1),
            Map.of("FieldID", 124, "PaymentModeID", 124, "FieldType", "PaymentMode", "FieldDisplay", "Online Payment", "PaymentMode", "Online Payment", "PrintSequence", 5, "IsActive", 1)
        );
        globalsByType.put("paymentmode", new CopyOnWriteArrayList<>(payList));
    }

    public List<Map<String, Object>> getGlobalData(String type) {
        if (type == null) type = "PaymentMode";
        String key = type.trim().toLowerCase();

        List<Map<String, Object>> list = globalsByType.get(key);
        if (list == null || list.isEmpty()) {
            if ("wardno".equals(key)) {
                return List.of(
                    Map.of("FieldID", 1, "FieldDisplay", "General Ward - 101", "value", "General Ward - 101", "label", "General Ward - 101"),
                    Map.of("FieldID", 2, "FieldDisplay", "Semi-Private Ward - 201", "value", "Semi-Private Ward - 201", "label", "Semi-Private Ward - 201"),
                    Map.of("FieldID", 3, "FieldDisplay", "Private Deluxe Ward - 301", "value", "Private Deluxe Ward - 301", "label", "Private Deluxe Ward - 301"),
                    Map.of("FieldID", 4, "FieldDisplay", "ICU / CCU", "value", "ICU / CCU", "label", "ICU / CCU")
                );
            }
            if ("visittype".equals(key)) {
                return List.of(
                    Map.of("FieldID", 1, "FieldDisplay", "Center Visit", "value", 1, "label", "Center Visit"),
                    Map.of("FieldID", 2, "FieldDisplay", "Home Collection", "value", 2, "label", "Home Collection")
                );
            }
            // General fallback returning valid FieldDisplay and FieldID
            return List.of(
                Map.of("FieldID", 1, "FieldDisplay", "Standard", "value", "Standard", "label", "Standard", "ID", 1, "Name", "Standard")
            );
        }

        // Map items to standard format with FieldID, FieldDisplay, value, label
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Integer> vetCanineMap = new HashMap<>();
        for (Map<String, Object> gm : genderMappings) {
            Object t = gm.get("Title");
            Object vet = gm.get("IsVetCanine");
            if (t != null && vet != null) {
                vetCanineMap.put(t.toString().trim(), ((Number) vet).intValue());
            }
        }

        for (Map<String, Object> item : list) {
            Map<String, Object> row = new HashMap<>(item);
            Object display = item.get("FieldDisplay");
            Object id = item.get("FieldID");

            if (display == null || display.toString().trim().isEmpty()) {
                continue; // Skip empty display
            }

            String displayStr = display.toString().trim();
            row.put("FieldDisplay", displayStr);
            row.put("label", displayStr);
            row.put("FieldID", id != null ? id : 1);

            if ("title".equals(key)) {
                row.put("value", displayStr);
                row.put("IsVetCanine", vetCanineMap.getOrDefault(displayStr, 0));
                row.put("isVetCanine", vetCanineMap.getOrDefault(displayStr, 0));
            } else if ("gender".equals(key)) {
                row.put("value", displayStr);
            } else if ("paymentmode".equals(key)) {
                row.put("value", id != null ? id : 134);
                row.put("PaymentMode", displayStr);
                row.put("PaymentModeID", id != null ? id : 134);
            } else if ("visittype".equals(key)) {
                row.put("value", id != null ? id : 1);
            } else if ("bankname".equals(key)) {
                row.put("value", displayStr);
            } else if ("hlmpatienttype".equals(key)) {
                row.put("value", displayStr);
            } else if ("source".equals(key)) {
                row.put("value", displayStr);
            } else if ("identity".equals(key)) {
                row.put("value", id != null ? id : 1);
            } else {
                row.put("value", displayStr);
            }

            result.add(row);
        }

        return result;
    }

    public List<Map<String, Object>> getDoctors() {
        if (!doctors.isEmpty()) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (Map<String, Object> d : doctors) {
                Map<String, Object> row = new HashMap<>(d);
                Object id = d.get("DoctorReferalID");
                Object name = d.get("Name");
                row.put("DoctorID", id);
                row.put("DoctorName", name);
                row.put("value", id);
                row.put("label", name);
                list.add(row);
            }
            return list;
        }

        return List.of(
            Map.of("DoctorID", 1, "DoctorReferalID", 1, "Name", "Self / Walk-in Patient", "DoctorName", "Self / Walk-in Patient", "DoctorCode", "DOC001", "Specialization", "General", "value", 1, "label", "Self / Walk-in Patient"),
            Map.of("DoctorID", 2, "DoctorReferalID", 2, "Name", "Dr. Rajesh Sharma, MD (Pathology)", "DoctorName", "Dr. Rajesh Sharma, MD (Pathology)", "DoctorCode", "DOC002", "Specialization", "Pathology", "value", 2, "label", "Dr. Rajesh Sharma, MD (Pathology)"),
            Map.of("DoctorID", 3, "DoctorReferalID", 3, "Name", "Dr. Ananya Roy, MS (Surgery)", "DoctorName", "Dr. Ananya Roy, MS (Surgery)", "DoctorCode", "DOC003", "Specialization", "Surgery", "value", 3, "label", "Dr. Ananya Roy, MS (Surgery)")
        );
    }

    public List<Map<String, Object>> getDiscApprovalEmployees() {
        return List.of(
            Map.of("EmployeeID", 1, "Name", "Super Administrator", "value", 1, "label", "Super Administrator"),
            Map.of("EmployeeID", 2, "Name", "Dr. Rajesh Sharma (Medical Director)", "value", 2, "label", "Dr. Rajesh Sharma (Medical Director)"),
            Map.of("EmployeeID", 3, "Name", "Finance & Accounts Manager", "value", 3, "label", "Finance & Accounts Manager")
        );
    }
}
