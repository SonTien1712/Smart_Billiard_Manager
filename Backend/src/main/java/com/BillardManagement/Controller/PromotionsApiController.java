// PromotionsApiController.java
package com.BillardManagement.Controller;

import com.BillardManagement.Entity.Promotion;
import com.BillardManagement.Service.PromotionService;
import com.BillardManagement.DTO.PromotionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.ZoneOffset;
import java.util.Optional;

@Controller
@RequestMapping("/api/promotions")
public class PromotionsApiController {
    private final PromotionService promotionService;

    @Autowired
    public PromotionsApiController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping("/{promotionId}")
    public ResponseEntity<PromotionDTO> getPromotion(@PathVariable Integer promotionId) {
        Optional<Promotion> promotionOpt = promotionService.getPromotionById(promotionId);
        if (promotionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        PromotionDTO dto = mapToDTO(promotionOpt.get());
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{promotionId}")
    public ResponseEntity<PromotionDTO> updatePromotion(
            @PathVariable Integer promotionId,
            @Valid @RequestBody PromotionDTO promotionDTO) {

        Optional<Promotion> promotionOpt = promotionService.getPromotionById(promotionId);
        if (promotionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Promotion existing = promotionOpt.get();
        updateEntityFromDTO(existing, promotionDTO);

        Promotion updated = promotionService.save(existing);
        PromotionDTO dto = mapToDTO(updated);
        return ResponseEntity.ok(dto);
    }

    private void updateEntityFromDTO(Promotion entity, PromotionDTO dto) {
        if (dto.getPromotionName() != null) {
            entity.setPromotionName(dto.getPromotionName());
        }
        if (dto.getPromotionCode() != null) {
            entity.setPromotionCode(dto.getPromotionCode());
        }
        if (dto.getDiscountType() != null) {
            entity.setDiscountType(dto.getDiscountType()); // Direct assignment!
        }
        if (dto.getDiscountValue() != null) {
            entity.setDiscountValue(dto.getDiscountValue());
        }
        if (dto.getStartDate() != null) {
            entity.setStartDate(dto.getStartDate().toInstant());
        }
        if (dto.getEndDate() != null) {
            entity.setEndDate(dto.getEndDate().toInstant());
        }
        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }
    }

    private PromotionDTO mapToDTO(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        dto.setPromotionId(promotion.getId());
        dto.setPromotionName(promotion.getPromotionName());
        dto.setPromotionCode(promotion.getPromotionCode());
        dto.setDiscountValue(promotion.getDiscountValue());
        dto.setIsActive(promotion.getIsActive());

        // Convert Instant to OffsetDateTime
        if (promotion.getStartDate() != null) {
            dto.setStartDate(promotion.getStartDate().atOffset(ZoneOffset.UTC));
        }
        if (promotion.getEndDate() != null) {
            dto.setEndDate(promotion.getEndDate().atOffset(ZoneOffset.UTC));
        }

        // Convert discountType String to Enum
        if (promotion.getDiscountType() != null) {
            dto.setDiscountType(
                    PromotionDTO.DiscountTypeEnum.valueOf(String.valueOf(promotion.getDiscountType()))
            );
        }

        return dto;
    }
}