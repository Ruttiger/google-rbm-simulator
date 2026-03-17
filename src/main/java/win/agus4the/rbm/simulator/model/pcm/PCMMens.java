package win.agus4the.rbm.simulator.model.pcm;

import java.util.List;

/**
 * Represents a message SMS sent to the PCM simulator.
 *
 * @param sender the sender is the phone number of the user sending the message.
 * @param recipients the recipients are the phone numbers of the users receiving the message, in E.164 format.
 * @param smsText the text content supplied by the user.
 * @param deliveryReport the delivery report status of the message.
 * @param deliveryReportURL the URL where the delivery report can be retrieved.
 * @param expiryDate the expiry date of the message.
 */
public record PCMMens(
        String sender,
        List<Recipient> recipients,
        String smsText,
        String deliveryReport,
        String deliveryReportURL,
        String expiryDate
) {

    public PCMMens {
        recipients = recipients == null ? List.of() : List.copyOf(recipients);
    }
}
