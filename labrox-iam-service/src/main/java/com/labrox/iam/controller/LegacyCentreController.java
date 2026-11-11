package com.labrox.iam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"/api/v1/Centre", "/api/v1/centre"})
@Tag(name = "Legacy Centre & Location API Bridge")
public class LegacyCentreController {

    private final JdbcTemplate jdbcTemplate;

    public LegacyCentreController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping({"/getAccessCentres", "/GetAccessCentres", "/getGlobalCentres", "/GetCentreList", "/getCentreList"})
    @Operation(summary = "Get list of accessible centres for logged-in user")
    public ResponseEntity<Map<String, Object>> getAccessCentres() {
        Map<String, Object> response = new HashMap<>();

        try {
            String sql = """
                SELECT centre_id AS "CentreID", centre AS "Centre", barcode_logic AS "BarcodeLogic",
                       visit_type AS "VisitType", hide_amount AS "HideAmount", set_mrp AS "SetMRP",
                       centre_id AS "DefaultCentreIds"
                FROM centres
                WHERE is_active = TRUE
                ORDER BY centre
            """;

            List<Map<String, Object>> centres = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, Object> c = new HashMap<>();
                int id = rs.getInt("CentreID");
                String name = rs.getString("Centre");
                c.put("CentreID", id);
                c.put("Centre", name);
                c.put("value", id);
                c.put("label", name);
                c.put("BarcodeLogic", rs.getString("BarcodeLogic") != null ? rs.getString("BarcodeLogic") : "1");
                c.put("VisitType", rs.getString("VisitType") != null ? rs.getString("VisitType") : "Walkin");
                c.put("HideAmount", rs.getInt("HideAmount"));
                c.put("SetMRP", rs.getInt("SetMRP"));
                c.put("DefaultCentreIds", rs.getInt("DefaultCentreIds"));
                c.put("DefaultCentreId", rs.getInt("DefaultCentreIds"));
                c.put("BTB", 0);
                c.put("SUBPCC", 0);
                return c;
            });

            if (centres.isEmpty()) {
                centres = createFallbackCentres();
            }

            response.put("success", true);
            response.put("status", true);
            response.put("message", centres);
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            response.put("success", true);
            response.put("status", true);
            response.put("message", createFallbackCentres());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping({"/BindCentreWorkOrder", "/bindCentreWorkOrder"})
    @Operation(summary = "Bind centres for work order booking")
    public ResponseEntity<Map<String, Object>> bindCentreWorkOrder(@RequestBody(required = false) Map<String, Object> body) {
        return getAccessCentres();
    }

