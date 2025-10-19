package com.BillardManagement.Service.Impl;

import com.BillardManagement.DTO.Request.BilliardTableRequest;
import com.BillardManagement.DTO.Response.BilliardTableResponse;
import com.BillardManagement.Entity.Billardclub;
import com.BillardManagement.Entity.Billiardtable;
import com.BillardManagement.Repository.BilliardClubRepo;
import com.BillardManagement.Repository.BilliardTableRepo;
import com.BillardManagement.Service.BilliardTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BilliardTableServiceImpl implements BilliardTableService {

    @Autowired
    private BilliardTableRepo tableRepository;

    @Autowired
    private BilliardClubRepo clubRepository;

    @Override
    public List<BilliardTableResponse> getTablesByCustomer(Integer customerId) {
        return tableRepository.findTablesWithClubByCustomerId(customerId);
    }

    @Override
    public List<BilliardTableResponse> getAllTables() {
        List<Billiardtable> tables = tableRepository.findAll();
        List<BilliardTableResponse> result = new ArrayList<>();

        for (Billiardtable t : tables) {
            BilliardTableResponse dto = new BilliardTableResponse();
            dto.setId(t.getId());
            dto.setTableName(t.getTableName());
            dto.setTableType(t.getTableType());
            dto.setHourlyRate(t.getHourlyRate());
            dto.setTableStatus(t.getTableStatus());
            dto.setClubId(t.getClubID().getId());
            dto.setClubName(t.getClubID().getClubName());
            result.add(dto);
        }
        return result;
    }

    @Override
    public BilliardTableResponse getTableById(Integer id) {
        Billiardtable t = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        BilliardTableResponse dto = new BilliardTableResponse();
        dto.setId(t.getId());
        dto.setTableName(t.getTableName());
        dto.setTableType(t.getTableType());
        dto.setHourlyRate(t.getHourlyRate());
        dto.setTableStatus(t.getTableStatus());
        dto.setClubId(t.getClubID().getId());
        dto.setClubName(t.getClubID().getClubName());
        return dto;
    }

    @Override
    public BilliardTableResponse addTable(BilliardTableRequest request) {
        Billardclub club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new RuntimeException("Club not found"));

        Billiardtable t = new Billiardtable();
        t.setTableName(request.getTableName());
        t.setTableType(request.getTableType());
        t.setHourlyRate(request.getHourlyRate());
        t.setTableStatus(request.getTableStatus());
        t.setClubID(club);

        Billiardtable saved = tableRepository.save(t);

        BilliardTableResponse dto = new BilliardTableResponse();
        dto.setId(saved.getId());
        dto.setTableName(saved.getTableName());
        dto.setTableType(saved.getTableType());
        dto.setHourlyRate(saved.getHourlyRate());
        dto.setTableStatus(saved.getTableStatus());
        dto.setClubId(club.getId());
        dto.setClubName(club.getClubName());
        return dto;
    }

    @Override
    public BilliardTableResponse updateTable(Integer id, BilliardTableRequest request) {
        Billiardtable t = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        Billardclub club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new RuntimeException("Club not found"));

        t.setTableName(request.getTableName());
        t.setTableType(request.getTableType());
        t.setHourlyRate(request.getHourlyRate());
        t.setTableStatus(request.getTableStatus());
        t.setClubID(club);

        Billiardtable updated = tableRepository.save(t);

        BilliardTableResponse dto = new BilliardTableResponse();
        dto.setId(updated.getId());
        dto.setTableName(updated.getTableName());
        dto.setTableType(updated.getTableType());
        dto.setHourlyRate(updated.getHourlyRate());
        dto.setTableStatus(updated.getTableStatus());
        dto.setClubId(club.getId());
        dto.setClubName(club.getClubName());
        return dto;
    }

    @Override
    public void deleteTable(Integer id) {
        if (!tableRepository.existsById(id)) {
            throw new RuntimeException("Table not found");
        }
        tableRepository.deleteById(id);
    }
}
