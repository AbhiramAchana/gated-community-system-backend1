package com.gatedcommunity.backend.controller;

import com.gatedcommunity.backend.model.User;
import com.gatedcommunity.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // ✅ Get all pending users
    @GetMapping("/admin/pending")
    public ResponseEntity<List<User>> getPendingUsers() {
        return ResponseEntity.ok(
                userRepository.findAll().stream()
                        .filter(u -> "PENDING".equals(u.getStatus()))
                        .toList()
        );
    }

    // ✅ Get all users
    @GetMapping("/admin/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // ✅ Approve user
    @PutMapping("/admin/{userId}/approve")
    public ResponseEntity<String> approveUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus("APPROVED");
        userRepository.save(user);
        return ResponseEntity.ok("User approved successfully");
    }

    // ✅ Reject user
    @PutMapping("/admin/{userId}/reject")
    public ResponseEntity<String> rejectUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus("REJECTED");
        userRepository.save(user);
        return ResponseEntity.ok("User rejected");
    }
}