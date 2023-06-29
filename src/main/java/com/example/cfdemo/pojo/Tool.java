package com.example.cfdemo.pojo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Tool {
    String toolCode;
    String toolType;
    String brand;
    BigDecimal daily_charge;
}
