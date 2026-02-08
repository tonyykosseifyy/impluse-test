package com.inpulse.auth_backend.core.auth.port;

import com.inpulse.auth_backend.core.auth.model.RefreshToken;
import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(Long userId);
}
