package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.service.WebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Allows manual registration of webhook callbacks for agents.
 */
@RestController
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/v1/webhooks")
    public Mono<ResponseEntity<Void>> register(@RequestBody Map<String, String> body) {
        String agentId = body.get("agentId");
        String webhookUrl = body.get("webhookUrl");
        if (agentId == null || webhookUrl == null) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        // Auxiliary registration without token or verification.
        webhookService.register(agentId, webhookUrl, null);
        return Mono.just(ResponseEntity.ok().build());
    }
}

