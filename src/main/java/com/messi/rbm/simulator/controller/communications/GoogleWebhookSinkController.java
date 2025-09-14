package com.messi.rbm.simulator.controller.communications;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Auxiliary endpoint that echoes verification challenges and acts as a sink for other events.
 */
@RestController
public class GoogleWebhookSinkController {

    @PostMapping("/webhook/google/{agentId}")
    public Mono<ResponseEntity<?>> receive(
            @PathVariable String agentId,
            @RequestBody(required = false) Map<String, Object> body) {
        if (body != null && body.containsKey("secret")) {
            Object secret = body.get("secret");
            return Mono.just(ResponseEntity.ok(Map.of("secret", secret)));
        }
        return Mono.just(ResponseEntity.ok().build());
    }
}
