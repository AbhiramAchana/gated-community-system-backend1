package com.gatedcommunity.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.gatedcommunity.backend.model.User;  // ✅ add this line
@Entity
@Table(name = "properties", schema = "public",
        uniqueConstraints = @UniqueConstraint(columnNames = {"block", "unit_number"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String block; // e.g., "A", "B", "Villa"

    @Column(name = "unit_number", nullable = false)
    private String unitNumber; // e.g., "101", "Villa-4"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id")
    private User tenant;
}
