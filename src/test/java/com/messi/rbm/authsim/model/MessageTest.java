package com.messi.rbm.authsim.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void serializesAndDeserializes() throws Exception {
        Message message = new Message(
                "name",
                "123",
                "hello",
                new Message.Representative("BOT"),
                "2024-01-01T00:00:00Z");

        String json = mapper.writeValueAsString(message);
        Message read = mapper.readValue(json, Message.class);

        assertEquals("123", read.messageId());
        assertEquals("hello", read.text());
        assertEquals("BOT", read.representative().representativeType());
    }

    @Test
    void validatesRequiredFields() {
        Message invalid = new Message(null, null, null, null, null);
        Set<ConstraintViolation<Message>> violations = validator.validate(invalid);
        Set<String> fields = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(fields.contains("messageId"));
        assertTrue(fields.contains("text"));
        assertTrue(fields.contains("representative"));
        assertEquals(3, fields.size());
    }
}
