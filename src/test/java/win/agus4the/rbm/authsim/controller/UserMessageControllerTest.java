package win.agus4the.rbm.authsim.controller;

import win.agus4the.rbm.simulator.GoogleRbmSimulatorApplication;
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
class UserMessageControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void userMessageEndpointReturnsStructuredResponse() {
        webTestClient.post().uri("/v1/phones/123/messages?agentId=AGENT")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("{\"messageId\":\"1\",\"text\":\"hi\",\"representative\":{\"representativeType\":\"BOT\"}}"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("received")
                .jsonPath("$.msisdn").isEqualTo("123")
                .jsonPath("$.agentId").isEqualTo("AGENT")
                .jsonPath("$.messageId").isEqualTo("1")
                .jsonPath("$.originalText").isEqualTo("hi");
    }

    @Test
    void userMessageEndpointValidationError() {
        webTestClient.post().uri("/v1/phones/123/messages?agentId=AGENT")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("{\"messageId\":\"1\",\"representative\":{\"representativeType\":\"BOT\"}}"))
                .exchange()
                .expectStatus().isBadRequest();
    }
}
