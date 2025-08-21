package com.messi.rbm.authsim;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AgentMessageControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void sendTextMessageReturnsDelivered() {
        String body = "{" +
                "\"messageId\":\"1\"," +
                "\"text\":\"hola\"" +
                "}";
        webTestClient.post().uri("/v1/phones/+123/agentMessages?agentId=bot")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo("DELIVERED");
    }

    @Test
    void richCardWithEchoAndForcedState() {
        String body = "{" +
                "\"messageId\":\"2\"," +
                "\"richCard\":{" +
                "\"standaloneCard\":{" +
                "\"cardContent\":{" +
                "\"title\":\"t\"}}}}";
        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/v1/phones/+123/agentMessages")
                        .queryParam("agentId", "bot")
                        .queryParam("forceState", "SENT")
                        .queryParam("echo", "true")
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state").isEqualTo("SENT")
                .jsonPath("$.echo.richCard").exists();
    }
}
