package com.inpulse.auth_backend.core.auth.port;

public interface PasswordHashPort {
    String hash(String rawPassword);

    boolean matches(String rawPassword, String hashedPassword);
}
