package com.BillardManagement.Service.Impl;

import com.BillardManagement.DTO.Request.BilliardTableRequest;
import com.BillardManagement.DTO.Response.BilliardTableResponse;
import com.BillardManagement.Entity.Billardclub;
import com.BillardManagement.Entity.Billiardtable;
import com.BillardManagement.Repository.BillardclubRepo;
import com.BillardManagement.Repository.BilliardTableRepo;
import com.BillardManagement.Service.BilliardTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BilliardTableServiceImpl implements BilliardTableService {

    private final BilliardTableRepo tableRepository;
    private final BillardclubRepo clubRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BilliardTableResponse> getAllTables() {
        return tableRepository.findAllTablesWithClub();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BilliardTableResponse> getTablesByCustomer(Integer customerId) {
        return tableRepository.findTablesWithClubByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public BilliardTableResponse getTableById(Integer id) {
        Billiardtable t = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));

        return new BilliardTableResponse(
                t.getId(),
                t.getTableName(),
                t.getTableType(),
                t.getHourlyRate(),
                t.getTableStatus(),
                t.getClubID().getId(),
                t.getClubID().getClubName()
        );
    }

    @Override
    public BilliardTableResponse addTable(BilliardTableRequest request) {
        Billardclub club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + request.getClubId()));

        Billiardtable table = new Billiardtable();
        table.setTableName(request.getTableName());
        table.setTableType(request.getTableType());
        table.setHourlyRate(request.getHourlyRate());
        table.setTableStatus(request.getTableStatus() != null ? request.getTableStatus() : "Available");
        table.setClubID(club);

        Billiardtable saved = tableRepository.save(table);

        return new BilliardTableResponse(
                saved.getId(),
                saved.getTableName(),
                saved.getTableType(),
                saved.getHourlyRate(),
                saved.getTableStatus(),
                club.getId(),
                club.getClubName()
        );
    }

    @Override
    public BilliardTableResponse updateTable(Integer id, BilliardTableRequest request) {
        Billiardtable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));

        Billardclub club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + request.getClubId()));

        table.setTableName(request.getTableName());
        table.setTableType(request.getTableType());
        table.setHourlyRate(request.getHourlyRate());
        table.setTableStatus(request.getTableStatus());
        table.setClubID(club);

        Billiardtable updated = tableRepository.save(table);

        return new BilliardTableResponse(
                updated.getId(),
                updated.getTableName(),
                updated.getTableType(),
                updated.getHourlyRate(),
                updated.getTableStatus(),
                club.getId(),
                club.getClubName()
        );
    }

    @Override
    public void deleteTable(Integer id) {
        if (!tableRepository.existsById(id)) {
            throw new RuntimeException("Table not found with id: " + id);
        }
        tableRepository.deleteById(id);
    }
}