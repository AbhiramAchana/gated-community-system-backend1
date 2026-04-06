package com.gatedcommunity.backend.repository;

import com.gatedcommunity.backend.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    List<Visitor> findByResidentIdOrderByCreatedAtDesc(Long residentId);
    List<Visitor> findAllByOrderByCreatedAtDesc();
    Optional<Visitor> findByEntryToken(String entryToken);
}