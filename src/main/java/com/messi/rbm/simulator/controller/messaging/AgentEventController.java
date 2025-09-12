package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.service.WebhookService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@SuppressFBWarnings("ALL")
public class AgentEventController {

    private final WebhookService webhookService;

    public AgentEventController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/v1/phones/{msisdn}/agentEvents")
    public Mono<ResponseEntity<Void>> sendEvent(@PathVariable String msisdn,
                                                @RequestParam String agentId,
                                                @RequestBody Map<String, Object> event) {
        Map<String, Object> payload = Map.of(
                "event", event.getOrDefault("event", "UNKNOWN"),
                "msisdn", msisdn
        );
        return webhookService.sendCallback(agentId, payload)
                .thenReturn(ResponseEntity.ok().build());
    }
}
