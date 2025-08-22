package com.messi.rbm.simulator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.messi.rbm.simulator.model.Message;
import com.messi.rbm.simulator.model.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageTypeDetectorTest {

    private MessageTypeDetector detector;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        detector = new MessageTypeDetector();
        mapper = new ObjectMapper();
    }

    @Test
    void detectsTextMessage() {
        Message message = new Message(null, "id1", "hola", new Message.Representative("BOT"), null, null, null, null);
        assertEquals(MessageType.TEXT, detector.detect(message));
    }

    @Test
    void detectsRichCardMessage() throws IOException {
        JsonNode richCard = mapper.readTree("{\"standaloneCard\":{}}" );
        Message message = new Message(null, "id2", null, new Message.Representative("BOT"), null, richCard, null, null);
        assertEquals(MessageType.RICH_CARD, detector.detect(message));
    }

    @Test
    void detectsMediaMessage() throws IOException {
        JsonNode media = mapper.readTree("{\"fileUrl\":\"https://example.com\"}" );
        Message message = new Message(null, "id3", null, new Message.Representative("BOT"), null, null, media, null);
        assertEquals(MessageType.MEDIA, detector.detect(message));
    }

}
