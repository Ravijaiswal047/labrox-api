package com.labrox.iam;

import com.labrox.iam.controller.LegacyLoginController;
import com.labrox.iam.domain.Employee;
import com.labrox.iam.repository.EmployeeRepository;
import com.labrox.iam.service.JwtTokenService;
import com.labrox.iam.service.PasswordEncoderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class LegacyLoginControllerTest {

    private EmployeeRepository employeeRepository;
    private PasswordEncoderService passwordEncoderService;
    private JwtTokenService jwtTokenService;
    private LegacyLoginController controller;

    @BeforeEach
    void setUp() {
        employeeRepository = Mockito.mock(EmployeeRepository.class);
        passwordEncoderService = Mockito.mock(PasswordEncoderService.class);
        jwtTokenService = Mockito.mock(JwtTokenService.class);
        controller = new LegacyLoginController(employeeRepository, passwordEncoderService, jwtTokenService);
    }

    @Test
    @DisplayName("Legacy Contract Parity: Step 1 LoginWithOTP returns success=true and isOTPLogin=false")
    void testLoginWithOtp_ValidUser() {
        Employee mockEmp = Employee.builder()
                .id(1L)
                .username("admin")
                .companyId(1)
                .active(true)
                .build();

        when(employeeRepository.findByUsernameAndActiveTrue("admin")).thenReturn(Optional.of(mockEmp));

        LegacyLoginController.LoginWithOtpRequest req = new LegacyLoginController.LoginWithOtpRequest();
        req.setUsername("admin");

        ResponseEntity<Map<String, Object>> response = controller.loginWithOtp(req);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().get("success"));
        assertEquals(false, response.getBody().get("isOTPLogin"));
        assertEquals("Please enter password", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Legacy Contract Parity: Step 2 Login returns token and user dictionary with exact PascalCase keys")
    @SuppressWarnings("unchecked")
    void testLogin_ValidAdmin() {
        Employee mockEmp = Employee.builder()
                .id(1L)
                .username("admin")
                .name("System Administrator")
                .companyId(1)
                .designationId(1)
                .defaultCentreId(1)
                .passwordHash("e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7")
                .passwordAlgo("SHA-256")
                .active(true)
                .build();

        when(employeeRepository.findByUsernameAndActiveTrue("admin")).thenReturn(Optional.of(mockEmp));
        when(passwordEncoderService.verifyPassword(eq("Admin@123"), any(), eq("SHA-256"))).thenReturn(true);
        when(passwordEncoderService.hashPassword("Admin@123")).thenReturn("$argon2id$mocked");
        when(jwtTokenService.generateAccessToken(any(), eq(1))).thenReturn("mock-jwt-token-xyz");

        LegacyLoginController.LegacyLoginRequest req = new LegacyLoginController.LegacyLoginRequest();
        req.setUsername("admin");
        req.setPassword("Admin@123");

        ResponseEntity<Map<String, Object>> response = controller.login(req);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(true, body.get("success"));
        assertEquals("mock-jwt-token-xyz", body.get("token"));

        Map<String, Object> user = (Map<String, Object>) body.get("user");
        assertNotNull(user);
        assertEquals("1", user.get("EmployeeID"));
        assertEquals("admin", user.get("UserName"));
        assertEquals("System Administrator", user.get("FirstName"));
        assertEquals(1, user.get("SuperAdmin"));
        assertEquals("sky_blue_theme", user.get("Theme"));
        assertEquals(13, user.get("FontSize"));
    }

    @Test
    @DisplayName("Legacy Contract Parity: Step 2 Login with wrong password returns success=false")
    void testLogin_InvalidPassword() {
        Employee mockEmp = Employee.builder()
                .id(1L)
                .username("admin")
                .companyId(1)
                .passwordHash("hashed")
                .passwordAlgo("ARGON2ID")
                .active(true)
                .build();

        when(employeeRepository.findByUsernameAndActiveTrue("admin")).thenReturn(Optional.of(mockEmp));
        when(passwordEncoderService.verifyPassword(eq("WrongPassword"), any(), eq("ARGON2ID"))).thenReturn(false);

        LegacyLoginController.LegacyLoginRequest req = new LegacyLoginController.LegacyLoginRequest();
        req.setUsername("admin");
        req.setPassword("WrongPassword");

        ResponseEntity<Map<String, Object>> response = controller.login(req);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(false, response.getBody().get("success"));
        assertEquals("Invalid credentials", response.getBody().get("message"));
    }
}
