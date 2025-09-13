package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.model.Message;
import com.messi.rbm.simulator.service.WebhookDispatcherService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@SuppressFBWarnings("ALL")
public class UserMessageController {

    private final WebhookDispatcherService dispatcherService;

    public UserMessageController(WebhookDispatcherService dispatcherService) {
        this.dispatcherService = dispatcherService;
    }

    @PostMapping("/v1/phones/{msisdn}/messages")
    public Mono<ResponseEntity<Map<String, String>>> userMessage(@PathVariable String msisdn,
                                                                 @RequestParam String agentId,
                                                                 @RequestBody Message message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("senderPhoneNumber", msisdn);
        payload.put("eventType", "USER_MESSAGE");
        payload.put("eventId", UUID.randomUUID().toString());
        payload.put("agentId", agentId);
        if (message.contentMessage() != null && message.contentMessage().text() != null) {
            payload.put("text", message.contentMessage().text());
        }
        String id = UUID.randomUUID().toString();
        return dispatcherService.dispatchEvent(agentId, payload)
                .thenReturn(ResponseEntity.ok(Map.of("name", "phones/" + msisdn + "/messages/" + id)));
    }
}
