package com.example.cfdemo.service;

import com.example.cfdemo.dao.ToolRetrievalDao;
import com.example.cfdemo.pojo.Checkout;
import com.example.cfdemo.pojo.RentalAgreement;
import com.example.cfdemo.pojo.Tool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ToolCheckoutImplTest {

    private ToolCheckout toolCheckout;

    @Mock
    private ToolRetrievalDao toolRetrievalDao;

    @BeforeEach
    void setUp() {
        toolCheckout = new ToolCheckoutImpl(toolRetrievalDao);
    }

    @Test
    void testCheckoutChainsaw() {
        // Given
        Checkout request = new Checkout();
        request.setToolCode("CHNS");
        request.setRentalDayCount(8);
        request.setDiscountPercent(40);
        request.setCheckoutDate(LocalDate.of(2023, 6, 28));

        Tool tool = new Tool();
        tool.setTool_code("CHNS");
        tool.setTool_type("Chainsaw");
        tool.setBrand("Stihl");
        tool.setDaily_charge(new BigDecimal("1.49"));
        tool.setWeekday_charge(true);
        tool.setWeekend_charge(false);
        tool.setHoliday_charge(true);

        RentalAgreement expectedAgreement = RentalAgreement.builder()
                .toolCode(tool.getTool_code())
                .toolType(tool.getTool_type())
                .toolBrand(tool.getBrand())
                .rentalDays(request.getRentalDayCount())
                .checkoutDate(request.getCheckoutDate())
                .dueDate(LocalDate.of(2023, 7,6))
                .dailyRentalCharge(tool.getDaily_charge())
                .chargeDays(6)
                .preDiscountCharge(new BigDecimal("8.94"))
                .discountPercent(request.getDiscountPercent())
                .discountAmount(new BigDecimal("3.58"))
                .finalCharge(new BigDecimal("5.36"))
                .build();

        // When
        when(toolRetrievalDao.checkoutTool(request)).thenReturn(tool);

        // Then
        RentalAgreement actualAgreement = toolCheckout.toolCheckout(request);
        assertEquals(expectedAgreement, actualAgreement);
    }
}
