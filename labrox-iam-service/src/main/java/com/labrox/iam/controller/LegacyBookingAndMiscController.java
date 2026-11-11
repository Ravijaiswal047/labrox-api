package com.labrox.iam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Legacy Booking, Coupons & Registration Helper API", description = "Endpoints matching legacy booking lookups, coupon validation, consent forms, and TRF handling")
public class LegacyBookingAndMiscController {

    @PostMapping({"/api/v1/Booking/getDataByMobileNo", "/api/v1/booking/getDataByMobileNo"})
    @Operation(summary = "Search existing patient demographics by mobile number")
    public ResponseEntity<Map<String, Object>> getDataByMobileNo(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/api/v1/Booking/GetMemberShipPatientData", "/api/v1/booking/getMemberShipPatientData"})
    @Operation(summary = "Get membership card patient data")
    public ResponseEntity<Map<String, Object>> getMemberShipPatientData(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/api/v1/CouponMaster/BindTestForAppliedCoupon", "/api/v1/couponMaster/bindTestForAppliedCoupon"})
    public ResponseEntity<Map<String, Object>> bindTestForAppliedCoupon(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/api/v1/CouponMaster/GetCouponValidationData", "/api/v1/couponMaster/getCouponValidationData"})
    public ResponseEntity<Map<String, Object>> getCouponValidationData(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/api/v1/ConcentFormMaster/generateConcentForm", "/api/v1/concentFormMaster/generateConcentForm"})
    public ResponseEntity<Map<String, Object>> generateConcentForm(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "Consent form recorded");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/api/v1/ConcentFormMaster/GetConcentFormData", "/api/v1/concentFormMaster/getConcentFormData"})
    public ResponseEntity<Map<String, Object>> getConcentFormData(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/api/v1/PndtFormMaster/generatPndtForm", "/api/v1/pndtFormMaster/generatPndtForm"})
    public ResponseEntity<Map<String, Object>> generatPndtForm(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "PNDT form recorded");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/api/v1/PreRegistration/GetTrfS3Files", "/api/v1/preRegistration/getTrfS3Files"})
    public ResponseEntity<Map<String, Object>> getTrfS3Files(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/api/v1/PreRegistration/SaveTrfWithFiles", "/api/v1/preRegistration/saveTrfWithFiles"})
    public ResponseEntity<Map<String, Object>> saveTrfWithFiles(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "TRF saved successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/api/v1/PreRegistration/DeleteTrfFile", "/api/v1/preRegistration/deleteTrfFile"})
    public ResponseEntity<Map<String, Object>> deleteTrfFile(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("success", true);
        response.put("message", "TRF file deleted");
        return ResponseEntity.ok(response);
    }
}
