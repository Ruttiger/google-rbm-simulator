package win.agus4the.rbm.simulator.model.pcm;

import jakarta.validation.constraints.NotBlank;

public record Recipient(@NotBlank String to) {
}
