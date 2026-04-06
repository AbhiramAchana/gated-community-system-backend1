package com.gatedcommunity.backend.service;

import com.gatedcommunity.backend.dto.AnnouncementDTO;
import com.gatedcommunity.backend.entity.Announcement;
import com.gatedcommunity.backend.model.User;
import com.gatedcommunity.backend.repository.AnnouncementRepository;
import com.gatedcommunity.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    public AnnouncementDTO.AnnouncementResponse create(Long adminId, AnnouncementDTO.AnnouncementRequest request) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        Announcement announcement = Announcement.builder()
                .createdBy(admin)
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType() != null ? request.getType() : "GENERAL")
                .priority(request.getPriority() != null ? request.getPriority() : "MEDIUM")
                .active(true)
                .expiresAt(request.getExpiresAt())
                .build();

        return mapToDTO(announcementRepository.save(announcement));
    }

    public List<AnnouncementDTO.AnnouncementResponse> getActive() {
        return announcementRepository.findByActiveTrueOrderByCreatedAtDesc()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<AnnouncementDTO.AnnouncementResponse> getAll() {
        return announcementRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public AnnouncementDTO.AnnouncementResponse deactivate(Long id) {
        Announcement a = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
        a.setActive(false);
        return mapToDTO(announcementRepository.save(a));
    }

    private AnnouncementDTO.AnnouncementResponse mapToDTO(Announcement a) {
        return AnnouncementDTO.AnnouncementResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .content(a.getContent())
                .type(a.getType())
                .priority(a.getPriority())
                .active(a.isActive())
                .createdByName(a.getCreatedBy().getName())
                .createdAt(a.getCreatedAt())
                .expiresAt(a.getExpiresAt())
                .build();
    }
}