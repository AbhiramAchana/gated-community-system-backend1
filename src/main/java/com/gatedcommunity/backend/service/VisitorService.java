package com.gatedcommunity.backend.service;

import com.gatedcommunity.backend.dto.VisitorDTO;
import com.gatedcommunity.backend.entity.Visitor;
import com.gatedcommunity.backend.model.User;
import com.gatedcommunity.backend.repository.UserRepository;
import com.gatedcommunity.backend.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final UserRepository userRepository;

    // ✅ Resident pre-approves a visitor
    public VisitorDTO.VisitorResponse preApproveVisitor(Long residentId, VisitorDTO.VisitorRequest request) {
        User resident = userRepository.findById(residentId)
                .orElseThrow(() -> new RuntimeException("Resident not found"));

        String token = generateToken();

        Visitor visitor = Visitor.builder()
                .resident(resident)
                .visitorName(request.getVisitorName())
                .visitorPhone(request.getVisitorPhone())
                .visitorVehicle(request.getVisitorVehicle())
                .purpose(request.getPurpose())
                .expectedArrival(request.getExpectedArrival())
                .entryToken(token)
                .status("APPROVED")
                .build();

        Visitor saved = visitorRepository.save(visitor);
        return mapToDTO(saved);
    }

    // ✅ Security logs entry or exit using token
    public VisitorDTO.VisitorResponse processGateAction(VisitorDTO.GateActionRequest request) {
        Visitor visitor = visitorRepository.findByEntryToken(request.getEntryToken())
                .orElseThrow(() -> new RuntimeException("Invalid entry token"));

        if ("ENTRY".equals(request.getAction())) {
            visitor.setStatus("ENTERED");
            visitor.setEntryTime(LocalDateTime.now());
        } else if ("EXIT".equals(request.getAction())) {
            visitor.setStatus("EXITED");
            visitor.setExitTime(LocalDateTime.now());
        }

        if (request.getSecurityNotes() != null) {
            visitor.setSecurityNotes(request.getSecurityNotes());
        }

        Visitor saved = visitorRepository.save(visitor);
        return mapToDTO(saved);
    }

    // ✅ Get all visitors for admin
    public List<VisitorDTO.VisitorResponse> getAllVisitors() {
        return visitorRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // ✅ Get visitors for a resident
    public List<VisitorDTO.VisitorResponse> getMyVisitors(Long residentId) {
        return visitorRepository.findByResidentIdOrderByCreatedAtDesc(residentId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // ✅ Lookup visitor by token (for security gate screen)
    public VisitorDTO.VisitorResponse getByToken(String token) {
        Visitor visitor = visitorRepository.findByEntryToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        return mapToDTO(visitor);
    }

    private String generateToken() {
        // 6-digit numeric token
        return String.format("%06d", new Random().nextInt(999999));
    }

    private VisitorDTO.VisitorResponse mapToDTO(Visitor v) {
        return VisitorDTO.VisitorResponse.builder()
                .id(v.getId())
                .visitorName(v.getVisitorName())
                .visitorPhone(v.getVisitorPhone())
                .visitorVehicle(v.getVisitorVehicle())
                .purpose(v.getPurpose())
                .entryToken(v.getEntryToken())
                .status(v.getStatus())
                .residentName(v.getResident().getName())
                .residentEmail(v.getResident().getEmail())
                .expectedArrival(v.getExpectedArrival())
                .entryTime(v.getEntryTime())
                .exitTime(v.getExitTime())
                .createdAt(v.getCreatedAt())
                .securityNotes(v.getSecurityNotes())
                .build();
    }
}