package com.gatedcommunity.backend.service;

import com.gatedcommunity.backend.dto.InvoiceDTO;
import com.gatedcommunity.backend.entity.Invoice;
import com.gatedcommunity.backend.entity.Property;
import com.gatedcommunity.backend.model.User;
import com.gatedcommunity.backend.repository.InvoiceRepository;
import com.gatedcommunity.backend.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final EmailService emailService;
    private final PropertyRepository propertyRepository;  // ✅ added

    public InvoiceService(InvoiceRepository invoiceRepository,
                          EmailService emailService,
                          PropertyRepository propertyRepository) {
        this.invoiceRepository = invoiceRepository;
        this.emailService = emailService;
        this.propertyRepository = propertyRepository;
    }

    public List<InvoiceDTO.InvoiceResponse> getInvoicesForAdmin() {
        return invoiceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<InvoiceDTO.InvoiceResponse> getInvoicesForResident(Long residentId) {
        return invoiceRepository.findByPropertyOwnerIdOrPropertyTenantId(residentId, residentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ✅ new — create single invoice
    public InvoiceDTO.InvoiceResponse createInvoice(InvoiceDTO.InvoiceRequest request) {
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        Invoice invoice = Invoice.builder()
                .property(property)
                .amount(request.getAmount())
                .lateFee(BigDecimal.ZERO)
                .dueDate(request.getDueDate())
                .status("PENDING")
                .monthYear(request.getMonthYear())
                .build();

        Invoice saved = invoiceRepository.save(invoice);

        // ✅ send email to tenant if exists, else owner
        User notifyUser = property.getTenant() != null ? property.getTenant() : property.getOwner();
        if (notifyUser != null) {
            emailService.sendNewInvoiceNotification(
                    notifyUser,
                    request.getAmount(),
                    request.getDueDate(),
                    request.getMonthYear()
            );
        }

        return mapToDTO(saved);
    }

    // Manual trigger — creates invoices for all currently assigned properties
    public int generateMonthlyInvoices(BigDecimal amount, java.time.LocalDate dueDate, String monthYear) {
        List<Property> properties = propertyRepository.findAll();
        int count = 0;

        for (Property property : properties) {
            if (property.getOwner() == null && property.getTenant() == null) continue; // skip unassigned properties

            // Skip if an invoice already exists for this property + monthYear
            boolean alreadyGenerated = invoiceRepository
                    .findByPropertyId(property.getId())
                    .stream()
                    .anyMatch(i -> monthYear.equals(i.getMonthYear()));

            if (alreadyGenerated) continue;

            Invoice invoice = Invoice.builder()
                    .property(property)
                    .amount(amount)
                    .lateFee(BigDecimal.ZERO)
                    .dueDate(dueDate)
                    .status("PENDING")
                    .monthYear(monthYear)
                    .build();

            invoiceRepository.save(invoice);

            // Notify resident via email
            User notifyUser = property.getTenant() != null ? property.getTenant() : property.getOwner();
            if (notifyUser != null) {
                emailService.sendNewInvoiceNotification(
                        notifyUser,
                        amount,
                        dueDate,
                        monthYear
                );
            }

            count++;
        }

        System.out.println("✅ Manual batch: Generated " + count + " invoices for " + monthYear);
        return count;
    }

    private InvoiceDTO.InvoiceResponse mapToDTO(Invoice invoice) {
        return InvoiceDTO.InvoiceResponse.builder()
                .invoiceId(invoice.getId())
                .propertyId(invoice.getProperty().getId())
                .block(invoice.getProperty().getBlock())
                .unitNumber(invoice.getProperty().getUnitNumber())
                .totalAmount(invoice.getAmount().add(invoice.getLateFee()))
                .lateFee(invoice.getLateFee())
                .dueDate(invoice.getDueDate())
                .status(invoice.getStatus())
                .monthYear(invoice.getMonthYear())
                .build();
    }

    public void deleteInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        if ("PAID".equals(invoice.getStatus())) {
            throw new RuntimeException("Cannot delete paid invoices");
        }
        
        invoiceRepository.deleteById(invoiceId);
    }
}