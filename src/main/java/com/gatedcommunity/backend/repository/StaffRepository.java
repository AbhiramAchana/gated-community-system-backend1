package com.gatedcommunity.backend.repository;

import com.gatedcommunity.backend.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByStatus(String status);
    List<Staff> findByRole(String role);
}
