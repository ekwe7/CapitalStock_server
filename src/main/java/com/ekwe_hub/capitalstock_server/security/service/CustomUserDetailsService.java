package com.ekwe_hub.capitalstock_server.security.service;

import com.ekwe_hub.capitalstock_server.admin.repository.SystemAdminRepository;
import com.ekwe_hub.capitalstock_server.auth.model.User;
import com.ekwe_hub.capitalstock_server.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SystemAdminRepository systemAdminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // First check standard users
        var userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            var authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                    .collect(Collectors.toList());

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPasswordHash(),
                    user.getStatus() == User.UserStatus.ACTIVE,
                    true, true, true,
                    authorities
            );
        }

        // Fallback check for SystemAdmins
        var adminOptional = systemAdminRepository.findByEmail(email);
        if (adminOptional.isPresent()) {
            var admin = adminOptional.get();
            return new org.springframework.security.core.userdetails.User(
                    admin.getEmail(),
                    admin.getPasswordHash(),
                    admin.getStatus() == com.ekwe_hub.capitalstock_server.admin.model.SystemAdmin.AdminStatus.ACTIVE,
                    true, true, true,
                    List.of(new SimpleGrantedAuthority("ROLE_SYSTEM_ADMIN"))
            );
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
