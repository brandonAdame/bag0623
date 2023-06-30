package com.example.cfdemo.controller;

import com.example.cfdemo.pojo.Checkout;
import com.example.cfdemo.pojo.ErrorResponse;
import com.example.cfdemo.pojo.RentalAgreement;
import com.example.cfdemo.service.ToolCheckout;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.date.HolidayCalendar;
import com.opengamma.strata.basics.date.HolidayCalendarId;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@RestController
public class MainController {

    public ToolCheckout checkoutService;

    public MainController(ToolCheckout checkoutService) {
        this.checkoutService = checkoutService;
    }

    @GetMapping(path = "/checkout", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<RentalAgreement> checkout(@Valid @RequestBody Checkout checkoutRequest) {
        log.info("Checkout request for: {}", checkoutRequest);

        RentalAgreement rentalAgreement = checkoutService.toolCheckout(checkoutRequest);

        log.info("{}", rentalAgreement);

        return ResponseEntity.ok(rentalAgreement);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> badRequestHandler(MethodArgumentNotValidException exception) {
        String field = Objects.requireNonNull(exception.getFieldError()).getField();
        String errorMsg;

        switch (field) {
            case "discountPercent":
                errorMsg = "not in the range 0-100";
                break;
            default:
                errorMsg = exception.getFieldError().getDefaultMessage();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(field).append(": ").append(errorMsg);

        return ResponseEntity.ok(ErrorResponse.builder().response(sb.toString()).build());
    }
}
