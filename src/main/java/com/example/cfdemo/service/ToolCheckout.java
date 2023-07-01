package com.example.cfdemo.service;

import com.example.cfdemo.pojo.Checkout;
import com.example.cfdemo.pojo.RentalAgreement;

public interface ToolCheckout {

    /**
     * Checkout a tool
     *
     * @param checkoutRequest Tool Checkout request
     * @return Tool rental agreement
     */
    RentalAgreement toolCheckout(Checkout checkoutRequest);
}
