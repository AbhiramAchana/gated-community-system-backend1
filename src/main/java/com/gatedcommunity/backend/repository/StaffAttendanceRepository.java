package com.gatedcommunity.backend.repository;

import com.gatedcommunity.backend.entity.StaffAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StaffAttendanceRepository extends JpaRepository<StaffAttendance, Long> {
    List<StaffAttendance> findByDate(LocalDate date);
    List<StaffAttendance> findByStaffId(Long staffId);
    List<StaffAttendance> findByDateBetween(LocalDate from, LocalDate to);
    Optional<StaffAttendance> findByStaffIdAndDate(Long staffId, LocalDate date);
}
