package com.example.rbm.simulator.dto;

import jakarta.validation.constraints.NotNull;

public record Representative(@NotNull RepresentativeType representativeType) {
}
