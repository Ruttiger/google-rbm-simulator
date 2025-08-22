package com.messi.rbm.simulator.controller;

import com.messi.rbm.simulator.model.ForceState;
import com.messi.rbm.simulator.model.Message;
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

@RestController
public class AgentMessageController {

    @PostMapping("/v1/phones/{msisdn}/agentMessages")
    public Mono<ResponseEntity<Map<String, Object>>> receiveMessage(
            @PathVariable String msisdn,
            @RequestParam String agentId,
            @RequestParam(required = false) ForceState forceState,
            @RequestParam(required = false, defaultValue = "false") boolean echo,
            @RequestBody Message message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "received");
        response.put("msisdn", msisdn);
        response.put("agentId", agentId);
        response.put("messageId", message.messageId());
        response.put("originalText", message.text());
        response.put("timestamp", Instant.now().toString());
        if (forceState != null) {
            response.put("forceState", forceState.name());
        }
        if (echo) {
            response.put("echo", message);
        }
        return Mono.just(ResponseEntity.ok(response));
    }
}
