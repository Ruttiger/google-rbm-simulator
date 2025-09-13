package com.messi.rbm.simulator.service;

import com.messi.rbm.simulator.model.WebhookConfig;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores webhook registrations and performs verification challenges.
 */
@Service
@SuppressFBWarnings("ALL")
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final Map<String, WebhookConfig> webhooks = new ConcurrentHashMap<>();
    private final WebClient webClient;

    public WebhookService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    /**
     * Register a webhook URL and optional client token for an agent without verification.
     * Used by the auxiliary /v1/webhooks endpoint.
     */
    public void register(String agentId, String webhookUrl, String clientToken) {
        if (agentId != null && webhookUrl != null) {
            webhooks.put(agentId, new WebhookConfig(webhookUrl, clientToken));
        }
    }

    /**
     * Retrieve webhook configuration for an agent.
     */
    public Mono<WebhookConfig> getConfig(String agentId) {
        return Mono.justOrEmpty(webhooks.get(agentId));
    }

    /**
     * Verify the provided webhook by issuing a challenge request. The webhook is
     * stored only if the response echoes the secret correctly.
     *
     * @return Mono emitting {@code true} if verification succeeded.
     */
    public Mono<Boolean> verifyAndRegister(String agentId, String webhookUrl, String clientToken) {
        if (agentId == null || webhookUrl == null || clientToken == null) {
            return Mono.just(false);
        }
        String secret = UUID.randomUUID().toString();
        return webClient.post()
                .uri(webhookUrl)
                .bodyValue(Map.of("clientToken", clientToken, "secret", secret))
                .retrieve()
                .bodyToMono(String.class)
                .map(resp -> resp.contains("\"secret\":\"" + secret + "\""))
                .onErrorReturn(false)
                .doOnNext(success -> {
                    if (Boolean.TRUE.equals(success)) {
                        webhooks.put(agentId, new WebhookConfig(webhookUrl, clientToken));
                        log.info("Webhook verified for agent {}", agentId);
                    } else {
                        log.warn("Webhook verification failed for agent {}", agentId);
                    }
                });
    }
}

