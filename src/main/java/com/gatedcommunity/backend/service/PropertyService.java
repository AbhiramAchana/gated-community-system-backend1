package com.gatedcommunity.backend.service;

import com.gatedcommunity.backend.dto.PropertyDTO;
import com.gatedcommunity.backend.entity.Property;
import com.gatedcommunity.backend.model.User;
import com.gatedcommunity.backend.repository.PropertyRepository;
import com.gatedcommunity.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public PropertyDTO.PropertyResponse createProperty(PropertyDTO.PropertyRequest request) {
        User owner = null;
        if (request.getOwnerId() != null) {
            owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new RuntimeException("Owner not found"));
        }

        Property property = Property.builder()
                .block(request.getBlock())
                .unitNumber(request.getUnitNumber())
                .owner(owner)
                .build();

        Property saved = propertyRepository.save(property);
        return mapToDTO(saved);
    }

    public PropertyDTO.PropertyResponse assignOwner(Long propertyId, Long ownerId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Resident not found"));

        property.setOwner(owner);
        Property saved = propertyRepository.save(property);
        return mapToDTO(saved);
    }
    
    public PropertyDTO.PropertyResponse assignTenant(Long propertyId, Long tenantId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        property.setTenant(tenant);
        Property saved = propertyRepository.save(property);
        return mapToDTO(saved);
    }

    public PropertyDTO.PropertyResponse unassignOwner(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        property.setOwner(null);
        Property saved = propertyRepository.save(property);
        return mapToDTO(saved);
    }
    
    public PropertyDTO.PropertyResponse unassignTenant(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        property.setTenant(null);
        Property saved = propertyRepository.save(property);
        return mapToDTO(saved);
    }

    public List<PropertyDTO.PropertyResponse> getAllProperties() {
        return propertyRepository.findAll().stream()
                .sorted((p1, p2) -> {
                    int blockCompare = p1.getBlock().compareTo(p2.getBlock());
                    if (blockCompare != 0) return blockCompare;
                    // Sort by unit number
                    try {
                        int unit1 = Integer.parseInt(p1.getUnitNumber());
                        int unit2 = Integer.parseInt(p2.getUnitNumber());
                        return Integer.compare(unit1, unit2);
                    } catch (NumberFormatException e) {
                        return p1.getUnitNumber().compareTo(p2.getUnitNumber());
                    }
                })
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PropertyDTO.PropertyResponse> getMyProperties(Long residentId) {
        return propertyRepository.findAll().stream()
                .filter(p -> (p.getOwner() != null && p.getOwner().getId().equals(residentId)) || 
                             (p.getTenant() != null && p.getTenant().getId().equals(residentId)))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<User> getAllResidents() {
        return userRepository.findAll().stream()
                .filter(u -> "RESIDENT".equals(u.getRole()))
                .filter(u -> !"DELETED".equals(u.getStatus()))
                .collect(Collectors.toList());
    }

    public void deactivateResident(Long residentId) {
        User resident = userRepository.findById(residentId)
                .orElseThrow(() -> new RuntimeException("Resident not found"));
        
        List<Property> propertiesAsOwner = propertyRepository.findAll().stream()
                .filter(p -> p.getOwner() != null && p.getOwner().getId().equals(residentId))
                .collect(Collectors.toList());
        
        for (Property p : propertiesAsOwner) {
            p.setOwner(null);
            propertyRepository.save(p);
        }

        List<Property> propertiesAsTenant = propertyRepository.findAll().stream()
                .filter(p -> p.getTenant() != null && p.getTenant().getId().equals(residentId))
                .collect(Collectors.toList());
        
        for (Property p : propertiesAsTenant) {
            p.setTenant(null);
            propertyRepository.save(p);
        }

        resident.setStatus("DELETED");
        userRepository.save(resident);
    }

    public void deleteProperty(Long propertyId) {
        try {
            propertyRepository.deleteById(propertyId);
        } catch (Exception e) {
            throw new RuntimeException("Cannot delete property. Ensure no invoices are tied to it.");
        }
    }

    private PropertyDTO.PropertyResponse mapToDTO(Property property) {
        PropertyDTO.PropertyResponse.PropertyResponseBuilder builder = PropertyDTO.PropertyResponse.builder()
                .id(property.getId())
                .block(property.getBlock())
                .unitNumber(property.getUnitNumber());

        if (property.getOwner() != null) {
            builder.ownerId(property.getOwner().getId())
                   .ownerName(property.getOwner().getName())
                   .ownerEmail(property.getOwner().getEmail());
        }
        
        if (property.getTenant() != null) {
            builder.tenantId(property.getTenant().getId())
                   .tenantName(property.getTenant().getName())
                   .tenantEmail(property.getTenant().getEmail());
        }

        return builder.build();
    }
}