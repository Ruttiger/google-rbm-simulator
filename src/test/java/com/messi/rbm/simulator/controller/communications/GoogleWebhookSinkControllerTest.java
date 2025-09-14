package com.messi.rbm.simulator.controller.communications;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class GoogleWebhookSinkControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void echoesSecretForVerification() {
        webTestClient.post()
                .uri("/webhook/google/agent")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"secret\":\"abc\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.secret").isEqualTo("abc");
    }

    @Test
    void acceptsGenericEvent() {
        webTestClient.post()
                .uri("/webhook/google/agent")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"foo\":\"bar\"}")
                .exchange()
                .expectStatus().isOk();
    }
}
