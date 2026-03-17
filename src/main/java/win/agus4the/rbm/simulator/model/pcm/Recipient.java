package win.agus4the.rbm.simulator.model.pcm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Recipient(
        @NotNull
        String to
) {
    /**
     * Metadata about the human or automated representative.
     *
     * @param representativeType type of representative.
     */
    public record Representative(@NotBlank String representativeType) {
    }
}
