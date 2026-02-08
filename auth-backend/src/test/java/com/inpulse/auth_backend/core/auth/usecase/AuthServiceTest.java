package com.inpulse.auth_backend.core.auth.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inpulse.auth_backend.core.auth.exception.AuthException;
import com.inpulse.auth_backend.core.auth.port.JwtPort;
import com.inpulse.auth_backend.core.auth.port.PasswordHashPort;
import com.inpulse.auth_backend.core.auth.model.RefreshToken;
import com.inpulse.auth_backend.core.auth.port.RefreshTokenRepositoryPort;
import com.inpulse.auth_backend.core.user.model.Role;
import com.inpulse.auth_backend.core.user.model.User;
import com.inpulse.auth_backend.core.user.port.UserRepositoryPort;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    @Mock
    private PasswordHashPort passwordHashPort;
    @Mock
    private JwtPort jwtPort;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepositoryPort,
                refreshTokenRepositoryPort,
                passwordHashPort,
                jwtPort,
                7
        );
    }

    @Test
    void loginShouldReturnAccessAndRefreshTokens() {
        User user = User.builder()
                .id(1L)
                .email("admin@inpulse.dev")
                .password("encoded-password")
                .role(Role.ADMIN)
                .build();

        when(userRepositoryPort.findByEmail("admin@inpulse.dev")).thenReturn(Optional.of(user));
        when(passwordHashPort.matches("ChangeMe123!", "encoded-password")).thenReturn(true);
        when(jwtPort.generateAccessToken(user)).thenReturn("access-token-for-1");
        when(jwtPort.accessTokenTtlSeconds()).thenReturn(900L);
        when(refreshTokenRepositoryPort.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> {
                    RefreshToken token = invocation.getArgument(0);
                    if (token.getId() == null) {
                        token.setId(1L);
                    }
                    return token;
                });

        var tokens = authService.login("admin@inpulse.dev", "ChangeMe123!");

        assertThat(tokens.accessToken()).isEqualTo("access-token-for-1");
        assertThat(tokens.refreshToken()).isNotBlank();
        assertThat(tokens.expiresIn()).isAfter(Instant.now().plusSeconds(850));
        assertThat(tokens.role()).isEqualTo(Role.ADMIN);
        verify(refreshTokenRepositoryPort).deleteByUserId(1L);
    }

    @Test
    void refreshShouldRotateToken() {
        User user = User.builder()
                .id(1L)
                .email("admin@inpulse.dev")
                .password("encoded-password")
                .role(Role.ADMIN)
                .build();
        RefreshToken existing = RefreshToken.builder()
                .id(10L)
                .token("old-refresh")
                .userId(1L)
                .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .revoked(false)
                .build();

        when(refreshTokenRepositoryPort.findByToken("old-refresh")).thenReturn(Optional.of(existing));
        when(userRepositoryPort.findById(1L)).thenReturn(Optional.of(user));
        when(jwtPort.generateAccessToken(user)).thenReturn("access-token-for-1");
        when(jwtPort.accessTokenTtlSeconds()).thenReturn(900L);
        when(refreshTokenRepositoryPort.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var tokens = authService.refresh("old-refresh");

        assertThat(existing.isRevoked()).isTrue();
        assertThat(tokens.refreshToken()).isNotEqualTo("old-refresh");
        assertThat(tokens.accessToken()).isEqualTo("access-token-for-1");
        verify(refreshTokenRepositoryPort).save(existing);
    }

    @Test
    void refreshShouldFailWhenTokenExpired() {
        RefreshToken expired = RefreshToken.builder()
                .token("expired-refresh")
                .userId(1L)
                .expiresAt(Instant.now().minus(1, ChronoUnit.MINUTES))
                .revoked(false)
                .build();
        when(refreshTokenRepositoryPort.findByToken("expired-refresh")).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> authService.refresh("expired-refresh"))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void logoutShouldRevokeTokenWhenExists() {
        RefreshToken token = RefreshToken.builder()
                .id(5L)
                .token("valid-refresh")
                .userId(1L)
                .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .revoked(false)
                .build();
        when(refreshTokenRepositoryPort.findByToken("valid-refresh")).thenReturn(Optional.of(token));
        when(refreshTokenRepositoryPort.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authService.logout("valid-refresh");

        assertThat(token.isRevoked()).isTrue();
        verify(refreshTokenRepositoryPort).save(token);
    }
}
