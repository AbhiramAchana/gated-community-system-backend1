package com.gatedcommunity.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class ReportDTO {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MonthlyReport {
        private String monthYear;
        private BigDecimal totalCollected;
        private BigDecimal totalPending;
        private BigDecimal totalOverdue;
        private long paidCount;
        private long pendingCount;
        private long overdueCount;
        private double collectionRate; // percentage
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FinancialReport {
        private int year;
        private BigDecimal yearTotalCollected;
        private BigDecimal yearTotalPending;
        private List<MonthlyReport> months;
    }
}
