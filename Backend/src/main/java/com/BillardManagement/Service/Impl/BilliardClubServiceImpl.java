package com.BillardManagement.Service.Impl;

import com.BillardManagement.Entity.Billardclub;
import com.BillardManagement.Repository.BilliardClubRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BilliardClubServiceImpl {
//    @Autowired
//    private BilliardClubRepo repo;
//
//    // Lấy tất cả
//    public List<Billardclub> getAllClubs() {
//        return repo.findAll();
//    }
//
//    // Lấy theo ID
//    public Optional<Billardclub> getClubById(Integer id) {
//        return repo.findById(id);
//    }
//
//    // Tạo mới
//    public Billardclub createClub(Billardclub club) {
//        return repo.save(club);
//    }
//
//    // Cập nhật
//    public Billardclub updateClub(Integer id, Billardclub updated) {
//        Billardclub existing = repo.findById(id)
//                .orElseThrow(() -> new RuntimeException("Club not found"));
//        existing.setClubName(updated.getClubName());
//        existing.setAddress(updated.getAddress());
//        existing.setPhoneNumber(updated.getPhoneNumber());
//        return repo.save(existing);
//    }
//
//    // Xóa
//    public void deleteClub(Integer id) {
//        repo.deleteById(id);
//    }
}