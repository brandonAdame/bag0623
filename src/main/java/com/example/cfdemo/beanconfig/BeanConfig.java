package com.example.cfdemo.beanconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
public class BeanConfig {

    @Bean
    public NumberFormat numberFormat() {
        return NumberFormat.getCurrencyInstance(Locale.US);
    }

    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern("MM/dd/yy");
    }
}
