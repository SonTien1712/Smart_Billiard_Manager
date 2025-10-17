package com.BillardManagement.Service;

import com.BillardManagement.Entity.Billardclub;
import com.BillardManagement.Entity.Customer;
import com.BillardManagement.Entity.Promotion;
import com.BillardManagement.Repository.BilliardClubRepo;
import com.BillardManagement.Repository.CustomerRepo;
import com.BillardManagement.Repository.PromotionRepository;
import com.BillardManagement.Exception.ResourceNotFoundException;
import com.BillardManagement.Exception.BusinessException;
import lombok.RequiredArgsConstructor;
import com.BillardManagement.Entity.DiscountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final BilliardClubRepo clubRepository;
    private final CustomerRepo customerRepository;

    public Promotion getPromotionById(Integer id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khuyến mãi với ID: " + id));
    }

    public Page<Promotion> getPromotionsByClub(Integer clubId, Pageable pageable) {
        return promotionRepository.findByClubId(clubId, pageable);
    }

    public List<Promotion> getActivePromotions(Integer clubId) {
        return promotionRepository.findActivePromotionsByClub(clubId, Instant.now());
    }

    public Promotion getPromotionByCode(String code) {
        return promotionRepository.findByPromotionCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khuyến mãi với mã: " + code));
    }

    @Transactional
    public Promotion createPromotion(Promotion promotion) {
        validatePromotion(promotion, true);

        // Set default values
        if (promotion.getUsedCount() == null) promotion.setUsedCount(0);
        if (promotion.getIsActive() == null) promotion.setIsActive(true);

        return promotionRepository.save(promotion);
    }

    @Transactional
    public Promotion updatePromotion(Integer id, Promotion updatedData) {
        Promotion existing = getPromotionById(id);

        // Update only non-null fields
        if (updatedData.getPromotionName() != null) {
            existing.setPromotionName(updatedData.getPromotionName());
        }
        if (updatedData.getPromotionCode() != null &&
                !updatedData.getPromotionCode().equals(existing.getPromotionCode())) {
            if (promotionRepository.existsByPromotionCode(updatedData.getPromotionCode())) {
                throw new BusinessException("Mã khuyến mãi đã tồn tại");
            }
            existing.setPromotionCode(updatedData.getPromotionCode());
        }
        if (updatedData.getDiscountType() != null) {
            existing.setDiscountType(updatedData.getDiscountType());
        }
        if (updatedData.getDiscountValue() != null) {
            existing.setDiscountValue(updatedData.getDiscountValue());
        }
        if (updatedData.getStartDate() != null) {
            existing.setStartDate(updatedData.getStartDate());
        }
        if (updatedData.getEndDate() != null) {
            existing.setEndDate(updatedData.getEndDate());
        }
        if (updatedData.getApplicableTableTypes() != null) {
            existing.setApplicableTableTypes(updatedData.getApplicableTableTypes());
        }
        if (updatedData.getMinPlayTime() != null) {
            existing.setMinPlayTime(updatedData.getMinPlayTime());
        }
        if (updatedData.getMinAmount() != null) {
            existing.setMinAmount(updatedData.getMinAmount());
        }
        if (updatedData.getMaxDiscount() != null) {
            existing.setMaxDiscount(updatedData.getMaxDiscount());
        }
        if (updatedData.getUsageLimit() != null) {
            existing.setUsageLimit(updatedData.getUsageLimit());
        }
        if (updatedData.getIsActive() != null) {
            existing.setIsActive(updatedData.getIsActive());
        }
        if (updatedData.getDescription() != null) {
            existing.setDescription(updatedData.getDescription());
        }

        validatePromotion(existing, false);
        return promotionRepository.save(existing);
    }

    @Transactional
    public void deletePromotion(Integer id) {
        Promotion promotion = getPromotionById(id);

        // Soft delete: just deactivate
        promotion.setIsActive(false);
        promotionRepository.save(promotion);

        // Or hard delete:
        // promotionRepository.delete(promotion);
    }

    @Transactional
    public Promotion applyPromotion(String code, Integer clubId) {
        Promotion promotion = promotionRepository
                .findValidPromotionByCodeAndClub(code, clubId, Instant.now())
                .orElseThrow(() -> new BusinessException("Mã khuyến mãi không hợp lệ hoặc đã hết hạn"));

        if (!promotion.canBeUsed()) {
            throw new BusinessException("Khuyến mãi đã hết lượt sử dụng");
        }

        promotion.incrementUsageCount();
        return promotionRepository.save(promotion);
    }

    private void validatePromotion(Promotion promotion, boolean isNew) {
        // Validate club exists
        if (promotion.getClub() == null || promotion.getClub().getId() == null) {
            throw new BusinessException("Club ID không được để trống");
        }
        if (!clubRepository.existsById(promotion.getClub().getId())) {
            throw new BusinessException("Không tìm thấy club với ID: " + promotion.getClub().getId());
        }

        // Validate customer if provided
        if (promotion.getCustomer() != null && promotion.getCustomer().getId() != null) {
            if (!customerRepository.existsById(promotion.getCustomer().getId())) {
                throw new BusinessException("Không tìm thấy khách hàng với ID: " + promotion.getCustomer().getId());
            }
        }

        // Validate dates
        if (promotion.getStartDate() != null && promotion.getEndDate() != null) {
            if (!promotion.getEndDate().isAfter(promotion.getStartDate())) {
                throw new BusinessException("Ngày kết thúc phải sau ngày bắt đầu");
            }
        }

        // Validate discount value
        if (promotion.getDiscountType() != null && promotion.getDiscountValue() != null) {
            if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
                if (promotion.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0 ||
                        promotion.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
                    throw new BusinessException("Giá trị giảm giá phần trăm phải từ 0-100");
                }
            } else {
                if (promotion.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BusinessException("Giá trị giảm giá phải lớn hơn 0");
                }
            }
        }

        // Validate promotion code uniqueness
        if (isNew && promotionRepository.existsByPromotionCode(promotion.getPromotionCode())) {
            throw new BusinessException("Mã khuyến mãi đã tồn tại");
        }

        // Validate usage counts
        if (promotion.getUsedCount() != null && promotion.getUsageLimit() != null) {
            if (promotion.getUsageLimit() > 0 && promotion.getUsedCount() > promotion.getUsageLimit()) {
                throw new BusinessException("Số lần đã sử dụng không được vượt quá giới hạn");
            }
        }
    }

    public Page<Promotion> getAllPromotions(Pageable pageable) {
        return promotionRepository.findAll(pageable);
    }
}