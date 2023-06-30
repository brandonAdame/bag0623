package com.example.cfdemo.pojo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Checkout {

    @NotBlank
    String toolCode;

    @Min(1)
    int rentalDayCount;

    @Min(0)
    @Max(100)
    int discountPercent;

    @NotNull
    LocalDate checkoutDate;
}
