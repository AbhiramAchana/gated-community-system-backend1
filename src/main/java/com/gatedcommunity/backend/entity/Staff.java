package com.gatedcommunity.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "staff", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phone;
    private String email;

    @Column(nullable = false)
    private String role; // SECURITY, CLEANER, ELECTRICIAN, PLUMBER, GARDENER, OTHER

    private String vendorCompany; // optional — third-party vendor

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    private LocalDate joiningDate;

    private String notes;
}
