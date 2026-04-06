package com.gatedcommunity.backend.controller;

import com.gatedcommunity.backend.dto.FacilityDTO;
import com.gatedcommunity.backend.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    // ── Admin ──

    @PostMapping("/admin/create")
    public ResponseEntity<FacilityDTO.FacilityResponse> createFacility(
            @RequestBody FacilityDTO.FacilityRequest request) {
        return ResponseEntity.ok(facilityService.createFacility(request));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<FacilityDTO.FacilityResponse>> getAllFacilities() {
        return ResponseEntity.ok(facilityService.getAllFacilities());
    }

    @PutMapping("/admin/{id}/toggle")
    public ResponseEntity<FacilityDTO.FacilityResponse> toggleFacility(@PathVariable Long id) {
        return ResponseEntity.ok(facilityService.toggleFacility(id));
    }

    @GetMapping("/admin/bookings")
    public ResponseEntity<List<FacilityDTO.BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(facilityService.getAllBookings());
    }

    @PutMapping("/admin/bookings/{bookingId}/status")
    public ResponseEntity<FacilityDTO.BookingResponse> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam String status) {
        return ResponseEntity.ok(facilityService.updateBookingStatus(bookingId, status));
    }

    @DeleteMapping("/admin/bookings/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        facilityService.deleteBooking(id);
        return ResponseEntity.ok().build();
    }

    // ── Resident ──

    @GetMapping("/active")
    public ResponseEntity<List<FacilityDTO.FacilityResponse>> getActiveFacilities() {
        return ResponseEntity.ok(facilityService.getActiveFacilities());
    }

    @PostMapping("/resident/{residentId}/book")
    public ResponseEntity<?> createBooking(
            @PathVariable Long residentId,
            @RequestBody FacilityDTO.BookingRequest request) {
        try {
            return ResponseEntity.ok(facilityService.createBooking(residentId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/resident/{residentId}/bookings")
    public ResponseEntity<List<FacilityDTO.BookingResponse>> getMyBookings(
            @PathVariable Long residentId) {
        return ResponseEntity.ok(facilityService.getMyBookings(residentId));
    }

    @PutMapping("/resident/bookings/{bookingId}/cancel")
    public ResponseEntity<FacilityDTO.BookingResponse> cancelBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(facilityService.updateBookingStatus(bookingId, "CANCELLED"));
    }
}
