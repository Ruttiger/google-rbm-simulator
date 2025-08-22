package com.messi.rbm.simulator.model;

import com.fasterxml.jackson.databind.JsonNode;
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
public record Message(
        String name,
        String sendTime,
        AgentContentMessage contentMessage
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
    public record AgentContentMessage(
            String text,
            JsonNode richCard,
            JsonNode uploadedRbmFile,
            JsonNode contentInfo,
            List<JsonNode> suggestions
    ) {
    }
}
