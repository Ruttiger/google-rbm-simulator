package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.service.BusinessMessagingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.junit.jupiter.api.BeforeEach;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CapabilityControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    BusinessMessagingService messagingService;

    @BeforeEach
    void reset() {
        messagingService.reset();
    }

    @Test
    void returnsNotFoundWhenNotTester() {
        webTestClient.get().uri("/v1/phones/123/capabilities?agentId=a1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void returnsCapabilitiesForTester() {
        messagingService.addTester("a1", "123").block();
        webTestClient.get().uri("/v1/phones/123/capabilities?agentId=a1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("phones/123/capabilities");
    }
}
