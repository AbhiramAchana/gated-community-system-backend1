package com.gatedcommunity.backend.service;

import com.gatedcommunity.backend.dto.StaffDTO;
import com.gatedcommunity.backend.entity.Staff;
import com.gatedcommunity.backend.entity.StaffAttendance;
import com.gatedcommunity.backend.repository.StaffAttendanceRepository;
import com.gatedcommunity.backend.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final StaffAttendanceRepository attendanceRepository;

    // ── Staff CRUD ──

    public StaffDTO.StaffResponse addStaff(StaffDTO.StaffRequest req) {
        Staff s = Staff.builder()
                .name(req.getName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .role(req.getRole())
                .vendorCompany(req.getVendorCompany())
                .joiningDate(req.getJoiningDate())
                .notes(req.getNotes())
                .build();
        return mapStaff(staffRepository.save(s));
    }

    public List<StaffDTO.StaffResponse> getAllStaff() {
        return staffRepository.findAll().stream().map(this::mapStaff).collect(Collectors.toList());
    }

    public StaffDTO.StaffResponse deactivateStaff(Long id) {
        Staff s = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        s.setStatus("INACTIVE");
        return mapStaff(staffRepository.save(s));
    }

    public StaffDTO.StaffResponse activateStaff(Long id) {
        Staff s = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        s.setStatus("ACTIVE");
        return mapStaff(staffRepository.save(s));
    }

    // ── Attendance ──

    public StaffDTO.AttendanceResponse markAttendance(StaffDTO.AttendanceRequest req) {
        Staff staff = staffRepository.findById(req.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // upsert — update if already marked for that day
        StaffAttendance attendance = attendanceRepository
                .findByStaffIdAndDate(req.getStaffId(), req.getDate())
                .orElse(StaffAttendance.builder().staff(staff).date(req.getDate()).build());

        attendance.setStatus(req.getStatus());
        attendance.setNotes(req.getNotes());
        return mapAttendance(attendanceRepository.save(attendance));
    }

    public List<StaffDTO.AttendanceResponse> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date).stream()
                .map(this::mapAttendance).collect(Collectors.toList());
    }

    public List<StaffDTO.AttendanceResponse> getAttendanceByMonth(int year, int month) {
        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());
        return attendanceRepository.findByDateBetween(from, to).stream()
                .map(this::mapAttendance).collect(Collectors.toList());
    }

    public List<StaffDTO.AttendanceResponse> getAttendanceByStaff(Long staffId) {
        return attendanceRepository.findByStaffId(staffId).stream()
                .map(this::mapAttendance).collect(Collectors.toList());
    }

    // ── Mappers ──

    private StaffDTO.StaffResponse mapStaff(Staff s) {
        return StaffDTO.StaffResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .phone(s.getPhone())
                .email(s.getEmail())
                .role(s.getRole())
                .vendorCompany(s.getVendorCompany())
                .status(s.getStatus())
                .joiningDate(s.getJoiningDate())
                .notes(s.getNotes())
                .build();
    }

    private StaffDTO.AttendanceResponse mapAttendance(StaffAttendance a) {
        return StaffDTO.AttendanceResponse.builder()
                .id(a.getId())
                .staffId(a.getStaff().getId())
                .staffName(a.getStaff().getName())
                .staffRole(a.getStaff().getRole())
                .date(a.getDate())
                .status(a.getStatus())
                .notes(a.getNotes())
                .build();
    }
}
