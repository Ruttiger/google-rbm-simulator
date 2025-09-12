package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.model.Message;
import com.messi.rbm.simulator.service.WebhookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Endpoint for sending agent messages to user phones in the simulator.
 */
@RestController
public class AgentMessageController {

    private final WebhookService webhookService;

    public AgentMessageController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/v1/phones/{msisdn}/agentMessages")
    public Mono<ResponseEntity<Map<String, Object>>> receiveMessage(
            @PathVariable String msisdn,
            @RequestParam String agentId,
            @RequestParam String messageId,
            @Valid @RequestBody Message message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("name", "phones/" + msisdn + "/agentMessages/" + messageId);
        response.put("sendTime", Instant.now().toString());
        response.put("agentId", agentId);
        if (message.contentMessage() != null) {
            response.put("contentMessage", message.contentMessage());
        }
        return Mono.just(ResponseEntity.ok(response))
                .doOnSuccess(resp -> handleTriggers(agentId, msisdn, message));
    }

    private void handleTriggers(String agentId, String msisdn, Message message) {
        if (message.contentMessage() == null) {
            return;
        }
        String text = message.contentMessage().text();
        if (text == null) {
            return;
        }
        if (text.startsWith("#USER:")) {
            String userText = text.substring(6);
            webhookService.sendCallback(agentId, Map.of(
                    "event", "USER_MESSAGE",
                    "msisdn", msisdn,
                    "text", userText
            )).subscribe();
        }
        if (text.contains("#READ")) {
            webhookService.sendCallback(agentId, eventMap("READ", msisdn)).subscribe();
        }
        if (text.contains("#DELIVERED")) {
            webhookService.sendCallback(agentId, eventMap("DELIVERED", msisdn)).subscribe();
        }
        if (text.contains("#IS_TYPING")) {
            webhookService.sendCallback(agentId, eventMap("IS_TYPING", msisdn)).subscribe();
        }
        if (text.contains("#SUBSCRIBE")) {
            webhookService.sendCallback(agentId, eventMap("SUBSCRIBE", msisdn)).subscribe();
        }
        if (text.contains("#UNSUBSCRIBE")) {
            webhookService.sendCallback(agentId, eventMap("UNSUBSCRIBE", msisdn)).subscribe();
        }
    }

    private Map<String, Object> eventMap(String type, String msisdn) {
        return Map.of("event", type, "msisdn", msisdn);
    }
}

