package com.example.cfdemo.pojo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);

        sb.append("\n")
                .append("Tool code: ").append(this.getToolCode()).append("\n")
                .append("Tool type: ").append(this.getToolType()).append("\n")
                .append("Tool brand: ").append(this.getToolBrand()).append("\n")
                .append("Rental days: ").append(this.getRentalDays()).append("\n")
                .append("Checkout date: ").append(this.checkoutDate.format(timeFormatter)).append("\n")
                .append("Due date: ").append(this.dueDate.format(timeFormatter)).append("\n")
                .append("Daily rental charge: ").append(numberFormat.format(this.dailyRentalCharge.doubleValue())).append("\n")
                .append("Charge days: ").append(this.chargeDays).append("\n")
                .append("Pre-discount charge: ").append(numberFormat.format(this.preDiscountCharge.doubleValue())).append("\n")
                .append("Discount percent: ").append(this.discountPercent).append("%").append("\n")
                .append("Discount amount: ").append(numberFormat.format(this.discountAmount.doubleValue())).append("\n")
                .append("Final charge: ").append(numberFormat.format(this.finalCharge.doubleValue()));

        return sb.toString();
    }
}
