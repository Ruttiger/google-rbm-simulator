package com.example.rbm.simulator;

import com.example.rbm.simulator.dto.MessageState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AgentMessagesSimulatorControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final String BASE_URL = "/v1/phones/+5215512345678/agentMessages";

    @Test
    void textMessageReturnsQueuedWithoutEcho() {
        String payload = "{\"messageId\":\"msg-12345\",\"representative\":{\"representativeType\":\"BOT\"},\"text\":\"Hola\"}";

        webTestClient.post()
                .uri(BASE_URL)
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.deliveryState.messageState").isEqualTo(MessageState.QUEUED.name())
                .jsonPath("$.text").doesNotExist()
                .jsonPath("$.richCard").doesNotExist();
    }

    @Test
    void richCardEchoAndForceState() {
        String payload = "{"
                + "\"messageId\":\"msg-67890\"," 
                + "\"representative\":{\"representativeType\":\"BOT\"},"
                + "\"text\":\"Aqui tienes una imagen:\","
                + "\"richCard\":{"
                + "\"standaloneCard\":{"
                + "\"cardContent\":{"
                + "\"title\":\"Ejemplo RBM\"," 
                + "\"description\":\"Imagen enviada con la API\","
                + "\"media\":{"
                + "\"height\":\"MEDIUM\"," 
                + "\"contentInfo\":{\"fileUrl\":\"https://example.com/imagen.png\"}"
                + "}"
                + "}"
                + "}"
                + "}"
                + "}";

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path(BASE_URL)
                        .queryParam("forceState", MessageState.SENT.name())
                        .queryParam("echo", true)
                        .build())
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.deliveryState.messageState").isEqualTo(MessageState.SENT.name())
                .jsonPath("$.text").isEqualTo("Aqui tienes una imagen:")
                .jsonPath("$.richCard.standaloneCard.cardContent.title").isEqualTo("Ejemplo RBM");
    }

    @Test
    void unauthorizedIfMissingBearer() {
        String payload = "{\"messageId\":\"msg-1\",\"representative\":{\"representativeType\":\"BOT\"},\"text\":\"hola\"}";

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void unsupportedMediaType() {
        String payload = "{}";

        webTestClient.post()
                .uri(BASE_URL)
                .header("Authorization", "Bearer token")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isEqualTo(415);
    }

    @Test
    void badRequestForInvalidE164() {
        String payload = "{\"messageId\":\"msg-1\",\"representative\":{\"representativeType\":\"BOT\"},\"text\":\"hola\"}";

        webTestClient.post()
                .uri("/v1/phones/12345/agentMessages")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void badRequestWhenMissingTextAndRichCard() {
        String payload = "{\"messageId\":\"msg-1\",\"representative\":{\"representativeType\":\"BOT\"}}";

        webTestClient.post()
                .uri(BASE_URL)
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
