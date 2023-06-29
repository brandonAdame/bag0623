package com.example.cfdemo.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RentalAgreement {
    String toolCode;
    Tool toolInfo;
    int rentalDays;
    LocalDate checkoutDate;
    LocalDate dueDate;
    BigDecimal dailyRentalCharge;
    int chargeDays;
    BigDecimal preDiscountCharge;
    long discountPercent;
    BigDecimal discountAmount;
    BigDecimal finalCharge; // (preDiscountCharge - discountAmount)
}
