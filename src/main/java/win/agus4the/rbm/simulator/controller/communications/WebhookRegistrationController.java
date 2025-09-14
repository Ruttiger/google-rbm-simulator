package win.agus4the.rbm.simulator.controller.communications;

import win.agus4the.rbm.simulator.model.communications.WebhookConfig;
import win.agus4the.rbm.simulator.service.communications.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(WebhookRegistrationController.class);

    public WebhookRegistrationController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> register(@PathVariable String agentId,
                                               @RequestBody WebhookConfig config) {
        log.info("Webhook verification requested agentId={} url={}", agentId, config.webhookUrl());
        return webhookService.verifyAndRegister(agentId, config.webhookUrl(), config.clientToken())
                .map(success -> {
                    if (success) {
                        log.info("Webhook verification succeeded agentId={}", agentId);
                        return ResponseEntity.ok().build();
                    }
                    log.warn("Webhook verification failed agentId={}", agentId);
                    return ResponseEntity.badRequest().build();
                });
    }
}

