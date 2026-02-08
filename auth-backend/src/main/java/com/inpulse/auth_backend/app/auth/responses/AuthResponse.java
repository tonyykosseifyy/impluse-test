package com.inpulse.auth_backend.app.auth.responses;

import java.time.Instant;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    Instant expiresIn,
    String role
) {
}
