package com.gatedcommunity.backend.controller;

import com.gatedcommunity.backend.dto.StaffDTO;
import com.gatedcommunity.backend.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    // ── Staff CRUD ──

    @PostMapping("/admin/add")
    public ResponseEntity<StaffDTO.StaffResponse> addStaff(@RequestBody StaffDTO.StaffRequest request) {
        return ResponseEntity.ok(staffService.addStaff(request));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<StaffDTO.StaffResponse>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @PutMapping("/admin/{id}/deactivate")
    public ResponseEntity<StaffDTO.StaffResponse> deactivateStaff(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.deactivateStaff(id));
    }

    @PutMapping("/admin/{id}/activate")
    public ResponseEntity<StaffDTO.StaffResponse> activateStaff(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.activateStaff(id));
    }

    // ── Attendance ──

    @PostMapping("/admin/attendance")
    public ResponseEntity<StaffDTO.AttendanceResponse> markAttendance(
            @RequestBody StaffDTO.AttendanceRequest request) {
        return ResponseEntity.ok(staffService.markAttendance(request));
    }

    @GetMapping("/admin/attendance/date")
    public ResponseEntity<List<StaffDTO.AttendanceResponse>> getAttendanceByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(staffService.getAttendanceByDate(date));
    }

    @GetMapping("/admin/attendance/month")
    public ResponseEntity<List<StaffDTO.AttendanceResponse>> getAttendanceByMonth(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(staffService.getAttendanceByMonth(year, month));
    }

    @GetMapping("/admin/attendance/staff/{staffId}")
    public ResponseEntity<List<StaffDTO.AttendanceResponse>> getAttendanceByStaff(
            @PathVariable Long staffId) {
        return ResponseEntity.ok(staffService.getAttendanceByStaff(staffId));
    }
}
