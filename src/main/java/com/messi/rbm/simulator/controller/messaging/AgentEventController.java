package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.service.WebhookDispatcherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class AgentEventController {

    private final WebhookDispatcherService dispatcherService;

    public AgentEventController(WebhookDispatcherService dispatcherService) {
        this.dispatcherService = dispatcherService;
    }

    @PostMapping("/v1/phones/{msisdn}/agentEvents")
    public Mono<ResponseEntity<Void>> sendEvent(@PathVariable String msisdn,
                                                @RequestParam String agentId,
                                                @RequestBody Map<String, Object> event) {
        Map<String, Object> payload = new java.util.LinkedHashMap<>();
        payload.put("senderPhoneNumber", msisdn);
        payload.put("eventType", event.getOrDefault("event", "UNKNOWN"));
        payload.put("eventId", java.util.UUID.randomUUID().toString());
        payload.put("agentId", agentId);
        if (event.get("messageId") != null) {
            payload.put("messageId", event.get("messageId"));
        }
        return dispatcherService.dispatchEvent(agentId, payload)
                .thenReturn(ResponseEntity.ok().build());
    }
}
