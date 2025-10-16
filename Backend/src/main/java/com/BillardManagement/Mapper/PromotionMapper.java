package com.BillardManagement.Mapper;

import com.BillardManagement.DTO.Request.CreatePromotionRequest;
import com.BillardManagement.DTO.PromotionDTO;
import com.BillardManagement.DTO.Request.UpdatePromotionRequest;
import com.BillardManagement.Entity.Billardclub;
import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Entity.Promotion;
import org.springframework.stereotype.Component;

@Component
public class PromotionMapper {

    /**
     * Convert Entity to DTO
     */
    public PromotionDTO toDTO(Promotion promotion) {
        if (promotion == null) {
            return null;
        }

        PromotionDTO dto = new PromotionDTO();
        dto.setPromotionId(promotion.getId());
        dto.setClubId(promotion.getClub() != null ? promotion.getClub().getId() : null);
        dto.setCustomerId(promotion.getCustomer() != null ? promotion.getCustomer().getId() : null);
        dto.setPromotionName(promotion.getPromotionName());
        dto.setPromotionCode(promotion.getPromotionCode());
        dto.setDiscountType(promotion.getDiscountType());
        dto.setDiscountValue(promotion.getDiscountValue());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setApplicableTableTypes(promotion.getApplicableTableTypes());
        dto.setMinPlayTime(promotion.getMinPlayTime());
        dto.setMinAmount(promotion.getMinAmount());
        dto.setMaxDiscount(promotion.getMaxDiscount());
        dto.setUsageLimit(promotion.getUsageLimit());
        dto.setUsedCount(promotion.getUsedCount());
        dto.setIsActive(promotion.getIsActive());
        dto.setDescription(promotion.getDescription());
        dto.setCreatedAt(promotion.getCreatedAt());
        dto.setUpdatedAt(promotion.getUpdatedAt());

        return dto;
    }

    /**
     * Convert CreateRequest to Entity
     */
    public Promotion toEntity(CreatePromotionRequest request) {
        if (request == null) {
            return null;
        }

        Promotion promotion = new Promotion();

        // Set club (chỉ set ID, service sẽ validate)
        if (request.getClubId() != null) {
            Billardclub club = new Billardclub();
            club.setId(request.getClubId());
            promotion.setClub(club);
        }

        // Set customer nếu có
        if (request.getCustomerId() != null) {
            Customer customer = new Customer();
            customer.setId(request.getCustomerId());
            promotion.setCustomer(customer);
        }

        promotion.setPromotionName(request.getPromotionName());
        promotion.setPromotionCode(request.getPromotionCode());
        promotion.setDiscountType(request.getDiscountType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setApplicableTableTypes(request.getApplicableTableTypes());
        promotion.setMinPlayTime(request.getMinPlayTime());
        promotion.setMinAmount(request.getMinAmount());
        promotion.setMaxDiscount(request.getMaxDiscount());
        promotion.setUsageLimit(request.getUsageLimit());
        promotion.setDescription(request.getDescription());

        return promotion;
    }

    /**
     * Convert UpdateRequest to Entity (for partial update)
     */
    public Promotion toEntity(UpdatePromotionRequest request) {
        if (request == null) {
            return null;
        }

        Promotion promotion = new Promotion();
        promotion.setPromotionName(request.getPromotionName());
        promotion.setPromotionCode(request.getPromotionCode());
        promotion.setDiscountType(request.getDiscountType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setApplicableTableTypes(request.getApplicableTableTypes());
        promotion.setMinPlayTime(request.getMinPlayTime());
        promotion.setMinAmount(request.getMinAmount());
        promotion.setMaxDiscount(request.getMaxDiscount());
        promotion.setUsageLimit(request.getUsageLimit());
        promotion.setIsActive(request.getIsActive());
        promotion.setDescription(request.getDescription());

        return promotion;
    }

    /**
     * Update existing entity from DTO (không dùng trong code hiện tại, nhưng có thể hữu ích)
     */
    public void updateEntityFromDTO(Promotion entity, PromotionDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getPromotionName() != null) {
            entity.setPromotionName(dto.getPromotionName());
        }
        if (dto.getPromotionCode() != null) {
            entity.setPromotionCode(dto.getPromotionCode());
        }
        if (dto.getDiscountType() != null) {
            entity.setDiscountType(dto.getDiscountType());
        }
        if (dto.getDiscountValue() != null) {
            entity.setDiscountValue(dto.getDiscountValue());
        }
        if (dto.getStartDate() != null) {
            entity.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            entity.setEndDate(dto.getEndDate());
        }
        if (dto.getApplicableTableTypes() != null) {
            entity.setApplicableTableTypes(dto.getApplicableTableTypes());
        }
        if (dto.getMinPlayTime() != null) {
            entity.setMinPlayTime(dto.getMinPlayTime());
        }
        if (dto.getMinAmount() != null) {
            entity.setMinAmount(dto.getMinAmount());
        }
        if (dto.getMaxDiscount() != null) {
            entity.setMaxDiscount(dto.getMaxDiscount());
        }
        if (dto.getUsageLimit() != null) {
            entity.setUsageLimit(dto.getUsageLimit());
        }
        if (dto.getUsedCount() != null) {
            entity.setUsedCount(dto.getUsedCount());
        }
        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
    }
}