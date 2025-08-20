package com.example.rbm.simulator.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AgentMessageRequest(
        @NotBlank @Size(max = 100) String messageId,
        @NotNull @Valid Representative representative,
        @Size(max = 4096) String text,
        @Valid RichCard richCard) {

    @AssertTrue(message = "either text or richCard must be provided")
    public boolean isTextOrRichCardPresent() {
        return (text != null && !text.isBlank()) || richCard != null;
    }
}
