package com.messi.rbm.authsim.model;

public record Message(
        String name,
        String messageId,
        String text,
        Representative representative,
        String sendTime
) {
    public record Representative(String representativeType) {}
}
