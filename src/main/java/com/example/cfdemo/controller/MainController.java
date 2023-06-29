package com.example.cfdemo.controller;

import com.example.cfdemo.pojo.Checkout;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.date.HolidayCalendar;
import com.opengamma.strata.basics.date.HolidayCalendarId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
public class MainController {

    @GetMapping(path = "/holiday", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> holiday() {
        HolidayCalendarId calendarId = HolidayCalendarId.of("USNY");
        HolidayCalendar holidayCalendar = calendarId.resolve(ReferenceData.standard());

        boolean isHoliday = holidayCalendar.isHoliday(LocalDate.of(2023, 9, 4));
        return ResponseEntity.ok(isHoliday ? "Holiday" : "Not Holiday");
    }

    /**
     * Use java.time to get the first monday of the month for September (Labor Day)
     */

    @GetMapping(path = "/checkout", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> checkout(@RequestBody Checkout checkout) {
        log.info("Checkout request for: {}", checkout);

        return ResponseEntity.ok("Checkout");
    }
}
