package com.gatedcommunity.backend.controller;

import com.gatedcommunity.backend.dto.PropertyDTO;
import com.gatedcommunity.backend.model.User;
import com.gatedcommunity.backend.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping("/admin/create")
    public ResponseEntity<PropertyDTO.PropertyResponse> createProperty(
            @RequestBody PropertyDTO.PropertyRequest request) {
        return ResponseEntity.ok(propertyService.createProperty(request));
    }

    @PutMapping("/admin/{propertyId}/assign-owner/{ownerId}")
    public ResponseEntity<PropertyDTO.PropertyResponse> assignOwner(
            @PathVariable Long propertyId,
            @PathVariable Long ownerId) {
        return ResponseEntity.ok(propertyService.assignOwner(propertyId, ownerId));
    }

    @PutMapping("/admin/{propertyId}/assign-tenant/{tenantId}")
    public ResponseEntity<PropertyDTO.PropertyResponse> assignTenant(
            @PathVariable Long propertyId,
            @PathVariable Long tenantId) {
        return ResponseEntity.ok(propertyService.assignTenant(propertyId, tenantId));
    }

    @PutMapping("/admin/{propertyId}/unassign-owner")
    public ResponseEntity<PropertyDTO.PropertyResponse> unassignOwner(
            @PathVariable Long propertyId) {
        return ResponseEntity.ok(propertyService.unassignOwner(propertyId));
    }

    @PutMapping("/admin/{propertyId}/unassign-tenant")
    public ResponseEntity<PropertyDTO.PropertyResponse> unassignTenant(
            @PathVariable Long propertyId) {
        return ResponseEntity.ok(propertyService.unassignTenant(propertyId));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<PropertyDTO.PropertyResponse>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    @GetMapping("/resident/{residentId}")
    public ResponseEntity<List<PropertyDTO.PropertyResponse>> getMyProperties(@PathVariable Long residentId) {
        return ResponseEntity.ok(propertyService.getMyProperties(residentId));
    }

    @GetMapping("/admin/residents")
    public ResponseEntity<List<User>> getAllResidents() {
        return ResponseEntity.ok(propertyService.getAllResidents());
    }

    @DeleteMapping("/admin/{propertyId}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long propertyId) {
        propertyService.deleteProperty(propertyId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/residents/{residentId}")
    public ResponseEntity<Void> deactivateResident(@PathVariable Long residentId) {
        propertyService.deactivateResident(residentId);
        return ResponseEntity.ok().build();
    }
}