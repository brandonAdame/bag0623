package com.example.cfdemo.controller;

import com.example.cfdemo.pojo.Checkout;
import com.example.cfdemo.pojo.RentalAgreement;
import com.example.cfdemo.pojo.Tool;
import com.example.cfdemo.service.ToolCheckout;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
    private Tool jackhammerRigid;
    private Tool jackhammerDewalt;

    private final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");;

    @BeforeEach
    void setup() {
        jackhammerRigid = new Tool();
        jackhammerRigid.setTool_code("JAKR");
        jackhammerRigid.setTool_type("Jackhammer");
        jackhammerRigid.setBrand("Rigid");
        jackhammerRigid.setDaily_charge(new BigDecimal("2.99"));
        jackhammerRigid.setWeekday_charge(true);
        jackhammerRigid.setWeekend_charge(false);
        jackhammerRigid.setHoliday_charge(false);

        jackhammerDewalt = new Tool();
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
    @DisplayName("Jackhammer Bad Request DiscountPercent")
    void checkoutJackhammer_BadRequest_DiscountPercent() throws Exception {
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response").value("discountPercent: not in the range 0-100"));
    }

    @Test
    @DisplayName("Checkout Ladder")
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
                .checkoutDate(request.getCheckoutDate().format(dateTimeFormatter))
                .dueDate(LocalDate.of(2020, 7, 5).format(dateTimeFormatter))
                .dailyRentalCharge(numberFormat.format(ladder.getDaily_charge()))
                .chargeDays(3)
                .preDiscountCharge(numberFormat.format(new BigDecimal("5.97")))
                .discountPercent("10%")
                .discountAmount(numberFormat.format(new BigDecimal("0.60")))
                .finalCharge(numberFormat.format(new BigDecimal("5.37")))
                .build();

        // When
        when(toolCheckout.toolCheckout(any())).thenReturn(expectedAgreement);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toolCode").value("LADW"))
                .andExpect(jsonPath("$.rentalDays").value(3))
                .andExpect(jsonPath("$.discountPercent").value("10%"))
                .andExpect(jsonPath("$.dueDate").value("07/05/20"))
                .andExpect(jsonPath("$.finalCharge").value("$5.37"));
    }

    @Test
    @DisplayName("Checkout Chainsaw")
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
                .checkoutDate(request.getCheckoutDate().format(dateTimeFormatter))
                .dueDate(LocalDate.of(2015, 7, 7).format(dateTimeFormatter))
                .dailyRentalCharge(numberFormat.format(chainsaw.getDaily_charge()))
                .chargeDays(3)
                .preDiscountCharge(numberFormat.format(chainsaw.getDaily_charge()))
                .discountPercent("25%")
                .discountAmount(numberFormat.format(new BigDecimal("1.12")))
                .finalCharge(numberFormat.format(new BigDecimal("3.35")))
                .build();

        // When
        when(toolCheckout.toolCheckout(any())).thenReturn(expectedAgreement);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toolCode").value("CHNS"))
                .andExpect(jsonPath("$.rentalDays").value(5))
                .andExpect(jsonPath("$.finalCharge").value("$3.35"))
                .andExpect(jsonPath("$.discountPercent").value("25%"))
                .andExpect(jsonPath("$.dueDate").value("07/07/15"));
    }

    @Test
    @DisplayName("Checkout Jackhammer DeWalt")
    public void testCheckoutJackhammer_DeWalt() throws Exception {
        // Given
        Checkout request = new Checkout();
        request.setToolCode("JAKD");
        request.setRentalDayCount(6);
        request.setDiscountPercent(0);
        request.setCheckoutDate(LocalDate.of(2015, 9, 3));

        RentalAgreement expectedAgreement = RentalAgreement.builder()
                .toolCode(jackhammerDewalt.getTool_code())
                .toolType(jackhammerDewalt.getTool_type())
                .toolBrand(jackhammerDewalt.getBrand())
                .rentalDays(request.getRentalDayCount())
                .checkoutDate(request.getCheckoutDate().format(dateTimeFormatter))
                .dueDate(LocalDate.of(2015, 9, 9).format(dateTimeFormatter))
                .dailyRentalCharge(numberFormat.format(jackhammerDewalt.getDaily_charge()))
                .chargeDays(4)
                .preDiscountCharge(numberFormat.format(new BigDecimal("11.96")))
                .discountPercent("0%")
                .discountAmount(numberFormat.format(new BigDecimal("0.00")))
                .finalCharge(numberFormat.format(new BigDecimal("11.96")))
                .build();

        // When
        when(toolCheckout.toolCheckout(any())).thenReturn(expectedAgreement);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toolCode").value("JAKD"))
                .andExpect(jsonPath("$.rentalDays").value(6))
                .andExpect(jsonPath("$.discountPercent").value("0%"))
                .andExpect(jsonPath("$.dueDate").value("09/09/15"))
                .andExpect(jsonPath("$.finalCharge").value("$11.96"));
    }

    @Test
    @DisplayName("Checkout Jackhammer Ridgid")
    public void testCheckoutJackhammer_Ridgid() throws Exception {
        // Given
        Checkout request = new Checkout();
        request.setToolCode("JAKR");
        request.setRentalDayCount(9);
        request.setDiscountPercent(0);
        request.setCheckoutDate(LocalDate.of(2015, 7, 2));

        RentalAgreement expectedAgreement = RentalAgreement.builder()
                .toolCode(jackhammerRigid.getTool_code())
                .toolType(jackhammerRigid.getTool_type())
                .toolBrand(jackhammerRigid.getBrand())
                .rentalDays(request.getRentalDayCount())
                .checkoutDate(request.getCheckoutDate().format(dateTimeFormatter))
                .dueDate(LocalDate.of(2015, 7, 11).format(dateTimeFormatter))
                .dailyRentalCharge(numberFormat.format(jackhammerRigid.getDaily_charge()))
                .chargeDays(6)
                .preDiscountCharge(numberFormat.format(new BigDecimal("17.94")))
                .discountPercent("0%")
                .discountAmount(numberFormat.format(new BigDecimal("0.00")))
                .finalCharge(numberFormat.format(new BigDecimal("17.94")))
                .build();

        // When
        when(toolCheckout.toolCheckout(any())).thenReturn(expectedAgreement);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toolCode").value("JAKR"))
                .andExpect(jsonPath("$.rentalDays").value(9))
                .andExpect(jsonPath("$.discountPercent").value("0%"))
                .andExpect(jsonPath("$.dueDate").value("07/11/15"))
                .andExpect(jsonPath("$.finalCharge").value("$17.94"));
    }

    @Test
    @DisplayName("Checkout Ridgid Fifty Percent Off")
    public void testCheckoutJackhammer_Ridgid_FiftyPercentOff() throws Exception {
        // Given
        Checkout request = new Checkout();
        request.setToolCode("JAKR");
        request.setRentalDayCount(4);
        request.setDiscountPercent(50);
        request.setCheckoutDate(LocalDate.of(2020, 7, 2));

        RentalAgreement expectedAgreement = RentalAgreement.builder()
                .toolCode(jackhammerRigid.getTool_code())
                .toolType(jackhammerRigid.getTool_type())
                .toolBrand(jackhammerRigid.getBrand())
                .rentalDays(request.getRentalDayCount())
                .checkoutDate(request.getCheckoutDate().format(dateTimeFormatter))
                .dueDate(LocalDate.of(2020, 7, 6).format(dateTimeFormatter))
                .dailyRentalCharge(numberFormat.format(jackhammerRigid.getDaily_charge()))
                .chargeDays(2)
                .preDiscountCharge(numberFormat.format(new BigDecimal("5.98")))
                .discountPercent("50%")
                .discountAmount(numberFormat.format(new BigDecimal("2.99")))
                .finalCharge(numberFormat.format(new BigDecimal("2.99")))
                .build();

        // When
        when(toolCheckout.toolCheckout(any())).thenReturn(expectedAgreement);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toolCode").value("JAKR"))
                .andExpect(jsonPath("$.rentalDays").value(4))
                .andExpect(jsonPath("$.discountPercent").value("50%"))
                .andExpect(jsonPath("$.dueDate").value("07/06/20"))
                .andExpect(jsonPath("$.finalCharge").value("$2.99"));
    }

    @Test
    @DisplayName("Checkout Jackhammer BadRequest RentalDayCount")
    void checkoutJackhammer_BadRequest_RentalDayCount() throws Exception {
        // Given
        Checkout request = new Checkout();
        request.setToolCode("JAKR");
        request.setRentalDayCount(0);
        request.setDiscountPercent(0); // percent must be 0 - 100
        request.setCheckoutDate(LocalDate.of(2015, 9, 3));

        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response").value("rentalDayCount: must be greater than or equal to 1"));
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
