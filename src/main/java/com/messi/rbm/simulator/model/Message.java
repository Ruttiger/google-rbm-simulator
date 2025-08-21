package com.messi.rbm.simulator.model;

public record Message(
        String name,
        String messageId,
        String text,
        Representative representative,
        String sendTime
) {
    public record Representative(String representativeType) {}
}
