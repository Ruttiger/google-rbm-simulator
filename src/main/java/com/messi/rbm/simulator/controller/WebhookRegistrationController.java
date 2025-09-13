package com.messi.rbm.simulator.controller;

import com.messi.rbm.simulator.model.WebhookConfig;
import com.messi.rbm.simulator.service.WebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Business Communications endpoint for webhook registration with challenge verification.
 */
@RestController
@RequestMapping("/v1/brands/{brandId}/agents/{agentId}/webhooks")
public class WebhookRegistrationController {

    private final WebhookService webhookService;

    public WebhookRegistrationController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> register(@PathVariable String agentId,
                                               @RequestBody WebhookConfig config) {
        return webhookService.verifyAndRegister(agentId, config.webhookUrl(), config.clientToken())
                .map(success -> success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build());
    }
}

