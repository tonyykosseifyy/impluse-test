package com.inpulse.auth_backend.app.common.responses;

import java.time.Instant;

public record ErrorResponse(
        String message,
        Instant timestamp
) {
}
