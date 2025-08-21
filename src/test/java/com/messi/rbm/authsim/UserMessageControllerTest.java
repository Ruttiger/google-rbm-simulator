package com.messi.rbm.authsim;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class UserMessageControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void receiveUserMessage() {
        String body = "{" +
                "\"messageId\":\"3\"," +
                "\"text\":\"hola agente\"" +
                "}";
        webTestClient.post().uri("/v1/phones/+123/userMessages?agentId=bot")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("received")
                .jsonPath("$.messageId").isEqualTo("3");
    }
}
