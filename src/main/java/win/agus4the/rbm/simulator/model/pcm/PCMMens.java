package win.agus4the.rbm.simulator.model.pcm;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Represents a PCM submit request for text/binary MT messages.
 */
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification = "Record performs defensive copy")
public record PCMMens(
        @NotBlank String sender,
        @NotEmpty List<@Valid Recipient> recipients,
        String smsText,
        String smsBinary,
        String deliveryReport,
        String deliveryReportURL,
        String expiryDate
) {

    public PCMMens {
        recipients = recipients == null ? List.of() : List.copyOf(recipients);
    }

    public boolean hasPayload() {
        return (smsText != null && !smsText.isBlank()) || (smsBinary != null && !smsBinary.isBlank());
    }
}
