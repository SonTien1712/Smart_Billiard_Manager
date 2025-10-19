package com.BillardManagement.Repository;

import com.BillardManagement.Entity.Billiardtable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import com.BillardManagement.DTO.Response.BilliardTableResponse;


@Repository
public interface BilliardTableRepo extends JpaRepository<Billiardtable, Integer> {
    @Query("""
        SELECT new com.BillardManagement.DTO.Response.BilliardTableResponse(
            t.id,
            t.tableName,
            t.tableType,
            t.hourlyRate,
            t.tableStatus,
            t.clubID.id,
            t.clubID.clubName
        )
        FROM Billiardtable t
        WHERE t.clubID.customerID = :customerID
    """)
    List<BilliardTableResponse> findTablesWithClubByCustomerId(@Param("customerID") Integer customerID);
}
