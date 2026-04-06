package com.gatedcommunity.backend.service;

import com.gatedcommunity.backend.dto.ReportDTO;
import com.gatedcommunity.backend.entity.Invoice;
import com.gatedcommunity.backend.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final InvoiceRepository invoiceRepository;

    public ReportDTO.FinancialReport getFinancialReport(int year) {
        List<Invoice> allInvoices = invoiceRepository.findAll();

        // Filter invoices for the requested year
        List<Invoice> yearInvoices = allInvoices.stream()
                .filter(i -> i.getMonthYear() != null && i.getMonthYear().endsWith(String.valueOf(year)))
                .collect(Collectors.toList());

        // Build month map — all 12 months
        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        List<ReportDTO.MonthlyReport> months = new ArrayList<>();
        for (String month : monthNames) {
            String key = month + " " + year;
            List<Invoice> monthInvoices = yearInvoices.stream()
                    .filter(i -> key.equals(i.getMonthYear()))
                    .collect(Collectors.toList());

            BigDecimal collected = monthInvoices.stream()
                    .filter(i -> "PAID".equals(i.getStatus()))
                    .map(i -> i.getAmount().add(i.getLateFee()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal pending = monthInvoices.stream()
                    .filter(i -> "PENDING".equals(i.getStatus()))
                    .map(i -> i.getAmount().add(i.getLateFee()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal overdue = monthInvoices.stream()
                    .filter(i -> "OVERDUE".equals(i.getStatus()))
                    .map(i -> i.getAmount().add(i.getLateFee()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long paidCount = monthInvoices.stream().filter(i -> "PAID".equals(i.getStatus())).count();
            long pendingCount = monthInvoices.stream().filter(i -> "PENDING".equals(i.getStatus())).count();
            long overdueCount = monthInvoices.stream().filter(i -> "OVERDUE".equals(i.getStatus())).count();

            double rate = monthInvoices.isEmpty() ? 0.0 :
                    Math.round((paidCount * 100.0 / monthInvoices.size()) * 10.0) / 10.0;

            months.add(ReportDTO.MonthlyReport.builder()
                    .monthYear(key)
                    .totalCollected(collected)
                    .totalPending(pending)
                    .totalOverdue(overdue)
                    .paidCount(paidCount)
                    .pendingCount(pendingCount)
                    .overdueCount(overdueCount)
                    .collectionRate(rate)
                    .build());
        }

        BigDecimal yearCollected = months.stream()
                .map(ReportDTO.MonthlyReport::getTotalCollected)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal yearPending = months.stream()
                .map(m -> m.getTotalPending().add(m.getTotalOverdue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReportDTO.FinancialReport.builder()
                .year(year)
                .yearTotalCollected(yearCollected)
                .yearTotalPending(yearPending)
                .months(months)
                .build();
    }
}
