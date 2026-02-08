package com.inpulse.auth_backend.app.auth.responses;

public record MeResponse(
    Long userId,
    String email,
    String role
) {
}
