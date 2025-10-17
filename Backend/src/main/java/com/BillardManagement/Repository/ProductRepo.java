package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {
    List<Product> findByIsActiveTrueOrderByProductNameAsc();
    List<Product> findByClubID_IdAndIsActiveTrueOrderByProductNameAsc(Integer clubId);
    List<Product> findByCustomerID_IdAndIsActiveTrueOrderByProductNameAsc(Integer customerId);
}

