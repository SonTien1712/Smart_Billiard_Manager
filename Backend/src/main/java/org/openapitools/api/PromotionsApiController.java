package org.openapitools.api;

import com.BillardManagement.Entity.Promotion;
import com.BillardManagement.Service.PromotionService;
import org.openapitools.model.PromotionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/api/promotions")
public class PromotionsApiController {
    private final PromotionService promotionService;
    private final NativeWebRequest request;

    @Autowired
    public PromotionsApiController(PromotionService promotionService, NativeWebRequest request) {
        this.promotionService = promotionService;
        this.request = request;
    }

    @GetMapping("/{promotionId}")
    public ResponseEntity<PromotionDTO> getPromotion(@PathVariable Integer promotionId) {
        Optional<Promotion> promotionOpt = promotionService.getPromotionById(promotionId);
        if (promotionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Promotion promotion = promotionOpt.get();
        PromotionDTO dto = mapToDTO(promotion);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{promotionId}")
    public ResponseEntity<PromotionDTO> updatePromotion(@PathVariable Integer promotionId, @Valid @RequestBody PromotionDTO promotionDTO) {
        Optional<Promotion> promotionOpt = promotionService.getPromotionById(promotionId);
        if (promotionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Promotion existing = promotionOpt.get();
        // Map fields from DTO to entity
        if (promotionDTO.getPromotionName() != null)
            existing.setPromotionName(promotionDTO.getPromotionName());
        if (promotionDTO.getPromotionCode() != null)
            existing.setPromotionCode(promotionDTO.getPromotionCode());
        if (promotionDTO.getDiscountType() != null)
            existing.setDiscountType(promotionDTO.getDiscountType());
        if (promotionDTO.getDiscountValue() != null)
            existing.setDiscountValue(promotionDTO.getDiscountValue());
        if (promotionDTO.getStartDate() != null)
            existing.setStartDate(promotionDTO.getStartDate().toInstant());
        if (promotionDTO.getEndDate() != null)
            existing.setEndDate(promotionDTO.getEndDate().toInstant());
        if (promotionDTO.getIsActive() != null)
            existing.setIsActive(promotionDTO.getIsActive());
        Promotion updated = promotionService.save(existing);
        PromotionDTO dto = mapToDTO(updated);
        return ResponseEntity.ok(dto);
    }

    private PromotionDTO mapToDTO(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        dto.setPromotionId(promotion.getId());
        // Map other fields as needed
        dto.setPromotionName(promotion.getPromotionName());
        dto.setPromotionCode(promotion.getPromotionCode());
        // You may need to convert types for discountType, startDate, endDate, etc.
        // ...existing code...
        return dto;
    }
}

