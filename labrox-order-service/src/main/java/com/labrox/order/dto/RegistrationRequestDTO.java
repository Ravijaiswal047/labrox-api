package com.labrox.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationRequestDTO {

    @JsonProperty("PatientData")
    private PatientDataDto patientData;

    @JsonProperty("LTData")
    private LTDataDto ltData;

    @JsonProperty("PLO")
    private List<PLODto> plo;

    @JsonProperty("RcData")
    private List<RcDataDto> rcData;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PatientDataDto {
        @JsonProperty("PatientID")
        private String patientID;

        @JsonProperty("Title")
        private String title;

        @JsonProperty("PName")
        private String pName;

        @JsonProperty("PatientName")
        private String patientName;

        @JsonProperty("FirstName")
        private String firstName;

        @JsonProperty("MiddleName")
        private String middleName;

        @JsonProperty("LastName")
        private String lastName;

        @JsonProperty("Gender")
        private String gender;

        @JsonProperty("DOB")
        private String dob;

        @JsonProperty("Age")
        private String age;

        @JsonProperty("AgeYear")
        private Integer ageYear;

        @JsonProperty("AgeMonth")
        private Integer ageMonth;

        @JsonProperty("AgeDays")
        private Integer ageDays;

        @JsonProperty("TotalAgeInDays")
        private Integer totalAgeInDays;

        @JsonProperty("Mobile")
        private String mobile;

        @JsonProperty("Email")
        private String email;

        @JsonProperty("House_No")
        private String houseNo;

        @JsonProperty("Street_Name")
        private String streetName;

        @JsonProperty("Locality")
        private String locality;

        @JsonProperty("City")
        private String city;

        @JsonProperty("State")
        private String state;

        @JsonProperty("PinCode")
        private String pinCode;

        @JsonProperty("VIP")
        private Integer vip;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LTDataDto {
        @JsonProperty("PName")
        private String pName;

        @JsonProperty("PatientName")
        private String patientName;

        @JsonProperty("DoctorID")
        private Integer doctorID;

        @JsonProperty("DoctorName")
        private String doctorName;

        @JsonProperty("CentreID")
        private Integer centreID;

        @JsonProperty("RateTypeID")
        private Integer rateTypeID;

        @JsonProperty("GrossAmount")
        private BigDecimal grossAmount;

        @JsonProperty("DiscountOnTotal")
        private BigDecimal discountOnTotal;

        @JsonProperty("DiscountReason")
        private String discountReason;

        @JsonProperty("NetAmount")
        private BigDecimal netAmount;

        @JsonProperty("Adjustment")
        private BigDecimal adjustment;

        @JsonProperty("IsCredit")
        private Integer isCredit;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PLODto {
        @JsonProperty("InvestigationID")
        private Integer investigationID;

        @JsonProperty("BarcodeNo")
        private String barcodeNo;

        @JsonProperty("ItemName")
        private String itemName;

        @JsonProperty("Rate")
        private BigDecimal rate;

        @JsonProperty("DiscountAmt")
        private BigDecimal discountAmt;

        @JsonProperty("Amount")
        private BigDecimal amount;

        @JsonProperty("SampleTypeID")
        private Integer sampleTypeID;

        @JsonProperty("IsPackage")
        private Integer isPackage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RcDataDto {
        @JsonProperty("AmountPaid")
        private BigDecimal amountPaid;

        @JsonProperty("PaymentMode")
        private String paymentMode;

        @JsonProperty("PaymentModeID")
        private Integer paymentModeID;

        @JsonProperty("BankName")
        private String bankName;

        @JsonProperty("CardNo")
        private String cardNo;

        @JsonProperty("TransactionNo")
        private String transactionNo;
    }
}
