package com.BillardManagement.Controller;

import com.BillardManagement.Entity.Billardclub;
import com.BillardManagement.Service.BilliardClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000") // Cho phép frontend React truy cập
@RestController
@RequestMapping("/api/customer/clubs")
public class BilliardClubController {

    @Autowired
    private BilliardClubService clubService;

    // ✅ Lấy tất cả các club
    @GetMapping
    public List<Billardclub> getAllClubs() {
        return clubService.getAllClubs();
    }

    // ✅ Lấy club theo ID
    @GetMapping("/{id}")
    public Optional<Billardclub> getClubById(@PathVariable Integer id) {
        return clubService.getClubById(id);
    }

    // ✅ Tạo mới club
    @PostMapping
    public Billardclub createClub(@RequestBody Billardclub club) {
        return clubService.createClub(club);
    }

    // ✅ Cập nhật club
    @PutMapping("/{id}")
    public Billardclub updateClub(@PathVariable Integer id, @RequestBody Billardclub updatedClub) {
        return clubService.updateClub(id, updatedClub);
    }

    // ✅ Xóa club
    @DeleteMapping("/{id}")
    public void deleteClub(@PathVariable Integer id) {
        clubService.deleteClub(id);
    }
}
