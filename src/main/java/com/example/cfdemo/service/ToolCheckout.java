package com.example.cfdemo.service;

import com.example.cfdemo.pojo.Checkout;
import com.example.cfdemo.pojo.RentalAgreement;

public interface ToolCheckout {
    RentalAgreement toolCheckout(Checkout checkout);
}
