package com.gatedcommunity.backend.service;

import com.gatedcommunity.backend.dto.PaymentDTO;
import com.gatedcommunity.backend.entity.Invoice;
import com.gatedcommunity.backend.repository.InvoiceRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Formatter;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final InvoiceRepository invoiceRepository;
    private final RazorpayClient razorpayClient;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    public PaymentDTO.OrderResponse createOrder(PaymentDTO.PaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if ("PAID".equals(invoice.getStatus())) {
            throw new RuntimeException("Invoice already paid");
        }

        try {
            // Razorpay amount is in paise (1 INR = 100 paise)
            long amountInPaise = invoice.getAmount()
                    .add(invoice.getLateFee())
                    .multiply(new BigDecimal("100"))
                    .longValue();

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "INV-" + invoice.getId());
            orderRequest.put("payment_capture", 1);

            Order order = razorpayClient.orders.create(orderRequest);

            // Use tenant if present, otherwise fall back to owner
            var resident = invoice.getProperty().getTenant() != null
                    ? invoice.getProperty().getTenant()
                    : invoice.getProperty().getOwner();

            return PaymentDTO.OrderResponse.builder()
                    .orderId(order.get("id"))
                    .amount(invoice.getAmount().add(invoice.getLateFee()))
                    .currency("INR")
                    .keyId(keyId)
                    .customerName(resident != null ? resident.getName() : "")
                    .customerEmail(resident != null ? resident.getEmail() : "")
                    .customerPhone(resident != null ? resident.getPhone() : "")
                    .build();

        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    public PaymentDTO.PaymentResponse verifyPayment(PaymentDTO.PaymentVerifyRequest request) {
        try {
            // ✅ Verify signature
            String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
            String generatedSignature = hmacSha256(payload, keySecret);

            if (!generatedSignature.equals(request.getRazorpaySignature())) {
                return PaymentDTO.PaymentResponse.builder()
                        .success(false)
                        .message("Payment verification failed — invalid signature")
                        .build();
            }

            // ✅ Mark invoice as PAID
            Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));

            invoice.setStatus("PAID");
            invoice.setPaidAt(LocalDateTime.now());
            invoiceRepository.save(invoice);

            // ✅ Send email receipt
            var paidResident = invoice.getProperty().getTenant() != null
                    ? invoice.getProperty().getTenant()
                    : invoice.getProperty().getOwner();
            if (paidResident != null) {
                emailService.sendPaymentReceipt(
                        paidResident,
                        invoice.getAmount().add(invoice.getLateFee()),
                        invoice.getMonthYear()
                );
            }

            // ✅ Broadcast to admin via WebSockets
            if (paidResident != null) {
                messagingTemplate.convertAndSend("/topic/admin/payments", 
                    "Payment of ₹" + invoice.getAmount().add(invoice.getLateFee()) + 
                    " received from " + paidResident.getName());
            }

            return PaymentDTO.PaymentResponse.builder()
                    .success(true)
                    .message("Payment successful!")
                    .build();

        } catch (Exception e) {
            return PaymentDTO.PaymentResponse.builder()
                    .success(false)
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }

    // ✅ HMAC-SHA256 signature verification
    private String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}