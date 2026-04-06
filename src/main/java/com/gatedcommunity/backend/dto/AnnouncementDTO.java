package com.gatedcommunity.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AnnouncementDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnouncementRequest {
        private String title;
        private String content;
        private String type;
        private String priority;
        private LocalDateTime expiresAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnnouncementResponse {
        private Long id;
        private String title;
        private String content;
        private String type;
        private String priority;
        private boolean active;
        private String createdByName;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
    }
}