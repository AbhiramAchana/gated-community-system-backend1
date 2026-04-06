package com.gatedcommunity.backend.controller;

import com.gatedcommunity.backend.dto.PaymentDTO;
import com.gatedcommunity.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<PaymentDTO.OrderResponse> createOrder(
            @RequestBody PaymentDTO.PaymentRequest request) {
        return ResponseEntity.ok(paymentService.createOrder(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentDTO.PaymentResponse> verifyPayment(
            @RequestBody PaymentDTO.PaymentVerifyRequest request) {
        return ResponseEntity.ok(paymentService.verifyPayment(request));
    }
}