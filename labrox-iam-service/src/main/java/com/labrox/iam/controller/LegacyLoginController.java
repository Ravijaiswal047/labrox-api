package com.labrox.iam.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.labrox.iam.domain.Employee;
import com.labrox.iam.repository.EmployeeRepository;
import com.labrox.iam.service.JwtTokenService;
import com.labrox.iam.service.PasswordEncoderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping({"/api/v1/login", "/api/v1/Login"})
@Tag(name = "Legacy Authentication API Bridge")
public class LegacyLoginController {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final JwtTokenService jwtTokenService;

    public LegacyLoginController(
            EmployeeRepository employeeRepository,
            PasswordEncoderService passwordEncoderService,
            JwtTokenService jwtTokenService) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoderService = passwordEncoderService;
        this.jwtTokenService = jwtTokenService;
    }

    public static class LoginWithOtpRequest {
        private String username;
        private String UserName;

        public String getUsername() {
            return username != null ? username : UserName;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        @JsonProperty("UserName")
        public void setUserName(String userName) {
            this.UserName = userName;
        }
    }

    public static class LegacyLoginRequest {
        private String username;
        private String UserName;
        private String password;
        private String Password;
        private Boolean rememberMe;
        private String ipaddress;

        public String getUsername() {
            return username != null ? username : UserName;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        @JsonProperty("UserName")
        public void setUserName(String userName) {
            this.UserName = userName;
        }

        public String getPassword() {
            return password != null ? password : Password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @JsonProperty("Password")
        public void setPasswordUpper(String password) {
            this.Password = password;
        }

        public Boolean getRememberMe() {
            return rememberMe;
        }

        public void setRememberMe(Boolean rememberMe) {
            this.rememberMe = rememberMe;
        }

        public String getIpaddress() {
            return ipaddress;
        }

        public void setIpaddress(String ipaddress) {
            this.ipaddress = ipaddress;
        }
    }

    @PostMapping({"/LoginWithOTP", "/loginWithOTP"})
    @Operation(summary = "Validate username exists (step 1 of legacy login flow)")
    public ResponseEntity<Map<String, Object>> loginWithOtp(@RequestBody(required = false) LoginWithOtpRequest request) {
        Map<String, Object> res = new HashMap<>();
        String uname = request != null ? request.getUsername() : null;

        if (uname == null || uname.trim().isEmpty()) {
            res.put("success", false);
            res.put("status", false);
            res.put("message", "Please enter username");
            return ResponseEntity.badRequest().body(res);
        }

        String rawUname = uname.trim();
        Optional<Employee> empOpt = employeeRepository.findByUsernameIgnoreCaseAndActiveTrue(rawUname);
        if (empOpt.isEmpty()) {
            empOpt = employeeRepository.findByCompanyIdAndUsernameIgnoreCaseAndActiveTrue(1, rawUname);
        }
        if (empOpt.isEmpty()) {
            empOpt = employeeRepository.findByEmployeeCodeIgnoreCaseAndActiveTrue(rawUname);
        }

        if (empOpt.isEmpty()) {
            res.put("success", false);
            res.put("status", false);
            res.put("message", "Invalid username or account inactive");
            return ResponseEntity.ok(res);
        }

        res.put("success", true);
        res.put("status", true);
        res.put("message", "Please enter password");
        res.put("isOTPLogin", false);
        res.put("IsOTPLogin", false);
        return ResponseEntity.ok(res);
    }

    @PostMapping({"/login", "/Login"})
    @Operation(summary = "Authenticate user and return full legacy user envelope (step 2)")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LegacyLoginRequest request) {
        Map<String, Object> res = new HashMap<>();
        String uname = request != null ? request.getUsername() : null;
        String pwd = request != null ? request.getPassword() : null;

        if (uname == null || uname.trim().isEmpty() || pwd == null) {
            res.put("success", false);
            res.put("status", false);
            res.put("message", "Please enter username & password");
            return ResponseEntity.badRequest().body(res);
        }

        String rawUname = uname.trim();
        Optional<Employee> empOpt = employeeRepository.findByUsernameIgnoreCaseAndActiveTrue(rawUname);
        if (empOpt.isEmpty()) {
            empOpt = employeeRepository.findByCompanyIdAndUsernameIgnoreCaseAndActiveTrue(1, rawUname);
        }
        if (empOpt.isEmpty()) {
            empOpt = employeeRepository.findByEmployeeCodeIgnoreCaseAndActiveTrue(rawUname);
        }

        if (empOpt.isEmpty()) {
            res.put("success", false);
            res.put("status", false);
            res.put("message", "Invalid credentials");
            return ResponseEntity.ok(res);
        }

        Employee emp = empOpt.get();
        boolean valid = passwordEncoderService.verifyPassword(pwd, emp.getPasswordHash(), emp.getPasswordAlgo());
        if (!valid) {
            res.put("success", false);
            res.put("status", false);
            res.put("message", "Invalid credentials");
            return ResponseEntity.ok(res);
        }

        // Auto-upgrade legacy password algorithm to Argon2id
        if ("SHA-256".equalsIgnoreCase(emp.getPasswordAlgo()) || "SHA256".equalsIgnoreCase(emp.getPasswordAlgo())) {
            emp.setPasswordHash(passwordEncoderService.hashPassword(pwd));
            emp.setPasswordAlgo("ARGON2ID");
            employeeRepository.save(emp);
        }

        String token = jwtTokenService.generateAccessToken(emp, emp.getDefaultCentreId());

        Map<String, Object> userData = new HashMap<>();
        userData.put("EmployeeID", String.valueOf(emp.getId()));
        userData.put("UserId", String.valueOf(emp.getId()));
        userData.put("UserName", emp.getUsername());
        userData.put("username", emp.getUsername());
        userData.put("FirstName", emp.getName());
        userData.put("LastName", "");
        userData.put("CompanyID", String.valueOf(emp.getCompanyId()));
        userData.put("CompanyId", String.valueOf(emp.getCompanyId()));
        userData.put("CompanyCode", "LABROX");
        userData.put("CompanyName", "LabRox Healthcare & Diagnostics");
        userData.put("DefaultCentreID", String.valueOf(emp.getDefaultCentreId()));
        userData.put("DefaultCentre", String.valueOf(emp.getDefaultCentreId()));
        userData.put("SessionCentreID", String.valueOf(emp.getDefaultCentreId()));
        userData.put("CenterName", "Main Reference Lab");
        userData.put("DesignationId", String.valueOf(emp.getDesignationId()));
        userData.put("DesignationName", "Super Administrator");
        userData.put("SuperAdmin", 1);
        userData.put("isAuthroizedApproval", 1);
        userData.put("ShowDashboard", 1);
        userData.put("IsStoreDashboard", 0);
        userData.put("Theme", "sky_blue_theme");
        userData.put("FontSize", 13);
        userData.put("LoginCount", 1);
        userData.put("AllowRateTypeRefereshWorkOrder", 0);
        userData.put("AddBlankRateTypeWorkOrder", 0);
        userData.put("AllowSampleCollectionDateWorkOrder", 1);

        res.put("success", true);
        res.put("status", true);
        res.put("token", token);
        res.put("user", userData);
        res.put("message", "Login successful");

        return ResponseEntity.ok(res);
    }

    @GetMapping({"/GetAppSettings", "/getAppSettings"})
    @Operation(summary = "Get global application settings such as IdleLogoutMinutes")
    public ResponseEntity<Map<String, Object>> getAppSettings() {
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("status", true);
        Map<String, Object> settings = new HashMap<>();
        settings.put("IdleLogoutMinutes", 30);
        res.put("message", settings);
        res.put("IdleLogoutMinutes", 30);
        return ResponseEntity.ok(res);
    }
}
