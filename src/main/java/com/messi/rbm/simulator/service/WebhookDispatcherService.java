package com.messi.rbm.simulator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.messi.rbm.simulator.model.WebhookConfig;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * Dispatches webhook events, handling Pub/Sub wrapping and signature generation.
 */
@Service
@SuppressFBWarnings("ALL")
public class WebhookDispatcherService {

    private final WebhookService webhookService;
    private final WebClient webClient;
    private final ObjectMapper mapper;

    public WebhookDispatcherService(WebhookService webhookService, WebClient.Builder builder, ObjectMapper mapper) {
        this.webhookService = webhookService;
        this.webClient = builder.build();
        this.mapper = mapper;
    }

    /**
     * Dispatch an event to the registered webhook for the given agent.
     * If a client token is present the payload is wrapped in a Pub/Sub envelope
     * and signed using HMAC-SHA512.
     */
    public Mono<Void> dispatchEvent(String agentId, Map<String, Object> event) {
        return webhookService.getConfig(agentId)
                .flatMap(cfg -> send(cfg, event))
                .onErrorResume(e -> Mono.empty());
    }

    private Mono<Void> send(WebhookConfig config, Map<String, Object> event) {
        if (config.clientToken() == null) {
            // Auxiliary webhook: send raw JSON without signature
            return webClient.post()
                    .uri(config.webhookUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(event)
                    .retrieve()
                    .bodyToMono(Void.class);
        }
        try {
            String inner = mapper.writeValueAsString(event);
            byte[] innerBytes = inner.getBytes(StandardCharsets.UTF_8);
            String base64 = Base64.getEncoder().encodeToString(innerBytes);
            Map<String, Object> wrapper = Map.of(
                    "message", Map.of(
                            "data", base64,
                            "messageId", UUID.randomUUID().toString(),
                            "publishTime", Instant.now().toString()
                    )
            );
            String envelope = mapper.writeValueAsString(wrapper);
            String signature = sign(envelope.getBytes(StandardCharsets.UTF_8), config.clientToken());
            return webClient.post()
                    .uri(config.webhookUrl())
                    .header("X-Goog-Signature", signature)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(envelope)
                    .retrieve()
                    .bodyToMono(Void.class);
        } catch (JsonProcessingException e) {
            return Mono.empty();
        }
    }

    private String sign(byte[] data, String token) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(token.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] raw = mac.doFinal(data);
            return Base64.getEncoder().encodeToString(raw);
        } catch (Exception e) {
            return "";
        }
    }
}

