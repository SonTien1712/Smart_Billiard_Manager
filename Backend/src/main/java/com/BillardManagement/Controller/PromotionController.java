package com.BillardManagement.Controller;

import com.BillardManagement.DTO.PromotionDTO;
import com.BillardManagement.DTO.CreatePromotionRequest;
import com.BillardManagement.DTO.UpdatePromotionRequest;
import com.BillardManagement.Entity.Promotion;
import com.BillardManagement.Service.PromotionService;
import com.BillardManagement.Mapper.PromotionMapper;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;
    private final PromotionMapper promotionMapper;

    /**
     * Lấy tất cả promotion với phân trang
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPromotions(
            @RequestParam(required = false) Integer clubId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Promotion> promotionPage = clubId != null
                ? promotionService.getPromotionsByClub(clubId, pageable)
                : promotionService.getAllPromotions(pageable);

        List<PromotionDTO> promotions = promotionPage.getContent().stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("promotions", promotions);
        response.put("currentPage", promotionPage.getNumber());
        response.put("totalItems", promotionPage.getTotalElements());
        response.put("totalPages", promotionPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * Lấy promotion theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PromotionDTO> getPromotionById(@PathVariable Integer id) {
        Promotion promotion = promotionService.getPromotionById(id);
        return ResponseEntity.ok(promotionMapper.toDTO(promotion));
    }

    /**
     * Lấy promotion đang active của club
     */
    @GetMapping("/active")
    public ResponseEntity<List<PromotionDTO>> getActivePromotions(
            @RequestParam Integer clubId) {
        List<Promotion> promotions = promotionService.getActivePromotions(clubId);
        List<PromotionDTO> dtos = promotions.stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Lấy promotion theo code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<PromotionDTO> getPromotionByCode(@PathVariable String code) {
        Promotion promotion = promotionService.getPromotionByCode(code);
        return ResponseEntity.ok(promotionMapper.toDTO(promotion));
    }

    /**
     * Tạo promotion mới
     */
    @PostMapping
    public ResponseEntity<PromotionDTO> createPromotion(
            @Valid @RequestBody CreatePromotionRequest request) {
        Promotion promotion = promotionMapper.toEntity(request);
        Promotion created = promotionService.createPromotion(promotion);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(promotionMapper.toDTO(created));
    }

    /**
     * Cập nhật promotion (PUT - full update)
     */
    @PutMapping("/{id}")
    public ResponseEntity<PromotionDTO> updatePromotionFull(
            @PathVariable Integer id,
            @Valid @RequestBody CreatePromotionRequest request) {
        Promotion promotion = promotionMapper.toEntity(request);
        promotion.setId(id);
        Promotion updated = promotionService.updatePromotion(id, promotion);
        return ResponseEntity.ok(promotionMapper.toDTO(updated));
    }

    /**
     * Cập nhật promotion (PATCH - partial update)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<PromotionDTO> updatePromotionPartial(
            @PathVariable Integer id,
            @Valid @RequestBody UpdatePromotionRequest request) {
        Promotion partialData = promotionMapper.toEntity(request);
        Promotion updated = promotionService.updatePromotion(id, partialData);
        return ResponseEntity.ok(promotionMapper.toDTO(updated));
    }

    /**
     * Xóa promotion (soft delete - deactivate)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePromotion(@PathVariable Integer id) {
        promotionService.deletePromotion(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Xóa khuyến mãi thành công");
        return ResponseEntity.ok(response);
    }

    /**
     * Apply promotion code (để sử dụng)
     */
    @PostMapping("/apply")
    public ResponseEntity<PromotionDTO> applyPromotion(
            @RequestParam String code,
            @RequestParam Integer clubId) {
        Promotion promotion = promotionService.applyPromotion(code, clubId);
        return ResponseEntity.ok(promotionMapper.toDTO(promotion));
    }

    /**
     * Toggle active status
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<PromotionDTO> toggleActiveStatus(@PathVariable Integer id) {
        Promotion promotion = promotionService.getPromotionById(id);
        promotion.setIsActive(!promotion.getIsActive());
        Promotion updated = promotionService.updatePromotion(id, promotion);
        return ResponseEntity.ok(promotionMapper.toDTO(updated));
    }
}