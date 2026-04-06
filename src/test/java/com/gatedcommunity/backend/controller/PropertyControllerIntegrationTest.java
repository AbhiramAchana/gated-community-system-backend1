package com.gatedcommunity.backend.controller;

import com.gatedcommunity.backend.BaseIntegrationTest;
import com.gatedcommunity.backend.dto.PropertyDTO;
import com.gatedcommunity.backend.entity.Property;
import com.gatedcommunity.backend.repository.PropertyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Property Management Integration Tests")
class PropertyControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PropertyRepository propertyRepository;

    @Test
    @DisplayName("Admin should create property successfully")
    void testCreateProperty() throws Exception {
        PropertyDTO.CreateRequest request = new PropertyDTO.CreateRequest();
        request.setPropertyNumber("A-101");
        request.setPropertyType("APARTMENT");
        request.setArea(1200.0);
        request.setBedrooms(3);
        request.setBathrooms(2);

        mockMvc.perform(post("/api/properties")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.propertyNumber").value("A-101"))
                .andExpect(jsonPath("$.propertyType").value("APARTMENT"));
    }

    @Test
    @DisplayName("Admin should assign owner to property")
    void testAssignOwner() throws Exception {
        // Create property first
        Property property = new Property();
        property.setPropertyNumber("B-202");
        property.setPropertyType(Property.PropertyType.APARTMENT);
        property.setArea(1500.0);
        property = propertyRepository.save(property);

        mockMvc.perform(put("/api/properties/" + property.getId() + "/assign-owner/" + testOwner.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerId").value(testOwner.getId()));
    }

    @Test
    @DisplayName("Owner should view their properties")
    void testOwnerViewProperties() throws Exception {
        // Create and assign property
        Property property = new Property();
        property.setPropertyNumber("C-303");
        property.setPropertyType(Property.PropertyType.VILLA);
        property.setOwner(testOwner);
        propertyRepository.save(property);

        mockMvc.perform(get("/api/properties/my-properties")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].propertyNumber").value("C-303"));
    }

    @Test
    @DisplayName("Should toggle owner-occupied status")
    void testToggleOwnerOccupied() throws Exception {
        Property property = new Property();
        property.setPropertyNumber("D-404");
        property.setPropertyType(Property.PropertyType.APARTMENT);
        property.setOwner(testOwner);
        property.setOwnerOccupied(false);
        property = propertyRepository.save(property);

        mockMvc.perform(put("/api/properties/" + property.getId() + "/toggle-owner-occupied")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerOccupied").value(true));
    }

    @Test
    @DisplayName("Non-owner should not access other's properties")
    void testUnauthorizedPropertyAccess() throws Exception {
        Property property = new Property();
        property.setPropertyNumber("E-505");
        property.setPropertyType(Property.PropertyType.APARTMENT);
        property.setOwner(testOwner);
        property = propertyRepository.save(property);

        mockMvc.perform(put("/api/properties/" + property.getId() + "/toggle-owner-occupied")
                .header("Authorization", "Bearer " + tenantToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should list all properties for admin")
    void testAdminListAllProperties() throws Exception {
        // Create multiple properties
        Property p1 = new Property();
        p1.setPropertyNumber("F-601");
        p1.setPropertyType(Property.PropertyType.APARTMENT);
        propertyRepository.save(p1);

        Property p2 = new Property();
        p2.setPropertyNumber("F-602");
        p2.setPropertyType(Property.PropertyType.VILLA);
        propertyRepository.save(p2);

        mockMvc.perform(get("/api/properties")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
