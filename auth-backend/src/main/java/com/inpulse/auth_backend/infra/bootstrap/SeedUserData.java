package com.inpulse.auth_backend.infra.bootstrap;

import com.inpulse.auth_backend.core.auth.port.PasswordHashPort;
import com.inpulse.auth_backend.core.user.model.Role;
import com.inpulse.auth_backend.core.user.model.User;
import com.inpulse.auth_backend.core.user.port.UserRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SeedUserData implements CommandLineRunner {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordHashPort passwordHashPort;
    private final boolean seedEnabled;
    private final String adminEmail;
    private final String adminPassword;
    private final String userEmail;
    private final String userPassword;

    public SeedUserData(
            UserRepositoryPort userRepositoryPort,
            PasswordHashPort passwordHashPort,
            @Value("${app.seed.enabled:true}") boolean seedEnabled,
            @Value("${app.seed.admin.email}") String adminEmail,
            @Value("${app.seed.admin.password}") String adminPassword,
            @Value("${app.seed.user.email}") String userEmail,
            @Value("${app.seed.user.password}") String userPassword
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordHashPort = passwordHashPort;
        this.seedEnabled = seedEnabled;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            return;
        }

        seedUserIfMissing(adminEmail, adminPassword, Role.ADMIN);
        seedUserIfMissing(userEmail, userPassword, Role.USER);
    }

    private void seedUserIfMissing(String email, String rawPassword, Role role) {
        if (userRepositoryPort.existsByEmail(email)) {
            return;
        }

        userRepositoryPort.save(User.builder()
                .email(email)
                .password(passwordHashPort.hash(rawPassword))
                .role(role)
                .build());
    }
}
