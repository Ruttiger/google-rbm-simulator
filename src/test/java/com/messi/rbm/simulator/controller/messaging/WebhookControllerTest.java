package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.model.communications.WebhookConfig;
import com.messi.rbm.simulator.service.communications.WebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class WebhookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WebhookService webhookService;

    @Test
    void registersWebhook() {
        webTestClient.post()
                .uri("/v1/webhooks")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"agentId\":\"agent-1\",\"webhookUrl\":\"http://example.com/hook\"}")
                .exchange()
                .expectStatus().isOk();

        webhookService.getConfig("agent-1")
                .map(WebhookConfig::webhookUrl)
                .as(StepVerifier::create)
                .expectNext("http://example.com/hook")
                .verifyComplete();
    }
}

