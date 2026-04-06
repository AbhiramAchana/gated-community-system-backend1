package com.gatedcommunity.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "facilities", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g., "Swimming Pool", "Gym", "Clubhouse"

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer capacity; // max concurrent bookings

    private String openTime;  // e.g., "06:00"
    private String closeTime; // e.g., "22:00"

    @Builder.Default
    private Boolean isActive = true;
}
