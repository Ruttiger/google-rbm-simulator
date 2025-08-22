package com.messi.rbm.simulator.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AgentMessageControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private String read(String file) throws Exception {
        return Files.readString(Path.of("src/test/resources/messages/" + file));
    }

    @Test
    void handlesTextMessage() throws Exception {
        webTestClient.post()
                .uri("/v1/phones/12345/agentMessages?agentId=test&messageId=msg-text")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(read("text-message.json"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("phones/12345/agentMessages/msg-text")
                .jsonPath("$.sendTime").exists()
                .jsonPath("$.agentId").isEqualTo("test")
                .jsonPath("$.contentMessage.text").isEqualTo("Hola desde RBM");
    }

    @Test
    void handlesRichCardMessage() throws Exception {
        webTestClient.post()
                .uri("/v1/phones/12345/agentMessages?agentId=test&messageId=msg-richcard")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(read("rich-card-message.json"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.agentId").isEqualTo("test")
                .jsonPath("$.contentMessage.richCard.standaloneCard.cardContent.title").isEqualTo("Ejemplo");
    }

    @Test
    void handlesMediaMessage() throws Exception {
        webTestClient.post()
                .uri("/v1/phones/12345/agentMessages?agentId=test&messageId=msg-media")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(read("media-message.json"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.agentId").isEqualTo("test")
                .jsonPath("$.contentMessage.contentInfo.fileUrl").isEqualTo("https://example.com/media.png");
    }

    @Test
    void handlesSuggestionsMessage() throws Exception {
        webTestClient.post()
                .uri("/v1/phones/12345/agentMessages?agentId=test&messageId=msg-suggestions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(read("suggestions-message.json"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.agentId").isEqualTo("test")
                .jsonPath("$.contentMessage.text").isEqualTo("¿Deseas continuar?")
                .jsonPath("$.contentMessage.suggestions[0].action.text").isEqualTo("Sí");
    }

    @Test
    void rejectsInvalidMessage() throws Exception {
        webTestClient.post()
                .uri("/v1/phones/12345/agentMessages?agentId=test&messageId=msg-invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(read("invalid-message.json"))
                .exchange()
                .expectStatus().isBadRequest();
    }
}
