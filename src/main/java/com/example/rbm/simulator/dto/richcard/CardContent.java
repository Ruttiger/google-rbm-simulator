package com.example.rbm.simulator.dto.richcard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public record CardContent(
        @Size(max = 200) String title,
        @Size(max = 2000) String description,
        @NotNull @Valid Media media
) {
}
