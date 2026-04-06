package com.gatedcommunity.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PropertyDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PropertyRequest {
        private String block;
        private String unitNumber;
        private Long ownerId; // optional — can assign later
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PropertyResponse {
        private Long id;
        private String block;
        private String unitNumber;
        private Long ownerId;
        private String ownerName;
        private String ownerEmail;
        private Long tenantId;
        private String tenantName;
        private String tenantEmail;
    }
}