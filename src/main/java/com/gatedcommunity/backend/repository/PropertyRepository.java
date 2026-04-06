package com.gatedcommunity.backend.repository;

import com.gatedcommunity.backend.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    // No custom query methods needed - use findAll() and filter in service layer
}