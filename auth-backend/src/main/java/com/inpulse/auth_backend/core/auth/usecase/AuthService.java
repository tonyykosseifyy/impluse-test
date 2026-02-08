package com.inpulse.auth_backend.core.auth.usecase;

import com.inpulse.auth_backend.core.auth.exception.AuthException;
import com.inpulse.auth_backend.core.auth.model.AuthTokens;
import com.inpulse.auth_backend.core.auth.port.JwtPort;
import com.inpulse.auth_backend.core.auth.port.PasswordHashPort;
import com.inpulse.auth_backend.core.auth.model.RefreshToken;
import com.inpulse.auth_backend.core.auth.port.RefreshTokenRepositoryPort;
import com.inpulse.auth_backend.core.user.model.User;
import com.inpulse.auth_backend.core.user.port.UserRepositoryPort;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final PasswordHashPort passwordHashPort;
    private final JwtPort jwtPort;
    private final long refreshTokenDays;

    public AuthService(
            UserRepositoryPort userRepositoryPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            PasswordHashPort passwordHashPort,
            JwtPort jwtPort,
            @Value("${app.jwt.refresh-expiration-days}") long refreshTokenDays
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.passwordHashPort = passwordHashPort;
        this.jwtPort = jwtPort;
        this.refreshTokenDays = refreshTokenDays;
    }

    @Override
    @Transactional
    public AuthTokens login(String email, String password) {
        User user = userRepositoryPort.findByEmail(email).orElseThrow(() -> new AuthException("Invalid email or password"));

        if (!passwordHashPort.matches(password, user.getPassword())) {
            throw new AuthException("Invalid email or password");
        }

        refreshTokenRepositoryPort.deleteByUserId(user.getId());

        RefreshToken newGeneratedRefreshToken = refreshTokenRepositoryPort.save(RefreshToken.builder()
            .token(newRefreshTokenValue())
            .userId(user.getId())
            .expiresAt(refreshExpiry())
            .revoked(false)
            .build());

        long accessTokenTtlSeconds = jwtPort.accessTokenTtlSeconds();

        return new AuthTokens(
            jwtPort.generateAccessToken(user),
            newGeneratedRefreshToken.getToken(),
            Instant.now().plusSeconds(accessTokenTtlSeconds),
            user.getRole()
        );
    }

    @Override
    @Transactional
    public AuthTokens refresh(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepositoryPort.findByToken(refreshTokenValue).orElseThrow(() -> new AuthException("Refresh token is invalid"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthException("Refresh token is expired or revoked");
        }

        User user = userRepositoryPort.findById(refreshToken.getUserId()).orElseThrow(() -> new AuthException("User not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepositoryPort.save(refreshToken);

        RefreshToken rotatedToken = refreshTokenRepositoryPort.save(RefreshToken.builder()
            .token(newRefreshTokenValue())
            .userId(user.getId())
            .expiresAt(refreshExpiry())
            .revoked(false)
            .build());

        long accessTokenTtlSeconds = jwtPort.accessTokenTtlSeconds();
        return new AuthTokens(jwtPort.generateAccessToken(user),
            rotatedToken.getToken(),
            Instant.now().plusSeconds(accessTokenTtlSeconds),
            user.getRole());
    }

    @Override
    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenRepositoryPort.findByToken(refreshTokenValue).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepositoryPort.save(token);
        });
    }

    private String newRefreshTokenValue() {
        return UUID.randomUUID().toString();
    }

    private Instant refreshExpiry() {
        return Instant.now().plus(refreshTokenDays, ChronoUnit.DAYS);
    }
}
