package com.gatedcommunity.backend.controller;

import com.gatedcommunity.backend.dto.ComplaintDTO;
import com.gatedcommunity.backend.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping("/resident/{residentId}")
    public ResponseEntity<ComplaintDTO.ComplaintResponse> raiseComplaint(
            @PathVariable Long residentId,
            @RequestBody ComplaintDTO.ComplaintRequest request) {
        return ResponseEntity.ok(complaintService.raiseComplaint(residentId, request));
    }

    @PutMapping("/admin/{complaintId}")
    public ResponseEntity<ComplaintDTO.ComplaintResponse> updateComplaint(
            @PathVariable Long complaintId,
            @RequestBody ComplaintDTO.AdminUpdateRequest request) {
        return ResponseEntity.ok(complaintService.updateComplaint(complaintId, request));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<ComplaintDTO.ComplaintResponse>> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<ComplaintDTO.ComplaintResponse>> getMyComplaints(
            @PathVariable Long residentId) {
        return ResponseEntity.ok(complaintService.getMyComplaints(residentId));
    }
}