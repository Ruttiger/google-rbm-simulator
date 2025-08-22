package com.messi.rbm.simulator.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public record Message(
        String name,
        String messageId,
        String text,
        Representative representative,
        String sendTime,
        JsonNode richCard,
        JsonNode media,
        List<JsonNode> suggestions
) {
    public record Representative(String representativeType) {}
}
