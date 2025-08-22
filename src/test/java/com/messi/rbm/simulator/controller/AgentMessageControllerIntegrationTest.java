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
                .uri("/v1/phones/12345/agentMessages?agentId=test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(read("text-message.json"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.messageType").isEqualTo("TEXT")
                .jsonPath("$.originalText").isEqualTo("Hola desde RBM");
    }

    @Test
    void handlesRichCardMessage() throws Exception {
        webTestClient.post()
                .uri("/v1/phones/12345/agentMessages?agentId=test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(read("rich-card-message.json"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.messageType").isEqualTo("RICH_CARD");
    }

    @Test
    void handlesMediaMessage() throws Exception {
        webTestClient.post()
                .uri("/v1/phones/12345/agentMessages?agentId=test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(read("media-message.json"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.messageType").isEqualTo("MEDIA");
    }

    @Test
    void handlesSuggestionsMessage() throws Exception {
        webTestClient.post()
                .uri("/v1/phones/12345/agentMessages?agentId=test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(read("suggestions-message.json"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.messageType").isEqualTo("TEXT")
                .jsonPath("$.suggestions[0].action.text").isEqualTo("Sí");
    }
}
