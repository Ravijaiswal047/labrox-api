package com.labrox.order.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "patient_master", schema = "order_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "uhid", nullable = false, unique = true, length = 50)
    private String uhid;

    @Column(name = "title", length = 20)
    private String title;

    @Column(name = "p_name", nullable = false, length = 150)
    private String name;

    @Column(name = "gender", nullable = false, length = 20)
    private String gender;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "age_year")
    private Integer ageYear;

    @Column(name = "age_month")
    private Integer ageMonth;

    @Column(name = "age_days")
    private Integer ageDays;

    @Column(name = "total_age_in_days", nullable = false)
    private Integer totalAgeInDays;

    @Column(name = "mobile", nullable = false, length = 20)
    private String mobile;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "pincode", length = 20)
    private String pincode;

    @Column(name = "is_vip")
    private Boolean isVip;

    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
