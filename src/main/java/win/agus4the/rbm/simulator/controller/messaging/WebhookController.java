package win.agus4the.rbm.simulator.controller.messaging;

import win.agus4the.rbm.simulator.service.communications.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/v1/webhooks")
    public Mono<ResponseEntity<Void>> register(@RequestBody Map<String, String> body) {
        String agentId = body.get("agentId");
        String webhookUrl = body.get("webhookUrl");
        if (agentId == null || webhookUrl == null) {
            log.warn("Webhook registration failed: missing agentId or webhookUrl");
            return Mono.just(ResponseEntity.badRequest().build());
        }
        // Auxiliary registration without token or verification.
        webhookService.register(agentId, webhookUrl, null);
        log.info("Registered webhook for agent {} at {}", agentId, webhookUrl);
        return Mono.just(ResponseEntity.ok().build());
    }
}

