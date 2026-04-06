package com.gatedcommunity.backend.repository;

import com.gatedcommunity.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdOrderBySentAtDesc(Long recipientId);
}
