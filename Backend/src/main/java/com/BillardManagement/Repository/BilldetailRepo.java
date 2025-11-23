package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Billdetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface BilldetailRepo extends JpaRepository<Billdetail, Integer> {
    List<Billdetail> findByBillID_Id(Integer billId);
    void deleteByBillID_Id(Integer billId);
    java.util.Optional<Billdetail> findByBillID_IdAndProductID_Id(Integer billId, Integer productId);

    // Eagerly load product for viewing purposes
    @Query("select d from Billdetail d left join fetch d.productID where d.billID.id = :billId")
    List<Billdetail> findWithProductByBill(@Param("billId") Integer billId);


    List<Billdetail> findByClubID_IdAndCustomerID_Id(Integer clubId, Integer customerId);
}
