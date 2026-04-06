package com.gatedcommunity.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InvoiceRequest {  // ✅ added
        private Long propertyId;
        private BigDecimal amount;
        private LocalDate dueDate;
        private String monthYear;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BatchBillingRequest {
        private BigDecimal amount;
        private LocalDate dueDate;
        private String monthYear;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InvoiceResponse {
        private Long invoiceId;
        private Long propertyId;
        private String block;
        private String unitNumber;
        private BigDecimal totalAmount;
        private BigDecimal lateFee;
        private LocalDate dueDate;
        private String status;
        private String monthYear;
    }
}