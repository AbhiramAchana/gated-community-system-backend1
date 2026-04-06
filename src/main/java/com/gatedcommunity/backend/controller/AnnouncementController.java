package com.gatedcommunity.backend.controller;

import com.gatedcommunity.backend.dto.AnnouncementDTO;
import com.gatedcommunity.backend.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping("/admin/{adminId}")
    public ResponseEntity<AnnouncementDTO.AnnouncementResponse> create(
            @PathVariable Long adminId,
            @RequestBody AnnouncementDTO.AnnouncementRequest request) {
        return ResponseEntity.ok(announcementService.create(adminId, request));
    }

    @GetMapping("/active")
    public ResponseEntity<List<AnnouncementDTO.AnnouncementResponse>> getActive() {
        return ResponseEntity.ok(announcementService.getActive());
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<AnnouncementDTO.AnnouncementResponse>> getAll() {
        return ResponseEntity.ok(announcementService.getAll());
    }

    @PutMapping("/admin/{id}/deactivate")
    public ResponseEntity<AnnouncementDTO.AnnouncementResponse> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(announcementService.deactivate(id));
    }
}