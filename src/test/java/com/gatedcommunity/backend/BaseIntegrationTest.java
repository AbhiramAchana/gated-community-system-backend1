package com.gatedcommunity.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gatedcommunity.backend.model.User;
import com.gatedcommunity.backend.repository.UserRepository;
import com.gatedcommunity.backend.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for integration tests with common setup
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtUtils jwtUtils;

    protected User testAdmin;
    protected User testOwner;
    protected User testTenant;
    
    protected String adminToken;
    protected String ownerToken;
    protected String tenantToken;

    @BeforeEach
    public void baseSetup() {
        // Clean up before each test
        userRepository.deleteAll();
        
        // Create test users
        createTestUsers();
        
        // Generate tokens
        generateTokens();
    }

    private void createTestUsers() {
        // Admin user
        testAdmin = new User();
        testAdmin.setEmail("admin@test.com");
        testAdmin.setPassword(passwordEncoder.encode("Admin@123"));
        testAdmin.setFirstName("Test");
        testAdmin.setLastName("Admin");
        testAdmin.setRole(User.Role.ADMIN);
        testAdmin.setPhoneNumber("1234567890");
        testAdmin = userRepository.save(testAdmin);

        // Owner user
        testOwner = new User();
        testOwner.setEmail("owner@test.com");
        testOwner.setPassword(passwordEncoder.encode("Owner@123"));
        testOwner.setFirstName("Test");
        testOwner.setLastName("Owner");
        testOwner.setRole(User.Role.OWNER);
        testOwner.setPhoneNumber("1234567891");
        testOwner = userRepository.save(testOwner);

        // Tenant user
        testTenant = new User();
        testTenant.setEmail("tenant@test.com");
        testTenant.setPassword(passwordEncoder.encode("Tenant@123"));
        testTenant.setFirstName("Test");
        testTenant.setLastName("Tenant");
        testTenant.setRole(User.Role.TENANT);
        testTenant.setPhoneNumber("1234567892");
        testTenant = userRepository.save(testTenant);
    }

    private void generateTokens() {
        adminToken = jwtUtils.generateToken(testAdmin.getEmail());
        ownerToken = jwtUtils.generateToken(testOwner.getEmail());
        tenantToken = jwtUtils.generateToken(testTenant.getEmail());
    }

    protected String asJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
