package com.example.rbm.simulator.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CardContent(
        @Size(max = 200) String title,
        @Size(max = 2000) String description,
        @NotNull @Valid Media media) {
}
