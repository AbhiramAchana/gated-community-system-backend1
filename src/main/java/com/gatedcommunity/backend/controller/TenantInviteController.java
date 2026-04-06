package com.gatedcommunity.backend.controller;

import com.gatedcommunity.backend.entity.TenantInvite;
import com.gatedcommunity.backend.service.TenantInviteService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/properties/resident")
@RequiredArgsConstructor
public class TenantInviteController {

    private final TenantInviteService tenantInviteService;

    @Data
    public static class InviteRequest {
        private String invitedName;
        private String invitedEmail;
        private LocalDate leaseExpiryDate;
    }

    @PostMapping("/{ownerId}/property/{propertyId}/invite-tenant")
    public ResponseEntity<?> inviteTenant(
            @PathVariable Long ownerId,
            @PathVariable Long propertyId,
            @RequestBody InviteRequest request) {
        try {
            TenantInvite invite = tenantInviteService.generateInvite(
                ownerId, 
                propertyId, 
                request.getInvitedName(), 
                request.getInvitedEmail(),
                request.getLeaseExpiryDate()
            );
            return ResponseEntity.ok(java.util.Map.of(
                "inviteCode", invite.getInviteCode(),
                "message", "Invitation sent to " + request.getInvitedEmail()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }
}
