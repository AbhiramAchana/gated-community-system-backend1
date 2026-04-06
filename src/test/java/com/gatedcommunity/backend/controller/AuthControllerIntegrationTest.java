package com.gatedcommunity.backend.controller;

import com.gatedcommunity.backend.BaseIntegrationTest;
import com.gatedcommunity.backend.dto.AuthDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Authentication Integration Tests")
class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Should register new user successfully")
    void testRegisterUser() throws Exception {
        AuthDTO.RegisterRequest request = new AuthDTO.RegisterRequest();
        request.setEmail("newuser@test.com");
        request.setPassword("Password@123");
        request.setFirstName("New");
        request.setLastName("User");
        request.setPhoneNumber("9876543210");
        request.setRole("OWNER");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@test.com"))
                .andExpect(jsonPath("$.role").value("OWNER"));
    }

    @Test
    @DisplayName("Should login with valid credentials")
    void testLoginSuccess() throws Exception {
        AuthDTO.LoginRequest request = new AuthDTO.LoginRequest();
        request.setEmail("admin@test.com");
        request.setPassword("Admin@123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("admin@test.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("Should fail login with invalid credentials")
    void testLoginFailure() throws Exception {
        AuthDTO.LoginRequest request = new AuthDTO.LoginRequest();
        request.setEmail("admin@test.com");
        request.setPassword("WrongPassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject duplicate email registration")
    void testDuplicateEmailRegistration() throws Exception {
        AuthDTO.RegisterRequest request = new AuthDTO.RegisterRequest();
        request.setEmail("admin@test.com"); // Already exists
        request.setPassword("Password@123");
        request.setFirstName("Duplicate");
        request.setLastName("User");
        request.setPhoneNumber("9876543210");
        request.setRole("OWNER");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should validate email format")
    void testInvalidEmailFormat() throws Exception {
        AuthDTO.RegisterRequest request = new AuthDTO.RegisterRequest();
        request.setEmail("invalid-email");
        request.setPassword("Password@123");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPhoneNumber("9876543210");
        request.setRole("OWNER");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }
}
