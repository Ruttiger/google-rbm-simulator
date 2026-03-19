package win.agus4the.rbm.simulator.service.communications;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import win.agus4the.rbm.simulator.model.communications.WebhookConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Dependencies are injected and not exposed")
public class WebhookDispatcherService {

    private final WebhookService webhookService;
    private final WebClient webClient;
    private final ObjectMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(WebhookDispatcherService.class);

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
                .doOnNext(cfg -> log.info(
                        "Dispatching event type={} agentId={} to {}",
                        event.get("eventType"), agentId, cfg.webhookUrl()))
                .flatMap(cfg -> send(cfg, event))
                .onErrorResume(e -> {
                    log.warn("Failed to dispatch event agentId={} error={}", agentId, e.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<Void> send(WebhookConfig config, Map<String, Object> event) {
        if (config.clientToken() == null) {
            // Auxiliary webhook: send raw JSON without signature
            return webClient.post()
                    .uri(config.webhookUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(event)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .doOnSuccess(v -> log.debug("Dispatched raw event agentId={}", config.webhookUrl()));
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
                    .bodyToMono(Void.class)
                    .doOnSuccess(v -> log.debug(
                            "Dispatched signed event agentId={} signaturePresent=true",
                            config.webhookUrl()));
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize event for agentId={}", config.webhookUrl(), e);
            return Mono.empty();
        }
    }

    private String sign(byte[] data, String token) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(token.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] raw = mac.doFinal(data);
            String sig = Base64.getEncoder().encodeToString(raw);
            log.debug("Generated signature for agent webhook");
            return sig;
        } catch (java.security.NoSuchAlgorithmException | java.security.InvalidKeyException e) {
            log.warn("Signature generation failed", e);
            return "";
        }
    }
}

