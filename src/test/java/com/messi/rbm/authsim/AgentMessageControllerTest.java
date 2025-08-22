package com.messi.rbm.authsim;

import com.messi.rbm.simulator.GoogleRbmSimulatorApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = GoogleRbmSimulatorApplication.class)
@AutoConfigureWebTestClient
class AgentMessageControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void agentMessageEndpointReceivesMessage() {
        webTestClient.post().uri("/v1/phones/12345/agentMessages?agentId=AGENT&messageId=1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("{\"contentMessage\":{\"text\":\"hi\"}}"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("received")
                .jsonPath("$.forceState").doesNotExist()
                .jsonPath("$.echo").doesNotExist();
    }

    @Test
    void agentMessageEndpointEchoesMessage() {
        webTestClient.post().uri("/v1/phones/12345/agentMessages?agentId=AGENT&messageId=1&echo=true")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("{\"contentMessage\":{\"text\":\"hi\"}}"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.echo.contentMessage.text").isEqualTo("hi");
    }

    @Test
    void agentMessageEndpointForcesState() {
        webTestClient.post().uri("/v1/phones/12345/agentMessages?agentId=AGENT&messageId=1&forceState=SENT")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("{\"contentMessage\":{\"text\":\"hi\"}}"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.forceState").isEqualTo("SENT");
    }

    @Test
    void agentMessageEndpointForcesStateAndEchoes() {
        webTestClient.post().uri("/v1/phones/12345/agentMessages?agentId=AGENT&messageId=1&forceState=SENT&echo=true")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("{\"contentMessage\":{\"text\":\"hi\"}}"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.forceState").isEqualTo("SENT")
                .jsonPath("$.echo.contentMessage.text").isEqualTo("hi");
    }

    @Test
    void agentMessageEndpointRejectsInvalidForceState() {
        webTestClient.post().uri("/v1/phones/12345/agentMessages?agentId=AGENT&messageId=1&forceState=BAD")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("{\"contentMessage\":{\"text\":\"hi\"}}"))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void agentMessageEndpointRejectsInvalidEcho() {
        webTestClient.post().uri("/v1/phones/12345/agentMessages?agentId=AGENT&messageId=1&echo=notbool")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("{\"contentMessage\":{\"text\":\"hi\"}}"))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void userMessageEndpointReceivesMessage() {
        webTestClient.post().uri("/v1/phones/12345/messages?agentId=AGENT")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("{\"messageId\":\"2\",\"text\":\"hello\",\"representative\":{\"representativeType\":\"BOT\"}}"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("received");
    }

    @Test
    void agentEventEndpointReceivesEvent() {
        webTestClient.post().uri("/v1/phones/12345/agentEvents?agentId=AGENT")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("{\"eventId\":\"e1\",\"eventType\":\"READ\",\"messageId\":\"1\"}"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.eventType").isEqualTo("READ");
    }
}

