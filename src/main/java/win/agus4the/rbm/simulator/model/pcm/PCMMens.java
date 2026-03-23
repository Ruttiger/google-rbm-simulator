package win.agus4the.rbm.simulator.model.pcm;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Represents a PCM submit request for text/binary MT messages.
 *
 * @param sender message sender
 * @param recipients destination recipients
 * @param smsText text payload
 * @param smsBinary binary payload encoded as string
 * @param deliveryReport delivery report mode
 * @param deliveryReportURL callback URL for delivery reports
 * @param expiryDate message expiration timestamp
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

    public PCMMens {
        recipients = recipients == null ? List.of() : List.copyOf(recipients);
    }

    public boolean hasPayload() {
        return (smsText != null && !smsText.isBlank()) || (smsBinary != null && !smsBinary.isBlank());
    }
}
