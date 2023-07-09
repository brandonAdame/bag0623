package com.example.cfdemo.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RentalAgreement {
    String toolCode;
    String toolType;
    String toolBrand;
    int rentalDays;
    String checkoutDate;
    String dueDate;
    String dailyRentalCharge;
    int chargeDays;
    String preDiscountCharge;
    String discountPercent;
    String discountAmount;
    String finalCharge;

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n")
                .append("Tool code: ").append(this.getToolCode()).append("\n")
                .append("Tool type: ").append(this.getToolType()).append("\n")
                .append("Tool brand: ").append(this.getToolBrand()).append("\n")
                .append("Rental days: ").append(this.getRentalDays()).append("\n")
                .append("Checkout date: ").append(this.checkoutDate).append("\n")
                .append("Due date: ").append(this.dueDate).append("\n")
                .append("Daily rental charge: ").append(this.dailyRentalCharge).append("\n")
                .append("Charge days: ").append(this.chargeDays).append("\n")
                .append("Pre-discount charge: ").append(this.preDiscountCharge).append("\n")
                .append("Discount percent: ").append(this.discountPercent).append("\n")
                .append("Discount amount: ").append(this.discountAmount).append("\n")
                .append("Final charge: ").append(this.finalCharge);

        return sb.toString();
    }
}
