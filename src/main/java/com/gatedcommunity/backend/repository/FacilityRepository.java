package com.gatedcommunity.backend.repository;

import com.gatedcommunity.backend.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
    List<Facility> findByIsActiveTrue();
}
