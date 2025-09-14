package win.agus4the.rbm.authsim;

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
                .jsonPath("$.name").isEqualTo("phones/12345/agentMessages/1")
                .jsonPath("$.sendTime").exists()
                .jsonPath("$.contentMessage.text").isEqualTo("hi");
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

