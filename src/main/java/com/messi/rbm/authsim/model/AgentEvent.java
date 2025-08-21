package com.messi.rbm.authsim.model;

public record AgentEvent(
        String eventId,
        String eventType,
        String messageId
) {}
