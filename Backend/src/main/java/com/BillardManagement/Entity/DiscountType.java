package com.BillardManagement.Entity;

public enum DiscountType {
    PERCENTAGE("Percentage"),
    FIXED_AMOUNT("FixedAmount");

    private final String value;

    DiscountType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
