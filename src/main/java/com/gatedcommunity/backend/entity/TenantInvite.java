package com.gatedcommunity.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "tenant_invites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(nullable = false, unique = true)
    private String inviteCode;

    @Column(nullable = false)
    private LocalDate leaseExpiryDate;

    @Column(nullable = false)
    private String invitedName;

    @Column(nullable = false)
    private String invitedEmail;

    @Builder.Default
    private boolean used = false;
}
