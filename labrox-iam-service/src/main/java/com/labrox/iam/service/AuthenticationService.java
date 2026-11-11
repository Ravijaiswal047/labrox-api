package com.labrox.iam.service;

import com.labrox.iam.domain.CentreMaster;
import com.labrox.iam.domain.Employee;
import com.labrox.iam.dto.LoginRequest;
import com.labrox.iam.dto.LoginResponse;
import com.labrox.iam.repository.CentreMasterRepository;
import com.labrox.iam.repository.EmployeeRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthenticationService {

    private final EmployeeRepository employeeRepository;
    private final CentreMasterRepository centreRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final JwtTokenService jwtTokenService;

    public AuthenticationService(
            EmployeeRepository employeeRepository,
            CentreMasterRepository centreRepository,
            PasswordEncoderService passwordEncoderService,
            JwtTokenService jwtTokenService) {
        this.employeeRepository = employeeRepository;
        this.centreRepository = centreRepository;
        this.passwordEncoderService = passwordEncoderService;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public LoginResponse authenticate(LoginRequest req) {
        Employee emp = employeeRepository.findByUsernameAndActiveTrue(req.getUsername().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoderService.verifyPassword(req.getPassword(), emp.getPasswordHash(), emp.getPasswordAlgo())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // Transparent auto-upgrade from legacy SHA-256 to modern Argon2id
        if ("SHA-256".equalsIgnoreCase(emp.getPasswordAlgo()) || "SHA256".equalsIgnoreCase(emp.getPasswordAlgo())) {
            emp.setPasswordHash(passwordEncoderService.hashPassword(req.getPassword()));
            emp.setPasswordAlgo("ARGON2ID");
            employeeRepository.save(emp);
        }

        Integer sessionCentreId = req.getCentreId() != null ? req.getCentreId() : emp.getDefaultCentreId();
        String token = jwtTokenService.generateAccessToken(emp, sessionCentreId);

        List<LoginResponse.AccessibleCentreDto> centres = centreRepository.findByCompanyIdAndActiveTrue(emp.getCompanyId())
                .stream()
                .map(c -> LoginResponse.AccessibleCentreDto.builder()
                        .centreId(c.getId())
                        .centreCode(c.getCentreCode())
                        .centreName(c.getCentreName())
                        .build())
                .toList();

        LoginResponse.UserPermissionsDto permissions = LoginResponse.UserPermissionsDto.builder()
                .canRefund(emp.isCanRefund())
                .canSettlement(emp.isCanSettlement())
                .canDiscountAfterBill(emp.isCanDiscountAfterBill())
                .saveAmendment(emp.isSaveAmendment())
                .blockExcelPrintout(emp.isBlockExcelPrintout())
                .blockLabReportPrintout(emp.isBlockLabReportPrintout())
                .amrAccess(emp.isAmrAccess())
                .unmasking(emp.isUnmasking())
                .build();

        return LoginResponse.builder()
                .token(token)
                .userId(emp.getId())
                .employeeCode(emp.getEmployeeCode())
                .name(emp.getName())
                .companyId(emp.getCompanyId())
                .defaultCentreId(emp.getDefaultCentreId())
                .currentCentreId(sessionCentreId)
                .designationId(emp.getDesignationId())
                .accessibleCentres(centres)
                .permissions(permissions)
                .build();
    }
}

