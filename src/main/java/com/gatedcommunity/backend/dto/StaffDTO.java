package com.gatedcommunity.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class StaffDTO {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StaffRequest {
        private String name;
        private String phone;
        private String email;
        private String role;
        private String vendorCompany;
        private LocalDate joiningDate;
        private String notes;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StaffResponse {
        private Long id;
        private String name;
        private String phone;
        private String email;
        private String role;
        private String vendorCompany;
        private String status;
        private LocalDate joiningDate;
        private String notes;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AttendanceRequest {
        private Long staffId;
        private LocalDate date;
        private String status; // PRESENT, ABSENT, HALF_DAY, LEAVE
        private String notes;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AttendanceResponse {
        private Long id;
        private Long staffId;
        private String staffName;
        private String staffRole;
        private LocalDate date;
        private String status;
        private String notes;
    }
}
