package com.gatedcommunity.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ComplaintDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComplaintRequest {
        private String category;
        private String subject;
        private String description;
        private String priority;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminUpdateRequest {
        private String status;
        private String adminResponse;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ComplaintResponse {
        private Long id;
        private String category;
        private String subject;
        private String description;
        private String status;
        private String priority;
        private String adminResponse;
        private String residentName;
        private String residentEmail;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime resolvedAt;
    }
}