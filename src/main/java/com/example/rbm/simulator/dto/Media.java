package com.example.rbm.simulator.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record Media(
        @NotNull Height height,
        @NotNull @Valid ContentInfo contentInfo) {
}
