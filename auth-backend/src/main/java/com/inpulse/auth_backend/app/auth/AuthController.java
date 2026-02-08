package com.inpulse.auth_backend.app.auth;

import com.inpulse.auth_backend.app.auth.requests.LoginRequest;
import com.inpulse.auth_backend.app.auth.requests.LogoutRequest;
import com.inpulse.auth_backend.app.auth.requests.RefreshRequest;
import com.inpulse.auth_backend.app.auth.responses.AuthResponse;
import com.inpulse.auth_backend.app.auth.responses.MeResponse;
import com.inpulse.auth_backend.core.auth.model.AuthPrincipal;
import com.inpulse.auth_backend.core.auth.model.AuthTokens;
import com.inpulse.auth_backend.core.auth.usecase.AuthUseCase;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return toResponse(authUseCase.login(request.email(), request.password()));
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return toResponse(authUseCase.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authUseCase.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("status", "ok", "time", Instant.now().toString());
    }

    @GetMapping("/me")
    public MeResponse me(Authentication authentication) {
        AuthPrincipal principal = (AuthPrincipal) authentication.getPrincipal();
        return new MeResponse(principal.userId(), principal.email(), principal.role().name());
    }

    private AuthResponse toResponse(AuthTokens tokens) {
        return new AuthResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                "Bearer",
                tokens.expiresIn(),
                tokens.role().name()
        );
    }
}
