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
class AgentEventControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void agentEventEndpointReturnsStructuredResponse() {
        webTestClient.post().uri("/v1/phones/123/agentEvents?agentId=AGENT")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("{\"eventId\":\"e1\",\"eventType\":\"READ\",\"messageId\":\"m1\"}"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("received")
                .jsonPath("$.eventType").isEqualTo("READ")
                .jsonPath("$.eventId").isEqualTo("e1");
    }

    @Test
    void agentEventEndpointMissingAgentIdReturnsBadRequest() {
        webTestClient.post().uri("/v1/phones/123/agentEvents")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("{\"eventId\":\"e1\",\"eventType\":\"READ\",\"messageId\":\"m1\"}"))
                .exchange()
                .expectStatus().isBadRequest();
    }
}
