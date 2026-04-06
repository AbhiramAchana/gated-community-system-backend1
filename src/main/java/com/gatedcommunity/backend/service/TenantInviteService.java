package com.gatedcommunity.backend.service;

import com.gatedcommunity.backend.entity.Property;
import com.gatedcommunity.backend.entity.TenantInvite;
import com.gatedcommunity.backend.repository.PropertyRepository;
import com.gatedcommunity.backend.repository.TenantInviteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantInviteService {

    private final TenantInviteRepository tenantInviteRepository;
    private final PropertyRepository propertyRepository;
    private final EmailService emailService;

    public TenantInvite generateInvite(Long ownerId, Long propertyId, String invitedName, String invitedEmail, LocalDate leaseExpiryDate) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (property.getOwner() == null || !property.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Only the property owner can generate tenant invites");
        }

        String code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        TenantInvite invite = TenantInvite.builder()
                .property(property)
                .inviteCode(code)
                .invitedName(invitedName)
                .invitedEmail(invitedEmail)
                .leaseExpiryDate(leaseExpiryDate)
                .used(false)
                .build();

        TenantInvite saved = tenantInviteRepository.save(invite);
        
        // Send email notification to tenant
        try {
            String registrationLink = "http://localhost:5173/register-tenant?token=" + code;
            String emailBody = String.format(
                "Hello %s,\n\n" +
                "You have been invited to join as a tenant for property %s-%s.\n\n" +
                "Your invite code is: %s\n\n" +
                "Click the link below to register:\n%s\n\n" +
                "Lease expiry date: %s\n\n" +
                "Best regards,\nGated Community Management",
                invitedName,
                property.getBlock(),
                property.getUnitNumber(),
                code,
                registrationLink,
                leaseExpiryDate
            );
            
            emailService.sendEmail(invitedEmail, "Tenant Invitation - Register Now", emailBody);
        } catch (Exception e) {
            // Log error but don't fail the invite generation
            System.err.println("Failed to send email: " + e.getMessage());
        }

        return saved;
    }
}
