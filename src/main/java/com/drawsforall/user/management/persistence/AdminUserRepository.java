package com.drawsforall.user.management.persistence;

import com.drawsforall.user.management.persistence.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findByUsernameIgnoreCase(String username);
    Optional<AdminUser> findByUsernameAndAndPassword(String username, String password);
}

