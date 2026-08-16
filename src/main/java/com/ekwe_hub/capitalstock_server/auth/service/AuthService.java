package com.ekwe_hub.capitalstock_server.auth.service;

import com.ekwe_hub.capitalstock_server.auth.dto.request.*;
import com.ekwe_hub.capitalstock_server.auth.dto.response.*;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(String email);
    UserProfileResponse getCurrentUserProfile(String email);
    void changePassword(String email, ChangePasswordRequest request);
}
