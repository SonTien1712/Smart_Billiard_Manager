package com.BillardManagement.Entity.converter;

import com.BillardManagement.Entity.PromotionType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PromotionTypeConverter implements AttributeConverter<PromotionType, String> {

    @Override
    public String convertToDatabaseColumn(PromotionType attribute) {
        if (attribute == null) return null;
        // Persist in DB using legacy-friendly labels to support existing rows
        switch (attribute) {
            case PERCENTAGE:
                return "Percentage";
            case FIXED_AMOUNT:
                return "FixedAmount";
            default:
                return attribute.name();
        }
    }

    @Override
    public PromotionType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        String v = dbData.trim();
        if (v.equalsIgnoreCase("Percentage") || v.equalsIgnoreCase("PERCENTAGE")) {
            return PromotionType.PERCENTAGE;
        }
        if (v.equalsIgnoreCase("FixedAmount") || v.equalsIgnoreCase("FIXED_AMOUNT") || v.equalsIgnoreCase("FIXEDAMOUNT") || v.equalsIgnoreCase("Fixed_Amount")) {
            return PromotionType.FIXED_AMOUNT;
        }
        // Try enum name as a last resort
        return PromotionType.valueOf(v.toUpperCase());
    }
}
