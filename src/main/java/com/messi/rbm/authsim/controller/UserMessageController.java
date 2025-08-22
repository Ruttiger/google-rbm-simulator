package com.messi.rbm.authsim.controller;

import com.messi.rbm.authsim.model.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@RestController
public class UserMessageController {

    @PostMapping("/v1/phones/{msisdn}/messages")
    public Mono<ResponseEntity<Map<String, Object>>> receiveUserMessage(
            @PathVariable String msisdn,
            @RequestParam String agentId,
            @RequestBody Message message) {
        Map<String, Object> response = Map.of(
                "status", "received",
                "msisdn", msisdn,
                "agentId", agentId,
                "messageId", message.messageId(),
                "originalText", message.text(),
                "timestamp", Instant.now().toString()
        );
        return Mono.just(ResponseEntity.ok(response));
    }
}
