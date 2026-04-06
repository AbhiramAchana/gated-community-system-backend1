package com.gatedcommunity.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FacilityDTO {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FacilityRequest {
        private String name;
        private String description;
        private Integer capacity;
        private String openTime;
        private String closeTime;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FacilityResponse {
        private Long id;
        private String name;
        private String description;
        private Integer capacity;
        private String openTime;
        private String closeTime;
        private Boolean isActive;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class BookingRequest {
        private Long facilityId;
        private LocalDate bookingDate;
        private String startTime;
        private String endTime;
        private String notes;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class BookingResponse {
        private Long id;
        private Long facilityId;
        private String facilityName;
        private Long residentId;
        private String residentName;
        private String residentPhone;
        private LocalDate bookingDate;
        private String startTime;
        private String endTime;
        private String status;
        private String notes;
        private LocalDateTime createdAt;
    }
}
