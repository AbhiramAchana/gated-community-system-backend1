package com.gatedcommunity.backend.repository;

import com.gatedcommunity.backend.entity.TenantInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantInviteRepository extends JpaRepository<TenantInvite, Long> {
    Optional<TenantInvite> findByInviteCode(String inviteCode);
}
