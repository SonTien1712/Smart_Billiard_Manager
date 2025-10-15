package com.BillardManagement.Service;

import com.BillardManagement.Entity.Promotion;
import com.BillardManagement.Repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    public Optional<Promotion> getPromotionById(Integer id) {
        return promotionRepository.findById(id);
    }

    public Promotion save(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    public Promotion updatePromotion(Integer id, Promotion updatedData) {
        return promotionRepository.findById(id).map(existing -> {
            if (updatedData.getPromotionName() != null)
                existing.setPromotionName(updatedData.getPromotionName());
            if (updatedData.getPromotionCode() != null)
                existing.setPromotionCode(updatedData.getPromotionCode());
            if (updatedData.getDiscountType() != null)
                existing.setDiscountType(updatedData.getDiscountType());
            if (updatedData.getDiscountValue() != null)
                existing.setDiscountValue(updatedData.getDiscountValue());
            if (updatedData.getStartDate() != null)
                existing.setStartDate(updatedData.getStartDate());
            if (updatedData.getEndDate() != null)
                existing.setEndDate(updatedData.getEndDate());
            if (updatedData.getIsActive() != null)
                existing.setIsActive(updatedData.getIsActive());
            if (updatedData.getDescription() != null)
                existing.setDescription(updatedData.getDescription());
            if (updatedData.getApplicableTableTypes() != null)
                existing.setApplicableTableTypes(updatedData.getApplicableTableTypes());
            if (updatedData.getMinPlayTime() != null)
                existing.setMinPlayTime(updatedData.getMinPlayTime());
            if (updatedData.getMinAmount() != null)
                existing.setMinAmount(updatedData.getMinAmount());
            if (updatedData.getMaxDiscount() != null)
                existing.setMaxDiscount(updatedData.getMaxDiscount());
            if (updatedData.getUsageLimit() != null)
                existing.setUsageLimit(updatedData.getUsageLimit());
            if (updatedData.getUsedCount() != null)
                existing.setUsedCount(updatedData.getUsedCount());

            return promotionRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Promotion not found with id " + id));
    }
}
