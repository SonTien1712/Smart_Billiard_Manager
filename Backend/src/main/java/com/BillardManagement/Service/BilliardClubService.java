package com.BillardManagement.Service;

import com.BillardManagement.Entity.Billardclub;
import java.util.List;
import java.util.Optional;

public interface BilliardClubService {

    // Lấy tất cả câu lạc bộ
    List<Billardclub> getAllClubs();

    // Lấy theo ID
    Optional<Billardclub> getClubById(Integer id);

    List<Billardclub> getClubsByCustomerId(Integer customerId);

    // Tạo mới
    Billardclub createClub(Billardclub club);

    // Cập nhật
    Billardclub updateClub(Integer id, Billardclub updated);

    // Xóa (xóa cứng khỏi DB)
    void deleteClub(Integer id);


}
