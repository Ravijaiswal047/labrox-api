package com.labrox.iam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/v1/CommonHC", "/api/v1/commonHC"})
@Tag(name = "Legacy Common Geographic & Demographic Bridge")
public class LegacyCommonHCController {

    private static final List<Map<String, Object>> COUNTRIES = new ArrayList<>();
    private static final List<Map<String, Object>> STATES = new ArrayList<>();
    private static final List<Map<String, Object>> CITIES = new ArrayList<>();

    static {
        // Seed Countries
        addCountry(1, "India", 1);
        addCountry(2, "United States", 0);
        addCountry(3, "Kenya", 0);
        addCountry(4, "United Arab Emirates", 0);

        // Seed States (India - CountryID: 1)
        addState(1, "Karnataka", 1);
        addState(2, "Maharashtra", 1);
        addState(3, "Delhi", 1);
        addState(4, "Telangana", 1);
        addState(5, "Tamil Nadu", 1);
        addState(6, "Gujarat", 1);
        addState(7, "Uttar Pradesh", 1);
        addState(8, "West Bengal", 1);
        addState(9, "Rajasthan", 1);
        addState(10, "Kerala", 1);
        addState(11, "Andhra Pradesh", 1);
        addState(12, "Punjab", 1);
        addState(13, "Haryana", 1);
        addState(14, "Bihar", 1);
        addState(15, "Madhya Pradesh", 1);
        addState(16, "Goa", 1);
        addState(17, "Odisha", 1);

        // Seed Cities (Karnataka - StateID: 1)
        int cityId = 1;
        cityId = addCity(cityId, "Bangalore", 1, 1);
        cityId = addCity(cityId, "Bengaluru", 1, 1);
        cityId = addCity(cityId, "Mysore", 1, 1);
        cityId = addCity(cityId, "Mysuru", 1, 1);
        cityId = addCity(cityId, "Mangalore", 1, 1);
        cityId = addCity(cityId, "Mangaluru", 1, 1);
        cityId = addCity(cityId, "Hubli", 1, 1);
        cityId = addCity(cityId, "Belgaum", 1, 1);
        cityId = addCity(cityId, "Davanagere", 1, 1);
        cityId = addCity(cityId, "Shimoga", 1, 1);
        cityId = addCity(cityId, "Bellary", 1, 1);
        cityId = addCity(cityId, "Tumkur", 1, 1);
        cityId = addCity(cityId, "Gulbarga", 1, 1);

        // Seed Cities (Maharashtra - StateID: 2)
        cityId = addCity(cityId, "Mumbai", 2, 1);
        cityId = addCity(cityId, "Pune", 2, 1);
        cityId = addCity(cityId, "Nagpur", 2, 1);
        cityId = addCity(cityId, "Nashik", 2, 1);
        cityId = addCity(cityId, "Thane", 2, 1);
        cityId = addCity(cityId, "Aurangabad", 2, 1);
        cityId = addCity(cityId, "Navi Mumbai", 2, 1);
        cityId = addCity(cityId, "Solapur", 2, 1);

        // Seed Cities (Delhi - StateID: 3)
        cityId = addCity(cityId, "Delhi", 3, 1);
        cityId = addCity(cityId, "New Delhi", 3, 1);
        cityId = addCity(cityId, "North Delhi", 3, 1);
        cityId = addCity(cityId, "South Delhi", 3, 1);
        cityId = addCity(cityId, "West Delhi", 3, 1);
        cityId = addCity(cityId, "East Delhi", 3, 1);

        // Seed Cities (Telangana - StateID: 4)
        cityId = addCity(cityId, "Hyderabad", 4, 1);
        cityId = addCity(cityId, "Secunderabad", 4, 1);
        cityId = addCity(cityId, "Warangal", 4, 1);
        cityId = addCity(cityId, "Nizamabad", 4, 1);
        cityId = addCity(cityId, "Karimnagar", 4, 1);

        // Seed Cities (Tamil Nadu - StateID: 5)
        cityId = addCity(cityId, "Chennai", 5, 1);
        cityId = addCity(cityId, "Coimbatore", 5, 1);
        cityId = addCity(cityId, "Madurai", 5, 1);
        cityId = addCity(cityId, "Tiruchirappalli", 5, 1);
        cityId = addCity(cityId, "Salem", 5, 1);
        cityId = addCity(cityId, "Tirunelveli", 5, 1);

        // Seed Cities (Gujarat - StateID: 6)
        cityId = addCity(cityId, "Ahmedabad", 6, 1);
        cityId = addCity(cityId, "Surat", 6, 1);
        cityId = addCity(cityId, "Vadodara", 6, 1);
        cityId = addCity(cityId, "Rajkot", 6, 1);
        cityId = addCity(cityId, "Bhavnagar", 6, 1);
        cityId = addCity(cityId, "Gandhinagar", 6, 1);

        // Seed Cities (Uttar Pradesh - StateID: 7)
        cityId = addCity(cityId, "Lucknow", 7, 1);
        cityId = addCity(cityId, "Kanpur", 7, 1);
        cityId = addCity(cityId, "Noida", 7, 1);
        cityId = addCity(cityId, "Greater Noida", 7, 1);
        cityId = addCity(cityId, "Ghaziabad", 7, 1);
        cityId = addCity(cityId, "Agra", 7, 1);
        cityId = addCity(cityId, "Varanasi", 7, 1);
        cityId = addCity(cityId, "Prayagraj", 7, 1);
        cityId = addCity(cityId, "Meerut", 7, 1);
        cityId = addCity(cityId, "Bareilly", 7, 1);

        // Seed Cities (West Bengal - StateID: 8)
        cityId = addCity(cityId, "Kolkata", 8, 1);
        cityId = addCity(cityId, "Howrah", 8, 1);
        cityId = addCity(cityId, "Durgapur", 8, 1);
        cityId = addCity(cityId, "Asansol", 8, 1);
        cityId = addCity(cityId, "Siliguri", 8, 1);

        // Seed Cities (Rajasthan - StateID: 9)
        cityId = addCity(cityId, "Jaipur", 9, 1);
        cityId = addCity(cityId, "Jodhpur", 9, 1);
        cityId = addCity(cityId, "Udaipur", 9, 1);
        cityId = addCity(cityId, "Kota", 9, 1);
        cityId = addCity(cityId, "Bikaner", 9, 1);
        cityId = addCity(cityId, "Ajmer", 9, 1);

        // Seed Cities (Kerala - StateID: 10)
        cityId = addCity(cityId, "Thiruvananthapuram", 10, 1);
        cityId = addCity(cityId, "Kochi", 10, 1);
        cityId = addCity(cityId, "Kozhikode", 10, 1);
        cityId = addCity(cityId, "Thrissur", 10, 1);
        cityId = addCity(cityId, "Kollam", 10, 1);

        // Seed Cities (Andhra Pradesh - StateID: 11)
        cityId = addCity(cityId, "Visakhapatnam", 11, 1);
        cityId = addCity(cityId, "Vijayawada", 11, 1);
        cityId = addCity(cityId, "Guntur", 11, 1);
        cityId = addCity(cityId, "Nellore", 11, 1);
        cityId = addCity(cityId, "Kurnool", 11, 1);
        cityId = addCity(cityId, "Tirupati", 11, 1);

        // Seed Cities (Punjab - StateID: 12)
        cityId = addCity(cityId, "Ludhiana", 12, 1);
        cityId = addCity(cityId, "Amritsar", 12, 1);
        cityId = addCity(cityId, "Jalandhar", 12, 1);
        cityId = addCity(cityId, "Patiala", 12, 1);
        cityId = addCity(cityId, "Mohali", 12, 1);

        // Seed Cities (Haryana - StateID: 13)
        cityId = addCity(cityId, "Gurgaon", 13, 1);
        cityId = addCity(cityId, "Gurugram", 13, 1);
        cityId = addCity(cityId, "Faridabad", 13, 1);
        cityId = addCity(cityId, "Panipat", 13, 1);
        cityId = addCity(cityId, "Ambala", 13, 1);
        cityId = addCity(cityId, "Karnal", 13, 1);

        // Seed Cities (Bihar - StateID: 14)
        cityId = addCity(cityId, "Patna", 14, 1);
        cityId = addCity(cityId, "Gaya", 14, 1);
        cityId = addCity(cityId, "Bhagalpur", 14, 1);
        cityId = addCity(cityId, "Muzaffarpur", 14, 1);

        // Seed Cities (Madhya Pradesh - StateID: 15)
        cityId = addCity(cityId, "Indore", 15, 1);
        cityId = addCity(cityId, "Bhopal", 15, 1);
        cityId = addCity(cityId, "Jabalpur", 15, 1);
        cityId = addCity(cityId, "Gwalior", 15, 1);

        // Seed Cities (Goa - StateID: 16)
        cityId = addCity(cityId, "Panaji", 16, 1);
        cityId = addCity(cityId, "Margao", 16, 1);
        cityId = addCity(cityId, "Vasco da Gama", 16, 1);

        // Seed Cities (Odisha - StateID: 17)
        cityId = addCity(cityId, "Bhubaneswar", 17, 1);
        cityId = addCity(cityId, "Cuttack", 17, 1);
        cityId = addCity(cityId, "Rourkela", 17, 1);
        cityId = addCity(cityId, "Puri", 17, 1);
    }

