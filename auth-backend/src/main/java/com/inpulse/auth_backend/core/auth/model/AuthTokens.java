package com.inpulse.auth_backend.core.auth.model;

import com.inpulse.auth_backend.core.user.model.Role;
import java.time.Instant;

public record AuthTokens(
    String accessToken,
    String refreshToken,
    Instant expiresIn,
    Role role
) {
}
