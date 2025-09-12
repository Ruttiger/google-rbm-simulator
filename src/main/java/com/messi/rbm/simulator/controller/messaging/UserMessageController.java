package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.model.Message;
import com.messi.rbm.simulator.service.WebhookService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
@SuppressFBWarnings("ALL")
public class UserMessageController {

    private final WebhookService webhookService;

    public UserMessageController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/v1/phones/{msisdn}/messages")
    public Mono<ResponseEntity<Map<String, String>>> userMessage(@PathVariable String msisdn,
                                                                 @RequestParam String agentId,
                                                                 @RequestBody Message message) {
        Map<String, Object> payload = Map.of(
                "event", "USER_MESSAGE",
                "msisdn", msisdn,
                "text", message.contentMessage() != null ? message.contentMessage().text() : null
        );
        String id = UUID.randomUUID().toString();
        return webhookService.sendCallback(agentId, payload)
                .thenReturn(ResponseEntity.ok(Map.of("name", "phones/" + msisdn + "/messages/" + id)));
    }
}
