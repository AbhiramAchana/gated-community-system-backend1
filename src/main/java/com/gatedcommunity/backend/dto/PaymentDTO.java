package com.gatedcommunity.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class PaymentDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentRequest {
        private Long invoiceId;
        private BigDecimal amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderResponse {
        private String orderId;       // Razorpay order_id
        private BigDecimal amount;    // in INR
        private String currency;
        private String keyId;         // send to frontend
        private String customerName;
        private String customerEmail;
        private String customerPhone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentVerifyRequest {
        private String razorpayOrderId;
        private String razorpayPaymentId;
        private String razorpaySignature;
        private Long invoiceId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentResponse {
        private boolean success;
        private String message;
    }
}