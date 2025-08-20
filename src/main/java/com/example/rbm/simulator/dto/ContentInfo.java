package com.example.rbm.simulator.dto;

import jakarta.validation.constraints.NotBlank;

public record ContentInfo(
        @NotBlank String fileUrl,
        String thumbnailUrl,
        Boolean forceRefresh) {
}
