package com.BillardManagement.Entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// PromotionType enum with robust JSON mapping
public enum PromotionType {
    PERCENTAGE("Percentage"),
    FIXED_AMOUNT("FixedAmount");

    private final String label;

    PromotionType(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static PromotionType fromString(String value) {
        if (value == null) return null;
        String v = value.trim();
        // Accept both DB/JSON variants and enum names
        if (v.equalsIgnoreCase("Percentage") || v.equalsIgnoreCase("PERCENTAGE")) {
            return PERCENTAGE;
        }
        if (v.equalsIgnoreCase("FixedAmount") || v.equalsIgnoreCase("FIXED_AMOUNT") || v.equalsIgnoreCase("FIXEDAMOUNT") || v.equalsIgnoreCase("Fixed_Amount")) {
            return FIXED_AMOUNT;
        }
        // Fallback: try standard enum name resolution after normalizing
        try {
            return PromotionType.valueOf(v.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid PromotionType value: " + value);
        }
    }
}
