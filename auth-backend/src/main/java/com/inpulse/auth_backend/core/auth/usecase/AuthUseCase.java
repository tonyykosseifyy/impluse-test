package com.inpulse.auth_backend.core.auth.usecase;

import com.inpulse.auth_backend.core.auth.model.AuthTokens;

public interface AuthUseCase {
    AuthTokens login(String email, String password);
    AuthTokens refresh(String refreshToken);
    void logout(String refreshToken);
}
