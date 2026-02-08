package com.inpulse.auth_backend.app.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.inpulse.auth_backend.core.health.HealthStatusUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HealthControllerTest {

    @Mock
    private HealthStatusUseCase healthStatusUseCase;

    @InjectMocks
    private HealthController healthController;

    @Test
    void healthShouldReturnOkStatusFromUseCase() {
        when(healthStatusUseCase.currentStatus()).thenReturn("ok");

        var response = healthController.health();

        assertThat(response).containsEntry("status", "ok");
    }
}
