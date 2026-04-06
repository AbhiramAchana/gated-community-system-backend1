package com.gatedcommunity.backend.service;

import com.gatedcommunity.backend.dto.AuthDTO;
import com.gatedcommunity.backend.model.User;
import com.gatedcommunity.backend.repository.UserRepository;
import com.gatedcommunity.backend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gatedcommunity.backend.entity.TenantInvite;
import com.gatedcommunity.backend.entity.Property;
import com.gatedcommunity.backend.repository.TenantInviteRepository;
import com.gatedcommunity.backend.repository.PropertyRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final TenantInviteRepository tenantInviteRepository;
    private final PropertyRepository propertyRepository;

    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        String status = "ADMIN".equals(request.getRole()) ? "APPROVED" : "PENDING";

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : "RESIDENT")
                .status(status)
                .phone(request.getPhone())  // ✅ fixed — correct builder syntax
                .build();

        userRepository.save(user);

        if ("PENDING".equals(status)) {
            return new AuthDTO.AuthResponse(null, null, user.getName(), user.getEmail(), user.getId());
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        String token = jwtUtils.generateJwtToken(authToken);

        return new AuthDTO.AuthResponse(token, user.getRole(), user.getName(), user.getEmail(), user.getId());
    }

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtils.generateJwtToken(authentication);

        return new AuthDTO.AuthResponse(
                token,
                user.getRole(),
                user.getName(),
                user.getEmail(),
                user.getId()
        );
    }

    public AuthDTO.AuthResponse registerTenant(AuthDTO.RegisterRequest request, String inviteCode) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        TenantInvite invite = tenantInviteRepository.findByInviteCode(inviteCode)
            .orElseThrow(() -> new RuntimeException("Invalid invite code"));

        if (invite.isUsed()) {
            throw new RuntimeException("Invite code already used");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("RESIDENT")
                .status("APPROVED")
                .phone(request.getPhone())
                .build();

        user = userRepository.save(user);

        Property property = invite.getProperty();
        property.setTenant(user);
        propertyRepository.save(property);

        invite.setUsed(true);
        tenantInviteRepository.save(invite);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        String jwtToken = jwtUtils.generateJwtToken(authToken);

        return new AuthDTO.AuthResponse(jwtToken, user.getRole(), user.getName(), user.getEmail(), user.getId());
    }
}