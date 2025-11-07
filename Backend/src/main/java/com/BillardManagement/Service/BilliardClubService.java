package com.BillardManagement.Service;

import com.BillardManagement.Entity.Billardclub;
import java.util.List;
import java.util.Optional;

public interface BilliardClubService {

    List<Billardclub> getAllClubs();

    Optional<Billardclub> getClubById(Integer id);


    List<Billardclub> getClubsByCustomerId(Integer customerId);

    Billardclub createClub(Billardclub club);

    Billardclub updateClub(Integer id, Billardclub updated);

    void deleteClub(Integer id);
}