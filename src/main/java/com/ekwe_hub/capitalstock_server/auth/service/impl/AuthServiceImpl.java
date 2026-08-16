package com.ekwe_hub.capitalstock_server.auth.service.impl;

import com.ekwe_hub.capitalstock_server.admin.repository.SystemAdminRepository;
import com.ekwe_hub.capitalstock_server.auth.dto.request.*;
import com.ekwe_hub.capitalstock_server.auth.dto.response.*;
import com.ekwe_hub.capitalstock_server.auth.model.User;
import com.ekwe_hub.capitalstock_server.auth.repository.UserRepository;
import com.ekwe_hub.capitalstock_server.auth.service.AuthService;
import com.ekwe_hub.capitalstock_server.common.events.AdminLoggedInEvent;
import com.ekwe_hub.capitalstock_server.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final SystemAdminRepository systemAdminRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(request.email());

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        if ("ROLE_SYSTEM_ADMIN".equals(role)) {
            var adminOpt = systemAdminRepository.findByEmail(request.email());
            adminOpt.ifPresent(admin -> eventPublisher.publishEvent(
                    new AdminLoggedInEvent(admin.getId(), admin.getEmail(), "127.0.0.1", LocalDateTime.now())
            ));
        }

        return new AuthResponse(accessToken, refreshToken, request.email(), role);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        if (!tokenProvider.validateToken(request.refreshToken())) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        String email = tokenProvider.getUsernameFromToken(request.refreshToken());
        String newAccessToken = tokenProvider.generateAccessToken(
                new UsernamePasswordAuthenticationToken(email, null, List.of())
        );
        String newRefreshToken = tokenProvider.generateRefreshToken(email);

        return new AuthResponse(newAccessToken, newRefreshToken, email, "ROLE_USER");
    }

    @Override
    public void logout(String email) {
        // Token invalidation hooks if token blacklisting cache is enabled
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(String email) {
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            List<String> roles = u.getRoles().stream().map(r -> r.getName().name()).toList();
            return new UserProfileResponse(u.getId(), u.getEmail(), u.getFullName(), u.getMerchantId(), roles, u.getStatus().name());
        }

        var adminOpt = systemAdminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            var admin = adminOpt.get();
            return new UserProfileResponse(admin.getId(), admin.getEmail(), admin.getFullName(), null, List.of("ROLE_SYSTEM_ADMIN"), admin.getStatus().name());
        }

        throw new IllegalArgumentException("User profile not found for email: " + email);
    }

    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
                throw new BadCredentialsException("Current password does not match");
            }
            user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
            userRepository.save(user);
            return;
        }

        var adminOpt = systemAdminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            var admin = adminOpt.get();
            if (!passwordEncoder.matches(request.currentPassword(), admin.getPasswordHash())) {
                throw new BadCredentialsException("Current password does not match");
            }
            admin.setPasswordHash(passwordEncoder.encode(request.newPassword()));
            systemAdminRepository.save(admin);
            return;
        }

        throw new IllegalArgumentException("User not found for password change");
    }
}
