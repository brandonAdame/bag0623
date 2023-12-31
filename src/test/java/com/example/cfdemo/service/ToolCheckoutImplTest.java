package com.example.cfdemo.service;

import com.example.cfdemo.dao.ToolRetrievalDao;
import com.example.cfdemo.pojo.Checkout;
import com.example.cfdemo.pojo.RentalAgreement;
import com.example.cfdemo.pojo.Tool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ToolCheckoutImplTest {

    private ToolCheckout toolCheckout; // System under test

    @Mock
    private ToolRetrievalDao toolRetrievalDao;

    @Mock
    private NumberFormat numberFormat;

    @Mock
    private DateTimeFormatter dateTimeFormatter;

    private Tool chainsaw;
    private Tool ladder;

    @BeforeEach
    void setUp() {
        toolCheckout = new ToolCheckoutImpl(toolRetrievalDao, numberFormat, dateTimeFormatter);

        chainsaw = new Tool();
        chainsaw.setTool_code("CHNS");
        chainsaw.setTool_type("Chainsaw");
        chainsaw.setBrand("Stihl");
        chainsaw.setDaily_charge(new BigDecimal("1.49"));
        chainsaw.setWeekday_charge(true);
        chainsaw.setWeekend_charge(false);
        chainsaw.setHoliday_charge(true);

        Tool jackhammerRidgid = new Tool();
        jackhammerRidgid.setTool_code("JAKR");
        jackhammerRidgid.setTool_type("Jackhammer");
        jackhammerRidgid.setBrand("Rigid");
        jackhammerRidgid.setDaily_charge(new BigDecimal("2.99"));
        jackhammerRidgid.setWeekday_charge(true);
        jackhammerRidgid.setWeekend_charge(false);
        jackhammerRidgid.setHoliday_charge(false);

        Tool jackhammerDewalt = new Tool();
        jackhammerDewalt.setTool_code("JAKD");
        jackhammerDewalt.setTool_type("Jackhammer");
        jackhammerDewalt.setBrand("DeWalt");
        jackhammerDewalt.setDaily_charge(new BigDecimal("2.99"));
        jackhammerDewalt.setWeekday_charge(true);
        jackhammerDewalt.setWeekend_charge(false);
        jackhammerDewalt.setHoliday_charge(false);

        ladder = new Tool();
        ladder.setTool_code("LADW");
        ladder.setTool_type("Ladder");
        ladder.setBrand("Werner");
        ladder.setDaily_charge(new BigDecimal("1.99"));
        ladder.setWeekday_charge(true);
        ladder.setWeekend_charge(true);
        ladder.setHoliday_charge(false);
    }

    @Test
    void testCheckoutChainsaw_WithIndependenceDay() {
        // Given
        Checkout request = new Checkout();
        request.setToolCode("CHNS");
        request.setRentalDayCount(8);
        request.setDiscountPercent(40);
        request.setCheckoutDate(LocalDate.of(2023, 6, 28));

        RentalAgreement expectedAgreement = RentalAgreement.builder()
                .toolCode(chainsaw.getTool_code())
                .toolType(chainsaw.getTool_type())
                .toolBrand(chainsaw.getBrand())
                .rentalDays(request.getRentalDayCount())
                .checkoutDate(request.getCheckoutDate().format(dateTimeFormatter))
                .dueDate(LocalDate.of(2023, 7, 6).format(dateTimeFormatter))
                .dailyRentalCharge(numberFormat.format(chainsaw.getDaily_charge()))
                .chargeDays(6)
                .preDiscountCharge(numberFormat.format(new BigDecimal("8.94")))
                .discountPercent("40%")
                .discountAmount(numberFormat.format(new BigDecimal("3.58")))
                .finalCharge(numberFormat.format(new BigDecimal("5.36")))
                .build();

        // When
        when(toolRetrievalDao.checkoutTool(request)).thenReturn(chainsaw);

        // Then
        RentalAgreement actualAgreement = toolCheckout.toolCheckout(request);
        assertEquals(expectedAgreement, actualAgreement);
    }

    @Test
    void testCheckoutChainsawOnIndependenceDay_OnSaturday() {
        // Given
        Checkout request = new Checkout();
        request.setToolCode("CHNS");
        request.setRentalDayCount(1);
        request.setDiscountPercent(0);
        request.setCheckoutDate(LocalDate.of(2015, 7, 4));

        RentalAgreement expectedAgreement = RentalAgreement.builder()
                .toolCode(chainsaw.getTool_code())
                .toolType(chainsaw.getTool_type())
                .toolBrand(chainsaw.getBrand())
                .rentalDays(request.getRentalDayCount())
                .checkoutDate(request.getCheckoutDate().format(dateTimeFormatter))
                .dueDate(LocalDate.of(2015, 7, 5).format(dateTimeFormatter))
                .dailyRentalCharge(numberFormat.format(chainsaw.getDaily_charge()))
                .chargeDays(1)
                .preDiscountCharge(numberFormat.format(new BigDecimal("1.49")))
                .discountPercent("0%")
                .discountAmount(numberFormat.format(new BigDecimal("0.00")))
                .finalCharge(numberFormat.format(new BigDecimal("1.49")))
                .build();

        // When
        when(toolRetrievalDao.checkoutTool(request)).thenReturn(chainsaw);

        // Then
        RentalAgreement actualAgreement = toolCheckout.toolCheckout(request);
        assertEquals(expectedAgreement, actualAgreement);
    }

    @Test
    void testCheckoutChainsawOnIndependenceDay_OnSunday() {
        // Given
        Checkout request = new Checkout();
        request.setToolCode("CHNS");
        request.setRentalDayCount(1);
        request.setDiscountPercent(0);
        request.setCheckoutDate(LocalDate.of(2021, 7, 4));

        RentalAgreement expectedAgreement = RentalAgreement.builder()
                .toolCode(chainsaw.getTool_code())
                .toolType(chainsaw.getTool_type())
                .toolBrand(chainsaw.getBrand())
                .rentalDays(request.getRentalDayCount())
                .checkoutDate(request.getCheckoutDate().format(dateTimeFormatter))
                .dueDate(LocalDate.of(2021, 7, 5).format(dateTimeFormatter))
                .dailyRentalCharge(numberFormat.format(chainsaw.getDaily_charge()))
                .chargeDays(1)
                .preDiscountCharge(numberFormat.format(new BigDecimal("1.49")))
                .discountPercent("0%")
                .discountAmount(numberFormat.format(new BigDecimal("0.00")))
                .finalCharge(numberFormat.format(new BigDecimal("1.49")))
                .build();

        // When
        when(toolRetrievalDao.checkoutTool(request)).thenReturn(chainsaw);

        // Then
        RentalAgreement actualAgreement = toolCheckout.toolCheckout(request);
        assertEquals(expectedAgreement, actualAgreement);
    }

    @Test
    void testCheckoutChainsawOnIndependenceDay_OnFriday() {
        // Given
        Checkout request = new Checkout();
        request.setToolCode("CHNS");
        request.setRentalDayCount(2);
        request.setDiscountPercent(0);
        request.setCheckoutDate(LocalDate.of(2003, 7, 4));

        RentalAgreement expectedAgreement = RentalAgreement.builder()
                .toolCode(chainsaw.getTool_code())
                .toolType(chainsaw.getTool_type())
                .toolBrand(chainsaw.getBrand())
                .rentalDays(request.getRentalDayCount())
                .checkoutDate(request.getCheckoutDate().format(dateTimeFormatter))
                .dueDate(LocalDate.of(2003, 7, 6).format(dateTimeFormatter))
                .dailyRentalCharge(numberFormat.format(chainsaw.getDaily_charge()))
                .chargeDays(1)
                .preDiscountCharge(numberFormat.format(new BigDecimal("1.49")))
                .discountPercent("0%")
                .discountAmount(numberFormat.format(new BigDecimal("0.00")))
                .finalCharge(numberFormat.format(new BigDecimal("1.49")))
                .build();

        // When
        when(toolRetrievalDao.checkoutTool(request)).thenReturn(chainsaw);

        // Then
        RentalAgreement actualAgreement = toolCheckout.toolCheckout(request);
        assertEquals(expectedAgreement, actualAgreement);
    }

    @Test
    void testCheckoutLadder_OnWeekday() {
        // Given
        Checkout request = new Checkout();
        request.setToolCode("LADW");
        request.setRentalDayCount(1);
        request.setDiscountPercent(0);
        request.setCheckoutDate(LocalDate.of(2023, 7, 7));

        RentalAgreement expectedAgreement = RentalAgreement.builder()
                .toolCode(ladder.getTool_code())
                .toolType(ladder.getTool_type())
                .toolBrand(ladder.getBrand())
                .rentalDays(request.getRentalDayCount())
                .checkoutDate(request.getCheckoutDate().format(dateTimeFormatter))
                .dueDate(LocalDate.of(2023, 7, 8).format(dateTimeFormatter))
                .dailyRentalCharge(numberFormat.format(ladder.getDaily_charge()))
                .chargeDays(1)
                .preDiscountCharge(numberFormat.format(new BigDecimal("1.99")))
                .discountPercent("0%")
                .discountAmount(numberFormat.format(new BigDecimal("0.00")))
                .finalCharge(numberFormat.format(new BigDecimal("1.99")))
                .build();

        // When
        when(toolRetrievalDao.checkoutTool(request)).thenReturn(ladder);

        // Then
        RentalAgreement actualAgreement = toolCheckout.toolCheckout(request);
        assertEquals(expectedAgreement, actualAgreement);
    }

    @Test
    void testCheckoutLadder_OnWeekend() {
        // Given
        Checkout request = new Checkout();
        request.setToolCode("LADW");
        request.setRentalDayCount(1);
        request.setDiscountPercent(0);
        request.setCheckoutDate(LocalDate.of(2023, 7, 8));

        RentalAgreement expectedAgreement = RentalAgreement.builder()
                .toolCode(ladder.getTool_code())
                .toolType(ladder.getTool_type())
                .toolBrand(ladder.getBrand())
                .rentalDays(request.getRentalDayCount())
                .checkoutDate(request.getCheckoutDate().format(dateTimeFormatter))
                .dueDate(LocalDate.of(2023, 7, 9).format(dateTimeFormatter))
                .dailyRentalCharge(numberFormat.format(ladder.getDaily_charge()))
                .chargeDays(1)
                .preDiscountCharge(numberFormat.format(new BigDecimal("1.99")))
                .discountPercent("0%")
                .discountAmount(numberFormat.format(new BigDecimal("0.00")))
                .finalCharge(numberFormat.format(new BigDecimal("1.99")))
                .build();

        // When
        when(toolRetrievalDao.checkoutTool(request)).thenReturn(ladder);

        // Then
        RentalAgreement actualAgreement = toolCheckout.toolCheckout(request);
        assertEquals(expectedAgreement, actualAgreement);
    }
}
