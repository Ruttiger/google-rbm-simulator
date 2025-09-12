package com.messi.rbm.simulator.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores webhook registrations and delivers callbacks asynchronously.
 */
@Service
@SuppressFBWarnings("ALL")
public class WebhookService {

    private final Map<String, String> webhooks = new ConcurrentHashMap<>();
    private final WebClient webClient;

    public WebhookService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    /**
     * Register a webhook URL for an agent.
     *
     * @param agentId    RBM agent identifier.
     * @param webhookUrl callback URL.
     */
    public void register(String agentId, String webhookUrl) {
        if (agentId != null && webhookUrl != null) {
            webhooks.put(agentId, webhookUrl);
        }
    }

    /**
     * Retrieve the webhook URL for an agent.
     *
     * @param agentId RBM agent identifier.
     * @return Mono emitting the webhook URL if present.
     */
    public Mono<String> getWebhook(String agentId) {
        return Mono.justOrEmpty(webhooks.get(agentId));
    }

    /**
     * Send a callback to the agent's webhook if registered.
     * Errors are ignored to keep simulation simple.
     *
     * @param agentId agent identifier
     * @param payload body to deliver
     * @return Mono signalling completion
     */
    public Mono<Void> sendCallback(String agentId, Object payload) {
        String url = webhooks.get(agentId);
        if (url == null) {
            return Mono.empty();
        }
        return webClient.post()
                .uri(url)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(e -> Mono.empty());
    }
}

