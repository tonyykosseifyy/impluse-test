package com.inpulse.auth_backend.app.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inpulse.auth_backend.app.auth.requests.LoginRequest;
import com.inpulse.auth_backend.app.auth.requests.LogoutRequest;
import com.inpulse.auth_backend.app.auth.requests.RefreshRequest;
import com.inpulse.auth_backend.app.auth.responses.AuthResponse;
import com.inpulse.auth_backend.core.auth.model.AuthTokens;
import com.inpulse.auth_backend.core.auth.usecase.AuthUseCase;
import com.inpulse.auth_backend.core.user.model.Role;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthUseCase authUseCase;

    @InjectMocks
    private AuthController authController;

    @Test
    void loginShouldReturnMappedResponse() {
        Instant expiresAt = Instant.parse("2030-01-01T00:00:00Z");
        when(authUseCase.login("admin@inpulse.dev", "ChangeMe123!"))
                .thenReturn(new AuthTokens("access-token", "refresh-token", expiresAt, Role.ADMIN));

        AuthResponse response = authController.login(new LoginRequest("admin@inpulse.dev", "ChangeMe123!"));

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.role()).isEqualTo("ADMIN");
        assertThat(response.expiresIn()).isEqualTo(expiresAt);
    }

    @Test
    void refreshShouldDelegateToUseCase() {
        when(authUseCase.refresh("old-refresh"))
                .thenReturn(new AuthTokens("access-token", "refresh-token", Instant.now(), Role.ADMIN));

        AuthResponse response = authController.refresh(new RefreshRequest("old-refresh"));

        assertThat(response.accessToken()).isEqualTo("access-token");
        verify(authUseCase).refresh("old-refresh");
    }

    @Test
    void logoutShouldDelegateToUseCase() {
        authController.logout(new LogoutRequest("to-revoke"));

        verify(authUseCase).logout("to-revoke");
    }
}
