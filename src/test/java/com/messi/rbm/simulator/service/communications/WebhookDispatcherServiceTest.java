package com.messi.rbm.simulator.service.communications;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WebhookDispatcherServiceTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void signsPayloadWithToken() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setResponseCode(200));
            server.start();

            WebhookService service = new WebhookService(WebClient.builder());
            service.register("ag", server.url("/").toString(), "token");

            WebhookDispatcherService dispatcher = new WebhookDispatcherService(service, WebClient.builder(), mapper);
            Map<String, Object> event = Map.of("senderPhoneNumber", "1", "eventType", "READ", "eventId", "e", "messageId", "m", "agentId", "ag");
            dispatcher.dispatchEvent("ag", event).block();

            RecordedRequest req = server.takeRequest();
            String body = req.getBody().readUtf8();

            JsonNode json = mapper.readTree(body);
            String data = json.get("message").get("data").asText();
            assertThat(data).isNotEmpty();

            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec("token".getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            String expectedSig = Base64.getEncoder().encodeToString(mac.doFinal(body.getBytes(StandardCharsets.UTF_8)));
            assertThat(req.getHeader("X-Goog-Signature")).isEqualTo(expectedSig);

            byte[] decoded = Base64.getDecoder().decode(data);
            JsonNode inner = mapper.readTree(decoded);
            assertThat(inner.get("senderPhoneNumber").asText()).isEqualTo("1");
        }
    }

    @Test
    void sendsRawPayloadWhenNoToken() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setResponseCode(200));
            server.start();

            WebhookService service = new WebhookService(WebClient.builder());
            service.register("ag", server.url("/").toString(), null);
            WebhookDispatcherService dispatcher = new WebhookDispatcherService(service, WebClient.builder(), mapper);
            Map<String, Object> event = Map.of("senderPhoneNumber", "1");
            dispatcher.dispatchEvent("ag", event).block();

            RecordedRequest req = server.takeRequest();
            assertThat(req.getHeader("X-Goog-Signature")).isNull();
            JsonNode json = mapper.readTree(req.getBody().readUtf8());
            assertThat(json.get("senderPhoneNumber").asText()).isEqualTo("1");
        }
    }
}

