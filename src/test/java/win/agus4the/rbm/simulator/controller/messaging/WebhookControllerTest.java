package win.agus4the.rbm.simulator.controller.messaging;

import win.agus4the.rbm.simulator.model.communications.WebhookConfig;
import win.agus4the.rbm.simulator.service.communications.WebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class WebhookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WebhookService webhookService;

    @Test
    void registersWebhookWithoutToken() {
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

    @Test
    void registersWebhookWithToken() {
        webTestClient.post()
                .uri("/v1/webhooks")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"agentId\":\"agent-2\",\"webhookUrl\":\"http://example.com/hook\",\"clientToken\":\"s3cr3t\"}")
                .exchange()
                .expectStatus().isOk();

        webhookService.getConfig("agent-2")
                .as(StepVerifier::create)
                .assertNext(cfg -> {
                    assertThat(cfg.webhookUrl()).isEqualTo("http://example.com/hook");
                    assertThat(cfg.clientToken()).isEqualTo("s3cr3t");
                })
                .verifyComplete();
    }
}

