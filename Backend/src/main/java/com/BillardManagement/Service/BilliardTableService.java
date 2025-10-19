package com.BillardManagement.Service;

import com.BillardManagement.DTO.Request.BilliardTableRequest;
import com.BillardManagement.DTO.Response.BilliardTableResponse;

import java.util.List;

public interface BilliardTableService {
    List<BilliardTableResponse> getAllTables();
    List<BilliardTableResponse> getTablesByCustomer(Integer customerId);
    BilliardTableResponse getTableById(Integer id);
    BilliardTableResponse addTable(BilliardTableRequest request);
    BilliardTableResponse updateTable(Integer id, BilliardTableRequest request);
    void deleteTable(Integer id);
}