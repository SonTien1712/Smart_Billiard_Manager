package com.BillardManagement.Repository;
import com.BillardManagement.Entity.Passwordresettoken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordresettokenRepo extends JpaRepository<Passwordresettoken, Long> {
    Optional<Passwordresettoken> findByToken(String token);
    void deleteByToken(String token);
}