package com.example.cfdemo.enums;

/**
 * Supported holidays for the POS system
 */
public enum HolidayEnums {
    INDEPENDENCE_DAY("IndependenceDay"),
    LABOR_DAY("LaborDay"),
    NO_HOLIDAY("NoHoliday");

    private final String name;

    HolidayEnums(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
