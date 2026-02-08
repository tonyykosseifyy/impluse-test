package com.inpulse.auth_backend.infra.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.inpulse.auth_backend.core.user.model.Role;
import com.inpulse.auth_backend.core.user.model.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    @Test
    void generateAndParseShouldReturnPrincipal() {
        JwtService jwtService = new JwtService(new JwtProperties(
                "test-secret-key-test-secret-key-123456",
                15,
                7
        ));
        User user = User.builder()
                .id(7L)
                .email("admin@inpulse.dev")
                .role(Role.ADMIN)
                .build();

        String token = jwtService.generateAccessToken(user);
        var parsed = jwtService.parseAccessToken(token);

        assertThat(parsed).isPresent();
        assertThat(parsed.get().userId()).isEqualTo(7L);
        assertThat(parsed.get().email()).isEqualTo("admin@inpulse.dev");
        assertThat(parsed.get().role()).isEqualTo(Role.ADMIN);
    }

    @Test
    void parseInvalidTokenShouldReturnEmpty() {
        JwtService jwtService = new JwtService(new JwtProperties(
                "test-secret-key-test-secret-key-123456",
                15,
                7
        ));

        Optional<?> parsed = jwtService.parseAccessToken("not-a-jwt");

        assertThat(parsed).isEmpty();
    }

    @Test
    void shouldFailWhenSecretTooShort() {
        assertThatThrownBy(() -> new JwtService(new JwtProperties("short-secret", 15, 7)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 32 bytes");
    }
}
