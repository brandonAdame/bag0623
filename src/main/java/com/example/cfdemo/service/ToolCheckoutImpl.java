package com.example.cfdemo.service;

import com.example.cfdemo.pojo.Checkout;
import com.example.cfdemo.pojo.RentalAgreement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ToolCheckoutImpl implements ToolCheckout {
    @Override
    public RentalAgreement toolCheckout(Checkout checkout) {
        return null;
    }
}
