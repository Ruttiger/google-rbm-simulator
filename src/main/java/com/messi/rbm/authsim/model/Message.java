package com.messi.rbm.authsim.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Message(
        String name,
        @NotBlank String messageId,
        @NotBlank String text,
        @NotNull Representative representative,
        String sendTime
) {
    public record Representative(@NotBlank String representativeType) {}
}
