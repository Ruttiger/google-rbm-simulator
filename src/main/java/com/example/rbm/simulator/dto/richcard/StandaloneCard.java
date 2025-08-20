package com.example.rbm.simulator.dto.richcard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record StandaloneCard(
        @NotNull @Valid CardContent cardContent
) {
}
