package com.inpulse.auth_backend.core.auth.port;

import com.inpulse.auth_backend.core.auth.model.AuthPrincipal;
import com.inpulse.auth_backend.core.user.model.User;
import java.util.Optional;

public interface JwtPort {
    String generateAccessToken(User user);

    long accessTokenTtlSeconds();

    Optional<AuthPrincipal> parseAccessToken(String token);
}
