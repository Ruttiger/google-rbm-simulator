package win.agus4the.rbm.simulator.model.pcm;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Represents a PCM submit request for text/binary MT messages.
 */
public record PCMMens(
        @NotBlank String sender,
        @NotEmpty List<@Valid Recipient> recipients,
        String smsText,
        String smsBinary,
        String deliveryReport,
        String deliveryReportURL,
        String expiryDate
) {

    public boolean hasPayload() {
        return (smsText != null && !smsText.isBlank()) || (smsBinary != null && !smsBinary.isBlank());
    }
}
