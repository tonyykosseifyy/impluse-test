package com.inpulse.auth_backend.app.health;

import com.inpulse.auth_backend.core.health.HealthStatusUseCase;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final HealthStatusUseCase healthStatusUseCase;

    public HealthController(HealthStatusUseCase healthStatusUseCase) {
        this.healthStatusUseCase = healthStatusUseCase;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", healthStatusUseCase.currentStatus());
    }
}
