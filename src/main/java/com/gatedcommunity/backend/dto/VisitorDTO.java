package com.gatedcommunity.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class VisitorDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VisitorRequest {
        private String visitorName;
        private String visitorPhone;
        private String visitorVehicle;
        private String purpose;
        private LocalDateTime expectedArrival;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VisitorResponse {
        private Long id;
        private String visitorName;
        private String visitorPhone;
        private String visitorVehicle;
        private String purpose;
        private String entryToken;
        private String status;
        private String residentName;
        private String residentEmail;
        private LocalDateTime expectedArrival;
        private LocalDateTime entryTime;
        private LocalDateTime exitTime;
        private LocalDateTime createdAt;
        private String securityNotes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GateActionRequest {
        private String entryToken;
        private String action;  // ENTRY or EXIT
        private String securityNotes;
    }
}