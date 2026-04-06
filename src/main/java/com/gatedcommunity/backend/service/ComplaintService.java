package com.gatedcommunity.backend.service;

import com.gatedcommunity.backend.dto.ComplaintDTO;
import com.gatedcommunity.backend.entity.Complaint;
import com.gatedcommunity.backend.model.User;
import com.gatedcommunity.backend.repository.ComplaintRepository;
import com.gatedcommunity.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    // ✅ Resident raises complaint
    public ComplaintDTO.ComplaintResponse raiseComplaint(Long residentId, ComplaintDTO.ComplaintRequest request) {
        User resident = userRepository.findById(residentId)
                .orElseThrow(() -> new RuntimeException("Resident not found"));

        Complaint complaint = Complaint.builder()
                .resident(resident)
                .category(request.getCategory())
                .subject(request.getSubject())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : "MEDIUM")
                .status("OPEN")
                .build();

        Complaint saved = complaintRepository.save(complaint);
        ComplaintDTO.ComplaintResponse dto = mapToDTO(saved);
        
        // Broadcast to admins
        messagingTemplate.convertAndSend("/topic/admin/complaints", dto);
        
        return dto;
    }

    // ✅ Admin updates complaint status
    public ComplaintDTO.ComplaintResponse updateComplaint(Long complaintId, ComplaintDTO.AdminUpdateRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setStatus(request.getStatus());
        complaint.setAdminResponse(request.getAdminResponse());

        if ("RESOLVED".equals(request.getStatus()) || "CLOSED".equals(request.getStatus())) {
            complaint.setResolvedAt(LocalDateTime.now());
        }

        Complaint saved = complaintRepository.save(complaint);

        // ✅ notify resident via email
        if (request.getAdminResponse() != null && !request.getAdminResponse().isEmpty()) {
            emailService.sendComplaintUpdate(
                    complaint.getResident(),
                    complaint.getSubject(),
                    request.getStatus(),
                    request.getAdminResponse()
            );
        }

        ComplaintDTO.ComplaintResponse dto = mapToDTO(saved);
        
        // Broadcast to specific resident
        messagingTemplate.convertAndSend("/topic/resident/" + complaint.getResident().getId() + "/complaints", dto);

        return dto;
    }

    // ✅ Get all complaints for admin
    public List<ComplaintDTO.ComplaintResponse> getAllComplaints() {
        return complaintRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // ✅ Get complaints for resident
    public List<ComplaintDTO.ComplaintResponse> getMyComplaints(Long residentId) {
        return complaintRepository.findByResidentIdOrderByCreatedAtDesc(residentId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private ComplaintDTO.ComplaintResponse mapToDTO(Complaint c) {
        return ComplaintDTO.ComplaintResponse.builder()
                .id(c.getId())
                .category(c.getCategory())
                .subject(c.getSubject())
                .description(c.getDescription())
                .status(c.getStatus())
                .priority(c.getPriority())
                .adminResponse(c.getAdminResponse())
                .residentName(c.getResident().getName())
                .residentEmail(c.getResident().getEmail())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .resolvedAt(c.getResolvedAt())
                .build();
    }
}