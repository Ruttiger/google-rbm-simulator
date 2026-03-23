package win.agus4the.rbm.simulator.controller.osp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "maap.simulator.enabled-interfaces=OSP",
        "sim.osp.client-id=orange-client",
        "sim.osp.client-secret=orange-secret",
        "sim.osp.token-ttl-seconds=1",
        "sim.osp.default-scope=osp.send"
})
@AutoConfigureWebTestClient
class OspControllerIntegrationTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void rejectsInvalidBasicAuthForToken() {
        webTestClient.post().uri("/v3/auth/")
                .header(HttpHeaders.AUTHORIZATION, basicAuth("wrong", "creds"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=client_credentials")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void issuesTokenAndUsesItForBotMessages() {
        String token = issueToken();

        webTestClient.post().uri("/v3/bot/v1/orange-bot/messages")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"message":{"text":"hola osp"}}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.orangeChatbotId").isEqualTo("orange-bot")
                .jsonPath("$.status").isEqualTo("accepted")
                .jsonPath("$.messageId").isNotEmpty();
    }

    @Test
    void rejectsExpiredToken() throws InterruptedException {
        String token = issueToken();
        Thread.sleep(1200);

        webTestClient.post().uri("/v3/bot/v1/orange-bot/messages")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"message":{"text":"hola osp"}}
                        """)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @ParameterizedTest
    @MethodSource("webhookPayloads")
    void acceptsWebhookNotifications(String expectedType, String payload) {
        webTestClient.post().uri("/webhook/orange/bot123/uuid-123")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("received")
                .jsonPath("$.notificationType").isEqualTo(expectedType);
    }

    private String issueToken() {
        byte[] body = webTestClient.post().uri("/v3/auth/")
                .header(HttpHeaders.AUTHORIZATION, basicAuth("orange-client", "orange-secret"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=client_credentials&scope=osp.send")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token_type").isEqualTo("Bearer")
                .jsonPath("$.scope").isEqualTo("osp.send")
                .jsonPath("$.expires_in").isEqualTo(1)
                .returnResult()
                .getResponseBodyContent();

        assertNotNull(body);
        try {
            JsonNode root = OBJECT_MAPPER.readTree(new String(body, StandardCharsets.UTF_8));
            return root.get("access_token").asText();
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo parsear el token OSP", ex);
        }
    }

    private static Stream<Arguments> webhookPayloads() {
        return Stream.of(
                Arguments.of("message", """
                        {"message":{"type":"TextMessage","text":"Hola"}}
                        """),
                Arguments.of("response", """
                        {"response":{"action":"openUrl"}}
                        """),
                Arguments.of("response", """
                        {"response":{"reply":{"id":"reply-1","text":"Sí"}}}
                        """),
                Arguments.of("messageStatus", """
                        {"messageStatus":{"status":"Delivered"}}
                        """),
                Arguments.of("messageStatus", """
                        {"messageStatus":{"status":"Displayed"}}
                        """),
                Arguments.of("messageStatus", """
                        {"messageStatus":{"status":"Revoke"}}
                        """),
                Arguments.of("messageStatus", """
                        {"messageStatus":{"status":"Revoke_failed"}}
                        """)
        );
    }

    private static String basicAuth(String clientId, String secret) {
        String raw = clientId + ":" + secret;
        return "Basic " + Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }
}
