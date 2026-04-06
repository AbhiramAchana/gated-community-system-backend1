package com.gatedcommunity.backend.controller;

import com.gatedcommunity.backend.dto.VisitorDTO;
import com.gatedcommunity.backend.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitors")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    // Resident pre-approves visitor
    @PostMapping("/resident/{residentId}/preapprove")
    public ResponseEntity<VisitorDTO.VisitorResponse> preApprove(
            @PathVariable Long residentId,
            @RequestBody VisitorDTO.VisitorRequest request) {
        return ResponseEntity.ok(visitorService.preApproveVisitor(residentId, request));
    }

    // Security gate action (entry/exit)
    @PostMapping("/gate/action")
    public ResponseEntity<VisitorDTO.VisitorResponse> gateAction(
            @RequestBody VisitorDTO.GateActionRequest request) {
        return ResponseEntity.ok(visitorService.processGateAction(request));
    }

    // Security/Admin — lookup by token
    @GetMapping("/gate/lookup/{token}")
    public ResponseEntity<VisitorDTO.VisitorResponse> lookupByToken(
            @PathVariable String token) {
        return ResponseEntity.ok(visitorService.getByToken(token));
    }

    // Admin — all visitors
    @GetMapping("/admin/all")
    public ResponseEntity<List<VisitorDTO.VisitorResponse>> getAllVisitors() {
        return ResponseEntity.ok(visitorService.getAllVisitors());
    }

    // Resident — my visitors
    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<VisitorDTO.VisitorResponse>> getMyVisitors(
            @PathVariable Long residentId) {
        return ResponseEntity.ok(visitorService.getMyVisitors(residentId));
    }
}