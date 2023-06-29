package com.example.cfdemo.service;

import com.example.cfdemo.dao.ToolRetrieval;
import com.example.cfdemo.pojo.Checkout;
import com.example.cfdemo.pojo.RentalAgreement;
import com.example.cfdemo.pojo.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ToolCheckoutImpl implements ToolCheckout {

    ToolRetrieval toolRetrieval;

    public ToolCheckoutImpl(ToolRetrieval toolRetrieval) {
        this.toolRetrieval = toolRetrieval;
    }

    @Override
    public RentalAgreement toolCheckout(Checkout checkoutRequest) {
        Tool tool = toolRetrieval.checkoutTool(checkoutRequest);

        log.info("{}", tool);
        return null;
    }
}
