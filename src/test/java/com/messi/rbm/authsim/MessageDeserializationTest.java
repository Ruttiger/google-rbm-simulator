package com.messi.rbm.authsim;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messi.rbm.authsim.model.Message;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageDeserializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void readsTextMessage() throws Exception {
        String json = "{" +
                "\"messageId\":\"1\"," +
                "\"text\":\"hola\"" +
                "}";
        Message message = mapper.readValue(json, Message.class);
        assertThat(message.text()).isEqualTo("hola");
    }

    @Test
    void readsRichCardMessage() throws Exception {
        String json = "{" +
                "\"messageId\":\"1\"," +
                "\"richCard\":{" +
                "\"standaloneCard\":{" +
                "\"cardContent\":{" +
                "\"title\":\"t\"," +
                "\"description\":\"d\"," +
                "\"media\":{" +
                "\"height\":\"MEDIUM\"," +
                "\"contentInfo\":{" +
                "\"fileUrl\":\"https://example.com/img.png\"," +
                "\"thumbnailUrl\":\"https://example.com/thumb.png\"}}}}}}";
        Message message = mapper.readValue(json, Message.class);
        assertThat(message.richCard()).isNotNull();
        assertThat(message.richCard().standaloneCard().cardContent().title()).isEqualTo("t");
    }
}
