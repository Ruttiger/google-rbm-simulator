package win.agus4the.rbm.simulator.controller.pcm;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "maap.simulator.enabled-interfaces=PCM",
        "sim.pcm.username=test-user",
        "sim.pcm.password=test-pass"
})
@AutoConfigureWebTestClient
class PcmControllerIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    private MockWebServer server;

    @AfterEach
    void tearDown() throws IOException {
        if (server != null) {
            server.shutdown();
            server = null;
        }
    }

    @Test
    void acceptsSmsTextSubmitWithBasicAuth() {
        webTestClient.post().uri("/restadpt_generico1/smsTextSubmit")
                .header(HttpHeaders.AUTHORIZATION, basicAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"sender":"bot1","recipients":[{"to":"+34600000001"}],"smsText":"hola"}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(1000);
    }

    @Test
    void rejectsInvalidBasicAuth() {
        webTestClient.post().uri("/restadpt_generico1/smsTextSubmit")
                .header(HttpHeaders.AUTHORIZATION, "Basic bad")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"sender":"bot1","recipients":[{"to":"+34600000001"}],"smsText":"hola"}
                        """)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(4001);
    }

    @Test
    void acceptsSmsBinarySubmit() {
        webTestClient.post().uri("/restadpt_generico1/smsBinarySubmit")
                .header(HttpHeaders.AUTHORIZATION, basicAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"sender":"bot1","recipients":[{"to":"+34600000001"}],"smsBinary":"DEADBEEF"}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.statusCode").isEqualTo(1000);
    }

    @Test
    void validatesMissingPayload() {
        webTestClient.post().uri("/restadpt_generico1/smsTextSubmit")
                .header(HttpHeaders.AUTHORIZATION, basicAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"sender":"bot1","recipients":[{"to":"+34600000001"}]}
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("$.statusCode").isEqualTo(4004);
    }

    @Test
    void usesProvisionedDeliveryReportUrlFallback() throws Exception {
        server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse().setResponseCode(200));
        server.start();
        String callback = server.url("/deliveryReport").toString();

        webTestClient.put().uri("/v1/provisioning/pcm/webhooks/bot1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{" +
                        "\"deliveryReportUrl\":\"" + callback + "\"," +
                        "\"smsDeliverUrl\":\"http://localhost/smsDeliver\"}")
                .exchange().expectStatus().isOk();

        webTestClient.post().uri("/restadpt_generico1/smsTextSubmit")
                .header(HttpHeaders.AUTHORIZATION, basicAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"sender":"bot1","recipients":[{"to":"+34600000001"}],"smsText":"#DELIVERED","deliveryReport":"All"}
                        """)
                .exchange().expectStatus().isOk();

        assertNotNull(server.takeRequest(5, TimeUnit.SECONDS));
    }

    private String basicAuth() {
        String value = Base64.getEncoder().encodeToString("test-user:test-pass".getBytes(StandardCharsets.UTF_8));
        return "Basic " + value;
    }
}
