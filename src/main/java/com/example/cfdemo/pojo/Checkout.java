package com.example.cfdemo.pojo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Checkout {
    String toolCode;
    int rentalDayCount;
    int discountPercent;
    LocalDate checkoutDate;
}
