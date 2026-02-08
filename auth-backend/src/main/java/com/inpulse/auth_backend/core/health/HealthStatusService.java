package com.inpulse.auth_backend.core.health;

import org.springframework.stereotype.Service;

@Service
public class HealthStatusService implements HealthStatusUseCase {

    @Override
    public String currentStatus() {
        return "ok";
    }
}
