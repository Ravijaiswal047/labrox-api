package com.labrox.iam.service;

import com.labrox.iam.domain.Employee;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtTokenService {

    private final SecretKey key;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtTokenService(
            @Value("${labrox.jwt.secret}") String secret,
            @Value("${labrox.jwt.access-token-expiration-ms:900000}") long accessTokenExpirationMs,
            @Value("${labrox.jwt.refresh-token-expiration-ms:604800000}") long refreshTokenExpirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public String generateAccessToken(Employee emp, Integer currentCentreId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", emp.getId());
        claims.put("employeeCode", emp.getEmployeeCode());
        claims.put("username", emp.getUsername());
        claims.put("companyId", emp.getCompanyId());
        claims.put("designationId", emp.getDesignationId());
        claims.put("defaultCentreId", emp.getDefaultCentreId());
        claims.put("sessionCentreId", currentCentreId != null ? currentCentreId : emp.getDefaultCentreId());
        claims.put("rateTypeId", emp.getRateTypeId());
        claims.put("canRefund", emp.isCanRefund());
        claims.put("canSettlement", emp.isCanSettlement());
        claims.put("canDiscountAfterBill", emp.isCanDiscountAfterBill());
        claims.put("saveAmendment", emp.isSaveAmendment());
        claims.put("blockExcelPrintout", emp.isBlockExcelPrintout());
        claims.put("blockLabReportPrintout", emp.isBlockLabReportPrintout());

        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .subject(emp.getUsername())
                .issuedAt(new Date(now))
                .expiration(new Date(now + accessTokenExpirationMs))
                .signWith(key)
                .compact();
    }

    public Claims validateAndExtractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

