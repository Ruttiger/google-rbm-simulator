package win.agus4the.rbm.authsim.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * User message delivered to the RBM agent simulator.
 *
 * @param name          optional resource name.
 * @param messageId     identifier of the message.
 * @param text          text content supplied by the user.
 * @param representative information about the representative sending the message.
 * @param sendTime      time the message was sent.
 */
public record Message(
        String name,
        @NotBlank String messageId,
        @NotBlank String text,
        @NotNull Representative representative,
        String sendTime
) {
    /**
     * Metadata about the human or automated representative.
     *
     * @param representativeType type of representative.
     */
    public record Representative(@NotBlank String representativeType) {
    }
}
