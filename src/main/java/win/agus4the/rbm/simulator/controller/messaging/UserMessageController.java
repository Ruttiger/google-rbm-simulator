package win.agus4the.rbm.simulator.controller.messaging;

import win.agus4the.rbm.simulator.model.messaging.Message;
import win.agus4the.rbm.simulator.service.communications.WebhookDispatcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserMessageController {

    private final WebhookDispatcherService dispatcherService;
    private static final Logger log = LoggerFactory.getLogger(UserMessageController.class);

    public UserMessageController(WebhookDispatcherService dispatcherService) {
        this.dispatcherService = dispatcherService;
    }

    @PostMapping("/v1/phones/{msisdn}/messages")
    public Mono<ResponseEntity<Map<String, String>>> userMessage(@PathVariable String msisdn,
                                                                 @RequestParam String agentId,
                                                                 @RequestBody Message message) {
        log.info("User message received msisdn={} agentId={}", msisdn, agentId);
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
                .doOnSubscribe(sub -> log.debug("Dispatching USER_MESSAGE event agentId={} msisdn={}", agentId, msisdn))
                .thenReturn(ResponseEntity.ok(Map.of("name", "phones/" + msisdn + "/messages/" + id)));
    }
}
