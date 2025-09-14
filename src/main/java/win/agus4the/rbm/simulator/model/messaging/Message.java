package win.agus4the.rbm.simulator.model.messaging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;

/**
 * Representation of the RBM agent message resource.
 * <p>
 * The official API expects the request body to contain a single
 * {@code contentMessage} object with the actual message payload. The previous
 * implementation modelled top level fields such as {@code text} or
 * {@code richCard}, which deviated from the specification. This record now
 * mirrors the schema defined in the RBM reference where the content is grouped
 * under {@code contentMessage}.
 *
 * @param name          RBM resource name of the message.
 * @param sendTime      ISO 8601 timestamp when the message was sent.
 * @param contentMessage wrapper containing the actual message payload.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Message(
        String name,
        String sendTime,
        @Valid @NotNull AgentContentMessage contentMessage
) {

    /**
     * Content of the agent message as defined by the RBM API.
     *
     * @param text            plain text message.
     * @param richCard        rich card content for rich interactions.
     * @param uploadedRbmFile reference to an uploaded RBM file.
     * @param contentInfo     additional content information metadata.
     * @param suggestions     list of suggested replies or actions.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AgentContentMessage(
            String text,
            JsonNode richCard,
            JsonNode uploadedRbmFile,
            JsonNode contentInfo,
            List<JsonNode> suggestions
    ) {

        public AgentContentMessage {
            if (suggestions != null) {
                suggestions = List.copyOf(suggestions);
            }
        }

        @SuppressFBWarnings("EI_EXPOSE_REP")
        public List<JsonNode> suggestions() {
            return suggestions == null ? null : List.copyOf(suggestions);
        }

        @AssertTrue(message = "contentMessage must contain at least one non-empty field")
        public boolean hasContent() {
            return (text != null && !text.isBlank())
                    || richCard != null
                    || uploadedRbmFile != null
                    || contentInfo != null
                    || (suggestions != null && !suggestions.isEmpty());
        }
    }
}
