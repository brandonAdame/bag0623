package com.example.cfdemo.controller;

import com.example.cfdemo.pojo.Checkout;
import com.example.cfdemo.pojo.RentalAgreement;
import com.example.cfdemo.pojo.Tool;
import com.example.cfdemo.service.ToolCheckout;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MainController.class)
public class MainControllerTest {

    @MockBean
    private ToolCheckout toolCheckout;

    @Autowired
    private MockMvc mockMvc;

    private Tool ladder;

    private Tool chainsaw;

    @BeforeEach
    void setup() {
        Tool jackhammerRigid = new Tool();
        jackhammerRigid.setTool_code("JAKR");
        jackhammerRigid.setTool_type("Jackhammer");
        jackhammerRigid.setBrand("Rigid");
        jackhammerRigid.setDaily_charge(new BigDecimal("2.99"));
        jackhammerRigid.setWeekday_charge(true);
        jackhammerRigid.setWeekend_charge(false);
        jackhammerRigid.setHoliday_charge(false);

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

        chainsaw = new Tool();
        chainsaw.setTool_code("CHNS");
        chainsaw.setTool_type("Chainsaw");
        chainsaw.setBrand("Stihl");
        chainsaw.setDaily_charge(new BigDecimal("1.49"));
        chainsaw.setWeekday_charge(true);
        chainsaw.setWeekend_charge(false);
        chainsaw.setHoliday_charge(true);
    }

    @Test
    void checkoutJackhammer_BadRequest() throws Exception {
        // Given
        Checkout request = new Checkout();
        request.setToolCode("JAKR");
        request.setRentalDayCount(5);
        request.setDiscountPercent(101); // percent must be 0 - 100
        request.setCheckoutDate(LocalDate.of(2015, 9, 3));

        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCheckoutLadder() throws Exception {
        // Given
        Checkout request = new Checkout();
        request.setToolCode("LADW");
        request.setRentalDayCount(3);
        request.setDiscountPercent(10);
        request.setCheckoutDate(LocalDate.of(2020, 7, 2));

        RentalAgreement expectedAgreement = RentalAgreement.builder()
                .toolCode(ladder.getTool_code())
                .toolType(ladder.getTool_type())
                .toolBrand(ladder.getBrand())
                .rentalDays(request.getRentalDayCount())
                .checkoutDate(request.getCheckoutDate())
                .dueDate(LocalDate.of(2020, 7, 5))
                .dailyRentalCharge(ladder.getDaily_charge())
                .chargeDays(3)
                .preDiscountCharge(new BigDecimal("5.97"))
                .discountPercent(request.getDiscountPercent())
                .discountAmount(new BigDecimal(".597"))
                .finalCharge(new BigDecimal("5.37"))
                .build();

        // When
        when(toolCheckout.toolCheckout(any())).thenReturn(expectedAgreement);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testCheckoutChainsaw() throws Exception {
        // Given
        Checkout request = new Checkout();
        request.setToolCode("CHNS");
        request.setRentalDayCount(5);
        request.setDiscountPercent(25);
        request.setCheckoutDate(LocalDate.of(2015, 7, 2));

        RentalAgreement expectedAgreement = RentalAgreement.builder()
                .toolCode(chainsaw.getTool_code())
                .toolType(chainsaw.getTool_type())
                .toolBrand(chainsaw.getBrand())
                .rentalDays(request.getRentalDayCount())
                .checkoutDate(request.getCheckoutDate())
                .dueDate(LocalDate.of(2015, 7, 7))
                .dailyRentalCharge(chainsaw.getDaily_charge())
                .chargeDays(3)
                .preDiscountCharge(new BigDecimal("4.47"))
                .discountPercent(request.getDiscountPercent())
                .discountAmount(new BigDecimal("1.12"))
                .finalCharge(new BigDecimal("3.35"))
                .build();

        // When
        when(toolCheckout.toolCheckout(any())).thenReturn(expectedAgreement);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testCheckoutJackhammer_DeWalt() throws Exception {
        // Given
        Checkout request = new Checkout();
        request.setToolCode("JAKD");
        request.setRentalDayCount(6);
        request.setDiscountPercent(0);
        request.setCheckoutDate(LocalDate.of(2015, 9, 3));

        RentalAgreement expectedAgreement = RentalAgreement.builder()
                .toolCode(chainsaw.getTool_code())
                .toolType(chainsaw.getTool_type())
                .toolBrand(chainsaw.getBrand())
                .rentalDays(request.getRentalDayCount())
                .checkoutDate(request.getCheckoutDate())
                .dueDate(LocalDate.of(2015, 9, 9))
                .dailyRentalCharge(chainsaw.getDaily_charge())
                .chargeDays(4)
                .preDiscountCharge(new BigDecimal("11.96"))
                .discountPercent(request.getDiscountPercent())
                .discountAmount(new BigDecimal("0.00"))
                .finalCharge(new BigDecimal("11.96"))
                .build();

        // When
        when(toolCheckout.toolCheckout(any())).thenReturn(expectedAgreement);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testCheckoutJackhammer_Ridgid() throws Exception {
        // Given
        Checkout request = new Checkout();
        request.setToolCode("JAKR");
        request.setRentalDayCount(9);
        request.setDiscountPercent(0);
        request.setCheckoutDate(LocalDate.of(2015, 7, 2));

        RentalAgreement expectedAgreement = RentalAgreement.builder()
                .toolCode(chainsaw.getTool_code())
                .toolType(chainsaw.getTool_type())
                .toolBrand(chainsaw.getBrand())
                .rentalDays(request.getRentalDayCount())
                .checkoutDate(request.getCheckoutDate())
                .dueDate(LocalDate.of(2015, 7, 11))
                .dailyRentalCharge(chainsaw.getDaily_charge())
                .chargeDays(6)
                .preDiscountCharge(new BigDecimal("17.94"))
                .discountPercent(request.getDiscountPercent())
                .discountAmount(new BigDecimal("0.00"))
                .finalCharge(new BigDecimal("17.94"))
                .build();

        // When
        when(toolCheckout.toolCheckout(any())).thenReturn(expectedAgreement);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testCheckoutJackhammer_Ridgid_FiftyPercentOff() throws Exception {
        // Given
        Checkout request = new Checkout();
        request.setToolCode("JAKR");
        request.setRentalDayCount(4);
        request.setDiscountPercent(50);
        request.setCheckoutDate(LocalDate.of(2020, 7, 2));

        RentalAgreement expectedAgreement = RentalAgreement.builder()
                .toolCode(chainsaw.getTool_code())
                .toolType(chainsaw.getTool_type())
                .toolBrand(chainsaw.getBrand())
                .rentalDays(request.getRentalDayCount())
                .checkoutDate(request.getCheckoutDate())
                .dueDate(LocalDate.of(2020, 7, 6))
                .dailyRentalCharge(chainsaw.getDaily_charge())
                .chargeDays(2)
                .preDiscountCharge(new BigDecimal("5.98"))
                .discountPercent(request.getDiscountPercent())
                .discountAmount(new BigDecimal("2.99"))
                .finalCharge(new BigDecimal("2.99"))
                .build();

        // When
        when(toolCheckout.toolCheckout(any())).thenReturn(expectedAgreement);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
