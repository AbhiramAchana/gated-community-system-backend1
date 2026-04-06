package com.gatedcommunity.backend.entity;

import com.gatedcommunity.backend.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitors", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ resident who pre-approved
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private User resident;

    @Column(nullable = false)
    private String visitorName;

    @Column(nullable = false)
    private String visitorPhone;

    private String visitorVehicle; // optional

    @Column(nullable = false)
    private String purpose; // DELIVERY, GUEST, SERVICE, etc.

    @Column(nullable = false, unique = true)
    private String entryToken; // 6-digit code for gate

    @Column(nullable = false)
    private String status; // PENDING, APPROVED, ENTERED, EXITED, DENIED

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime expectedArrival;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    private String securityNotes;
}