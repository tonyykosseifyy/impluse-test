package com.inpulse.auth_backend.core.auth.model;

import com.inpulse.auth_backend.core.user.model.Role;

public record AuthPrincipal(
    Long userId,
    String email,
    Role role
) {
}
