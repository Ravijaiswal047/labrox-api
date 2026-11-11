package com.labrox.iam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/iam/login",
                    "/api/v1/login/**",
                    "/api/v1/Login/**",
                    "/api/v1/Menu/**",
                    "/api/v1/menu/**",
                    "/api/v1/Centre/**",
                    "/api/v1/centre/**",
                    "/api/v1/Employee/**",
                    "/api/v1/employee/**",
                    "/api/v1/DoctorReferal/**",
                    "/api/v1/doctorReferal/**",
                    "/api/v1/Global/**",
                    "/api/v1/global/**",
                    "/api/v1/TestData/**",
                    "/api/v1/testData/**",
                    "/api/v1/CommonController/**",
                    "/api/v1/commonController/**",
                    "/api/v1/CommonAPI/**",
                    "/api/v1/commonAPI/**",
                    "/api/v1/CommonHC/**",
                    "/api/v1/commonHC/**",
                    "/api/v1/CompanyMaster/**",
                    "/api/v1/companymaster/**",
                    "/api/v1/ManageFieldMaster/**",
                    "/api/v1/managefieldmaster/**",
                    "/api/v1/Booking/**",
                    "/api/v1/booking/**",
                    "/api/v1/CouponMaster/**",
                    "/api/v1/couponMaster/**",
                    "/api/v1/ConcentFormMaster/**",
                    "/api/v1/concentFormMaster/**",
                    "/api/v1/PndtFormMaster/**",
                    "/api/v1/pndtFormMaster/**",
                    "/api/v1/PreRegistration/**",
                    "/api/v1/preRegistration/**",
                    "/api/v1/DiscApproval/**",
                    "/api/v1/discApproval/**",
                    "/api/v1/MachineGroup/**",
                    "/api/v1/machineGroup/**",
                    "/api/v1/LanguageMaster/**",
                    "/api/v1/languageMaster/**",
                    "/api/v1/Designation/**",
                    "/api/v1/designation/**",
                    "/api/v1/Accounts/**",
                    "/api/v1/accounts/**",
                    "/api/v1/ReferalLab/**",
                    "/api/v1/referalLab/**",
                    "/api/v1/FieldBoyMaster/**",
                    "/api/v1/fieldBoyMaster/**",
                    "/api/v1/RazorPay/**",
                    "/api/v1/razorPay/**",
                    "/api/v1/Department/**",
                    "/api/v1/department/**",
                    "/api/v1/HistoCyto/**",
                    "/api/v1/histoCyto/**",
                    "/api/v1/Lab/**",
                    "/api/v1/lab/**",
                    "/api/v1/BuildingBlock/**",
                    "/api/v1/buildingBlock/**",
                    "/api/v1/DignosticMaster/**",
                    "/api/v1/dignosticMaster/**",
                    "/api/v1/Investigations/**",
                    "/api/v1/investigations/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:3000", "http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Correlation-Id"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
