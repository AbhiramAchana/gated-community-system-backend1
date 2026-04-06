package com.gatedcommunity.backend.controller;

import com.gatedcommunity.backend.dto.InvoiceDTO;
import com.gatedcommunity.backend.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/admin/all")
    public ResponseEntity<List<InvoiceDTO.InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getInvoicesForAdmin());
    }

    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<InvoiceDTO.InvoiceResponse>> getMyInvoices(@PathVariable Long residentId) {
        return ResponseEntity.ok(invoiceService.getInvoicesForResident(residentId));
    }

    @PostMapping("/admin/trigger-billing")
    public ResponseEntity<String> forceGenerateBills(@RequestBody InvoiceDTO.BatchBillingRequest request) {
        int count = invoiceService.generateMonthlyInvoices(
                request.getAmount(), request.getDueDate(), request.getMonthYear());
        return ResponseEntity.ok("Generated " + count + " invoice(s) for " + request.getMonthYear() + ".");
    }

    @PostMapping("/admin/create")
    public ResponseEntity<InvoiceDTO.InvoiceResponse> createInvoice(
            @RequestBody InvoiceDTO.InvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.createInvoice(request));
    }

    @DeleteMapping("/admin/{invoiceId}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long invoiceId) {
        invoiceService.deleteInvoice(invoiceId);
        return ResponseEntity.ok().build();
    }
}