    private static void addCountry(int id, String name, int isDefault) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("CountryID", id);
        map.put("countryID", id);
        map.put("ID", id);
        map.put("id", id);
        map.put("value", id);
        map.put("Country", name);
        map.put("CountryName", name);
        map.put("label", name);
        map.put("IsDefault", isDefault);
        COUNTRIES.add(Collections.unmodifiableMap(map));
    }

    private static void addState(int id, String name, int countryId) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("StateID", id);
        map.put("stateID", id);
        map.put("ID", id);
        map.put("id", id);
        map.put("value", id);
        map.put("State", name);
        map.put("StateName", name);
        map.put("label", name);
        map.put("CountryID", countryId);
        map.put("countryID", countryId);
        STATES.add(Collections.unmodifiableMap(map));
    }

    private static int addCity(int id, String name, int stateId, int countryId) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("CityID", id);
        map.put("cityID", id);
        map.put("ID", id);
        map.put("id", id);
        map.put("value", id);
        map.put("City", name);
        map.put("CityName", name);
        map.put("label", name);
        map.put("StateID", stateId);
        map.put("stateID", stateId);
        map.put("CountryID", countryId);
        map.put("countryID", countryId);
        CITIES.add(Collections.unmodifiableMap(map));
        return id + 1;
    }

    @PostMapping({"/SearchCityData", "/searchCityData"})
    @Operation(summary = "Search cities for patient address autocomplete")
    public ResponseEntity<Map<String, Object>> searchCityData(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        int stateId = 0;
        String query = "";

        if (body != null) {
            Object sId = body.get("stateId");
            if (sId == null) sId = body.get("StateID");
            if (sId == null) sId = body.get("stateID");
            if (sId instanceof Number) {
                stateId = ((Number) sId).intValue();
            } else if (sId instanceof String && !((String) sId).trim().isEmpty()) {
                try {
                    stateId = Integer.parseInt(((String) sId).trim());
                } catch (Exception ignored) {}
            }

            Object q = body.get("cityName");
            if (q == null) q = body.get("CityName");
            if (q == null) q = body.get("City");
            if (q != null) {
                query = q.toString().trim().toLowerCase();
            }
        }

        final int targetStateId = stateId;
        final String targetQuery = query;

        List<Map<String, Object>> filtered = CITIES.stream()
            .filter(c -> {
                if (targetStateId > 0) {
                    Number sid = (Number) c.get("StateID");
                    if (sid == null || sid.intValue() != targetStateId) {
                        return false;
                    }
                }
                if (!targetQuery.isEmpty()) {
                    String cityName = (String) c.get("City");
                    return cityName != null && cityName.toLowerCase().contains(targetQuery);
                }
                return true;
            })
            .collect(Collectors.toList());

        response.put("success", true);
        response.put("status", true);
        response.put("message", filtered);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/SearchStateData", "/searchStateData"})
    @Operation(summary = "Search states")
    public ResponseEntity<Map<String, Object>> searchStateData(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        int countryId = 0;
        String query = "";

        if (body != null) {
            Object cId = body.get("countryId");
            if (cId == null) cId = body.get("CountryID");
            if (cId == null) cId = body.get("countryID");
            if (cId instanceof Number) {
                countryId = ((Number) cId).intValue();
            } else if (cId instanceof String && !((String) cId).trim().isEmpty()) {
                try {
                    countryId = Integer.parseInt(((String) cId).trim());
                } catch (Exception ignored) {}
            }

            Object q = body.get("stateName");
            if (q == null) q = body.get("StateName");
            if (q == null) q = body.get("State");
            if (q != null) {
                query = q.toString().trim().toLowerCase();
            }
        }

        final int targetCountryId = countryId;
        final String targetQuery = query;

        List<Map<String, Object>> filtered = STATES.stream()
            .filter(s -> {
                if (targetCountryId > 0) {
                    Number cid = (Number) s.get("CountryID");
                    if (cid == null || cid.intValue() != targetCountryId) {
                        return false;
                    }
                }
                if (!targetQuery.isEmpty()) {
                    String stateName = (String) s.get("State");
                    return stateName != null && stateName.toLowerCase().contains(targetQuery);
                }
                return true;
            })
            .collect(Collectors.toList());

        response.put("success", true);
        response.put("status", true);
        response.put("message", filtered);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/SearchCountryData", "/searchCountryData"})
    @Operation(summary = "Search countries")
    public ResponseEntity<Map<String, Object>> searchCountryData(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();

        String query = "";
        if (body != null) {
            Object q = body.get("countryName");
            if (q == null) q = body.get("CountryName");
            if (q == null) q = body.get("Country");
            if (q != null) {
                query = q.toString().trim().toLowerCase();
            }
        }

        final String targetQuery = query;

        List<Map<String, Object>> filtered = COUNTRIES.stream()
            .filter(c -> {
                if (!targetQuery.isEmpty()) {
                    String countryName = (String) c.get("CountryName");
                    return countryName != null && countryName.toLowerCase().contains(targetQuery);
                }
                return true;
            })
            .collect(Collectors.toList());

        response.put("success", true);
        response.put("status", true);
        response.put("message", filtered);
        return ResponseEntity.ok(response);
    }
}
