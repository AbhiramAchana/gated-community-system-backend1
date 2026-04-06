package com.gatedcommunity.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.gatedcommunity.backend.model.User;  // ✅ add this line

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User recipient;

    @Column(nullable = false, length = 20)
    private String type; // EMAIL, SMS, WHATSAPP

    @Column(nullable = false, length = 255)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String messageBody;

    @Column(nullable = false, length = 20)
    private String status; // SENT, FAILED, PENDING

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt;

    private String errorMessage;
}
