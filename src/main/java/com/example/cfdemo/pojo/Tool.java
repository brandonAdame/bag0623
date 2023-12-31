package com.example.cfdemo.pojo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Tool {
    String tool_code;
    String tool_type;
    String brand;
    BigDecimal daily_charge;
    boolean weekday_charge;
    boolean weekend_charge;
    boolean holiday_charge;
}