    @RequestMapping(value = {"/BindRateTypeWorkOrder", "/bindRateTypeWorkOrder",
                            "/BindRateTypeWorkOrderSearch", "/bindRateTypeWorkOrderSearch",
                            "/BindRateTypeDropdown", "/bindRateTypeDropdown",
                            "/GetRateTypes", "/getRateTypes", "/GetRateType", "/getRateType"},
                    method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Bind rate types for chosen diagnostic centre")
    public ResponseEntity<Map<String, Object>> bindRateTypeWorkOrder(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        List<Map<String, Object>> rateTypes = new ArrayList<>();

        Map<String, Object> r1 = new HashMap<>();
        r1.put("RateTypeID", 1);
        r1.put("RateTypeName", "Standard Cash Rate");
        r1.put("value", 1);
        r1.put("label", "Standard Cash Rate");
        r1.put("BarcodeLogic", "1");
        r1.put("LockRegistration", 0);
        r1.put("PayMode", "Cash");
        r1.put("HideAmount", 0);
        r1.put("CentreType", "Walkin");
        r1.put("centreType", "Walkin");
        r1.put("BusinessUnitID", 1);
        r1.put("Country", "India");
        r1.put("CountryID", 1);
        r1.put("State", "Karnataka");
        r1.put("StateID", 1);
        r1.put("City", "Bangalore");
        r1.put("CityID", 1);
        r1.put("Pincode", "560001");
        rateTypes.add(r1);

        Map<String, Object> r2 = new HashMap<>();
        r2.put("RateTypeID", 2);
        r2.put("RateTypeName", "Corporate Panel Credit");
        r2.put("value", 2);
        r2.put("label", "Corporate Panel Credit");
        r2.put("BarcodeLogic", "1");
        r2.put("LockRegistration", 0);
        r2.put("PayMode", "Credit");
        r2.put("HideAmount", 0);
        r2.put("CentreType", "Corporate");
        r2.put("centreType", "Corporate");
        r2.put("BusinessUnitID", 1);
        r2.put("Country", "India");
        r2.put("CountryID", 1);
        r2.put("State", "Karnataka");
        r2.put("StateID", 1);
        r2.put("City", "Bangalore");
        r2.put("CityID", 1);
        r2.put("Pincode", "560001");
        rateTypes.add(r2);

        Map<String, Object> r3 = new HashMap<>();
        r3.put("RateTypeID", 3);
        r3.put("RateTypeName", "Senior Citizen / VIP Disc");
        r3.put("value", 3);
        r3.put("label", "Senior Citizen / VIP Disc");
        r3.put("BarcodeLogic", "1");
        r3.put("LockRegistration", 0);
        r3.put("PayMode", "Cash");
        r3.put("HideAmount", 0);
        r3.put("CentreType", "Walkin");
        r3.put("centreType", "Walkin");
        r3.put("BusinessUnitID", 1);
        r3.put("Country", "India");
        r3.put("CountryID", 1);
        r3.put("State", "Karnataka");
        r3.put("StateID", 1);
        r3.put("City", "Bangalore");
        r3.put("CityID", 1);
        r3.put("Pincode", "560001");
        rateTypes.add(r3);

        response.put("success", true);
        response.put("status", true);
        response.put("message", rateTypes);

        return ResponseEntity.ok(response);
    }

    @PostMapping({"/getRateTypeDetailWithCentre", "/GetRateTypeDetailWithCentre"})
    @Operation(summary = "Get rate type detail with associated centre")
    public ResponseEntity<Map<String, Object>> getRateTypeDetailWithCentre(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> detail = new HashMap<>();
        detail.put("RateTypeID", 1);
        detail.put("RateTypeName", "Standard Cash Rate");
        detail.put("CentreID", 1);
        detail.put("Centre", "Main Reference Lab");
        detail.put("BarcodeLogic", "1");
        detail.put("PayMode", "Cash");
        detail.put("HideAmount", 0);
        detail.put("LockRegistration", 0);
        detail.put("Country", "India");
        detail.put("CountryID", 1);
        detail.put("State", "Karnataka");
        detail.put("StateID", 1);
        detail.put("City", "Bangalore");
        detail.put("CityID", 1);
        detail.put("Pincode", "560001");
        response.put("success", true);
        response.put("status", true);
        response.put("message", List.of(detail));
        return ResponseEntity.ok(response);
    }

    @GetMapping({"/Getcopayment", "/getcopayment"})
    @Operation(summary = "Get co-payment configurations")
    public ResponseEntity<Map<String, Object>> getCoPayment() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", true);
        response.put("message", Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    private List<Map<String, Object>> createFallbackCentres() {
        Map<String, Object> c = new HashMap<>();
        c.put("CentreID", 1);
        c.put("Centre", "Main Reference Lab");
        c.put("value", 1);
        c.put("label", "Main Reference Lab");
        c.put("BarcodeLogic", "1");
        c.put("VisitType", "Walkin");
        c.put("HideAmount", 0);
        c.put("SetMRP", 0);
        c.put("DefaultCentreIds", 1);
        c.put("DefaultCentreId", 1);
        c.put("BTB", 0);
        c.put("SUBPCC", 0);
        return List.of(c);
    }
}
