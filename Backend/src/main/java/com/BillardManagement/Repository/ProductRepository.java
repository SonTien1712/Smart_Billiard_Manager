package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByClubIdAndIsActiveTrue(Integer clubId);

    List<Product> findByClubId(Integer clubId);

    List<Product> findByCustomerId(Integer customerId);

    List<Product> findByClubIdAndCategory(Integer clubId, String category);

    @Query("SELECT p FROM Product p WHERE p.club.id = :clubId AND p.isActive = true AND " +
            "(LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Product> searchProducts(@Param("clubId") Integer clubId, @Param("keyword") String keyword);

    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.club.id = :clubId")
    Optional<Product> findByIdAndClubId(@Param("id") Integer id, @Param("clubId") Integer clubId);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.club.id = :clubId AND p.isActive = true")
    Long countActiveProductsByClubId(@Param("clubId") Integer clubId);
}
