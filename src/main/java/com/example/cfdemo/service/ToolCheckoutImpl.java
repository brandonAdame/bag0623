package com.example.cfdemo.service;

import com.example.cfdemo.dao.ToolRetrievalDao;
import com.example.cfdemo.pojo.Checkout;
import com.example.cfdemo.pojo.RentalAgreement;
import com.example.cfdemo.pojo.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

import static java.time.temporal.TemporalAdjusters.firstInMonth;

@Slf4j
@Service
public class ToolCheckoutImpl implements ToolCheckout {

    ToolRetrievalDao toolRetrievalDao;
    NumberFormat numberFormat;
    DateTimeFormatter dateTimeFormatter;

    public ToolCheckoutImpl(ToolRetrievalDao toolRetrievalDao, NumberFormat numberFormat, DateTimeFormatter dateTimeFormatter) {
        this.toolRetrievalDao = toolRetrievalDao;
        this.numberFormat = numberFormat;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public RentalAgreement toolCheckout(Checkout checkoutRequest) {
        Tool tool = toolRetrievalDao.checkoutTool(checkoutRequest);

        log.info("{}", tool);
        return generateRentalAgreement(tool, checkoutRequest);
    }

    /**
     * Generate rental agreement from tool and checkout request
     *
     * @param tool            Tool
     * @param checkoutRequest Customer checkout request
     * @return Tool rental agreement
     */
    private RentalAgreement generateRentalAgreement(Tool tool, Checkout checkoutRequest) {
        int chargeDays = generateChargeDays(tool, checkoutRequest);
        BigDecimal preDiscount = generatePreDiscountCharge(tool, chargeDays);
        BigDecimal discountAmount = generateDiscountAmount(preDiscount, checkoutRequest);
        BigDecimal finalCharge = generateFinalCharge(preDiscount, discountAmount);

        return RentalAgreement.builder()
                .toolCode(checkoutRequest.getToolCode())
                .toolType(tool.getTool_type())
                .toolBrand(tool.getBrand())
                .rentalDays(checkoutRequest.getRentalDayCount())
                .checkoutDate(checkoutRequest.getCheckoutDate().format(dateTimeFormatter))
                .dueDate(checkoutRequest.getCheckoutDate().plusDays(checkoutRequest.getRentalDayCount()).format(dateTimeFormatter))
                .dailyRentalCharge(numberFormat.format(tool.getDaily_charge()))
                .chargeDays(chargeDays)
                .preDiscountCharge(numberFormat.format(preDiscount))
                .discountPercent(checkoutRequest.getDiscountPercent())
                .discountAmount(numberFormat.format(discountAmount))
                .finalCharge(numberFormat.format(finalCharge))
                .build();
    }

    /**
     * Generate final tool charge from pre-discount and discount amount
     *
     * @param preDiscountAmount Chargeable amount prior to discount
     * @param discountAmount    Discount amount
     * @return Final tool charge
     */
    private BigDecimal generateFinalCharge(BigDecimal preDiscountAmount, BigDecimal discountAmount) {
        return preDiscountAmount.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Generate discount percentage
     *
     * @param preDiscountAmount Chargeable amount prior to discount
     * @param checkoutRequest   Tool checkout request
     * @return Discount amount
     */
    private BigDecimal generateDiscountAmount(BigDecimal preDiscountAmount, Checkout checkoutRequest) {
        BigDecimal discountPercent = new BigDecimal(checkoutRequest.getDiscountPercent())
                .multiply(new BigDecimal(".1")).multiply(new BigDecimal(".1"));
        return preDiscountAmount.multiply(discountPercent).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Generate pre-discount charge for a tool.
     *
     * @param tool       Tool
     * @param chargeDays Chargeable days
     * @return Pre-discount charge based on chargeable days
     */
    private BigDecimal generatePreDiscountCharge(Tool tool, int chargeDays) {
        return tool.getDaily_charge()
                .multiply(BigDecimal.valueOf(chargeDays))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Returns the amount of days that are chargeable based on <code>rentalDayCount</code>.
     *
     * @param tool            Tool
     * @param checkoutRequest Customer checkout request
     * @return Amount of chargeable days
     */
    private int generateChargeDays(Tool tool, Checkout checkoutRequest) {
        int chargeDays = 0;
        for (int i = 0; i < checkoutRequest.getRentalDayCount(); i++) {
            LocalDate currDate = checkoutRequest.getCheckoutDate().plusDays(i);
            log.debug("Checking date: {}", currDate);

            if (isChargeableDay(tool, currDate)) {
                chargeDays++;
            }
        }
        return chargeDays;
    }

    /**
     * Checks if tool on a <code>date</code> is chargeable
     *
     * @param tool Tool
     * @param date Date
     * @return True if date is chargeable; false otherwise
     */
    private boolean isChargeableDay(Tool tool, LocalDate date) {
        return (tool.isWeekday_charge() && !isWeekend(date))
                || (tool.isWeekend_charge() && isWeekend(date))
                || (tool.isHoliday_charge() && isHoliday(date));
    }

    /**
     * Checks if <code>date</code> is a weekend
     *
     * @param date Date to check against
     * @return True if date is on the weekend; false otherwise
     */
    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = DayOfWeek.of(date.get(ChronoField.DAY_OF_WEEK));
        return day == DayOfWeek.SUNDAY || day == DayOfWeek.SATURDAY;
    }

    /**
     * Returns true if date is a supported holiday; false otherwise.
     *
     * @param date Checkout date
     * @return True if is holiday; false otherwise
     */
    private boolean isHoliday(LocalDate date) {
        if ((date.getMonth() == Month.SEPTEMBER) && (date == date.with(firstInMonth(DayOfWeek.MONDAY)))) {
            return true;
        } else return (date.getMonth() == Month.JULY) && (date.getDayOfMonth() == 4);
    }
}
