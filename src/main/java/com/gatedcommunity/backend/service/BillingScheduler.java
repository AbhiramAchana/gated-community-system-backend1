package com.gatedcommunity.backend.service;

import com.gatedcommunity.backend.entity.Invoice;
import com.gatedcommunity.backend.entity.Property;
import com.gatedcommunity.backend.repository.InvoiceRepository;
import com.gatedcommunity.backend.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingScheduler {

    private final InvoiceRepository invoiceRepository;
    private final PropertyRepository propertyRepository;
    private final EmailService emailService;

    // ✅ Runs at midnight on the 1st of every month
    @Scheduled(cron = "0 0 0 1 * *")
    public void generateMonthlyInvoices() {
        List<Property> properties = propertyRepository.findAll();
        String monthYear = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        LocalDate dueDate = LocalDate.now().withDayOfMonth(15); // due 15th of month

        for (Property property : properties) {
            // skip unassigned properties
            if (property.getOwner() == null && property.getTenant() == null) continue;

            // ✅ check no invoice already exists for this month
            boolean alreadyGenerated = invoiceRepository
                    .findByPropertyId(property.getId())
                    .stream()
                    .anyMatch(i -> monthYear.equals(i.getMonthYear()));

            if (alreadyGenerated) continue;

            Invoice invoice = Invoice.builder()
                    .property(property)
                    .amount(new BigDecimal("2500.00")) // default amount
                    .lateFee(BigDecimal.ZERO)
                    .dueDate(dueDate)
                    .status("PENDING")
                    .monthYear(monthYear)
                    .build();

            invoiceRepository.save(invoice);

            // notify tenant if present, else owner
            var notifyUser = property.getTenant() != null ? property.getTenant() : property.getOwner();
            emailService.sendNewInvoiceNotification(
                    notifyUser,
                    new BigDecimal("2500.00"),
                    dueDate,
                    monthYear
            );
        }

        System.out.println("✅ Monthly invoices generated for: " + monthYear);
    }

    // ✅ Mark overdue invoices — runs daily at 1am
    @Scheduled(cron = "0 0 1 * * *")
    public void markOverdueInvoices() {
        List<Invoice> pendingInvoices = invoiceRepository.findByStatus("PENDING");
        LocalDate today = LocalDate.now();

        for (Invoice invoice : pendingInvoices) {
            if (invoice.getDueDate() != null && invoice.getDueDate().isBefore(today)) {
                invoice.setStatus("OVERDUE");
                invoiceRepository.save(invoice);
            }
        }

        System.out.println("✅ Overdue invoices updated");
    }

    // ✅ Apply Late Fees — runs on the 7th of every month
    @Scheduled(cron = "0 0 1 7 * *")
    public void applyLateFees() {
        List<Invoice> pendingInvoices = invoiceRepository.findByStatus("OVERDUE");
        
        for (Invoice invoice : pendingInvoices) {
            // Apply $500 late fee if not already applied
            if (invoice.getLateFee().compareTo(new BigDecimal("500.00")) < 0) {
                invoice.setLateFee(new BigDecimal("500.00"));
                invoiceRepository.save(invoice);
            }
        }
        System.out.println("✅ Applied late fees to overdue invoices on the 7th.");
    }
}