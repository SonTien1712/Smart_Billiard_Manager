package com.BillardManagement.Service.Impl;

import com.BillardManagement.Entity.Billardclub;
import com.BillardManagement.Repository.BilliardClubRepo;
import com.BillardManagement.Service.BilliardClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BilliardClubServiceImpl implements BilliardClubService {

    @Autowired
    private BilliardClubRepo repo;

    @Override
    public List<Billardclub> getAllClubs() {
        return repo.findAll();
    }

    @Override
    public Optional<Billardclub> getClubById(Integer id) {
        return repo.findById(id);
    }

    @Override
    public Billardclub createClub(Billardclub club) {
        // ✅ Khi tạo mới, nếu chưa set isActive thì mặc định là true
        if (club.getIsActive() == null) {
            club.setIsActive(true);
        }
        return repo.save(club);
    }

    @Override
    public Billardclub updateClub(Integer id, Billardclub updated) {
        Billardclub existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        existing.setClubName(updated.getClubName());
        existing.setAddress(updated.getAddress());
        existing.setPhoneNumber(updated.getPhoneNumber());

        // ✅ Cập nhật thêm trạng thái hoạt động
        if (updated.getIsActive() != null) {
            existing.setIsActive(updated.getIsActive());
        }

        return repo.save(existing);
    }

    @Override
    public void deleteClub(Integer id) {
        repo.deleteById(id);
    }

    // ✅ Thêm hàm bật/tắt trạng thái hoạt động
    public Billardclub toggleActive(Integer id) {
        Billardclub club = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found"));
        club.setIsActive(!club.getIsActive());
        return repo.save(club);
    }
    @Override
    public List<Billardclub> getActiveClubs() {
        return repo.findAll().stream()
                .filter(club -> club.getIsActive() != null && club.getIsActive())
                .toList(); // hoặc Collectors.toList() nếu dùng Java < 16
    }
}
