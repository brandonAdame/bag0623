package com.example.cfdemo.pojo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class RentalAgreement {
    String toolCode;
    String toolType;
    String toolBrand;
    int rentalDays;
    LocalDate checkoutDate;
    LocalDate dueDate;
    BigDecimal dailyRentalCharge;
    int chargeDays;
    BigDecimal preDiscountCharge;
    long discountPercent;
    BigDecimal discountAmount;
    BigDecimal finalCharge;
}
