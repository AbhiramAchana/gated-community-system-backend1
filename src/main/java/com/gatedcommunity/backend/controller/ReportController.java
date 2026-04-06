package com.gatedcommunity.backend.controller;

import com.gatedcommunity.backend.dto.ReportDTO;
import com.gatedcommunity.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/admin/financial")
    public ResponseEntity<ReportDTO.FinancialReport> getFinancialReport(
            @RequestParam(defaultValue = "0") int year) {
        if (year == 0) year = LocalDate.now().getYear();
        return ResponseEntity.ok(reportService.getFinancialReport(year));
    }
}
