package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

    // Tìm theo promotion code
    Optional<Promotion> findByPromotionCode(String promotionCode);

    // Kiểm tra promotion code đã tồn tại chưa
    boolean existsByPromotionCode(String promotionCode);

    // Lấy tất cả promotion của một club
    Page<Promotion> findByClubId(Integer clubId, Pageable pageable);

    // Lấy promotion đang active của club
    @Query("SELECT p FROM Promotion p WHERE p.club.id = :clubId " +
            "AND p.isActive = true " +
            "AND p.startDate <= :now " +
            "AND p.endDate >= :now")
    List<Promotion> findActivePromotionsByClub(
            @Param("clubId") Integer clubId,
            @Param("now") Instant now
    );

    // Lấy promotion có thể sử dụng (active + còn lượt)
    @Query("SELECT p FROM Promotion p WHERE p.club.id = :clubId " +
            "AND p.isActive = true " +
            "AND p.startDate <= :now " +
            "AND p.endDate >= :now " +
            "AND (p.usageLimit = 0 OR p.usedCount < p.usageLimit)")
    List<Promotion> findUsablePromotionsByClub(
            @Param("clubId") Integer clubId,
            @Param("now") Instant now
    );

    // Tìm promotion theo code và club (để apply)
    @Query("SELECT p FROM Promotion p WHERE p.promotionCode = :code " +
            "AND p.club.id = :clubId " +
            "AND p.isActive = true " +
            "AND p.startDate <= :now " +
            "AND p.endDate >= :now " +
            "AND (p.usageLimit = 0 OR p.usedCount < p.usageLimit)")
    Optional<Promotion> findValidPromotionByCodeAndClub(
            @Param("code") String code,
            @Param("clubId") Integer clubId,
            @Param("now") Instant now
    );

    // Lấy promotion của customer cụ thể
    List<Promotion> findByCustomerId(Integer customerId);

    // Xóa promotion hết hạn
    @Query("SELECT p FROM Promotion p WHERE p.endDate < :now")
    List<Promotion> findExpiredPromotions(@Param("now") Instant now);

    // Count promotion của club
    long countByClubId(Integer clubId);
}