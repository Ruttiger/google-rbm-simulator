package win.agus4the.rbm.simulator.controller.messaging;

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

import java.util.Map;

@RestController
public class AgentEventController {

    private final WebhookDispatcherService dispatcherService;
    private static final Logger log = LoggerFactory.getLogger(AgentEventController.class);

    public AgentEventController(WebhookDispatcherService dispatcherService) {
        this.dispatcherService = dispatcherService;
    }

    @PostMapping("/v1/phones/{msisdn}/agentEvents")
    public Mono<ResponseEntity<Void>> sendEvent(@PathVariable String msisdn,
                                                @RequestParam String agentId,
                                                @RequestBody Map<String, Object> event) {
        log.info("Agent event received msisdn={} agentId={} type={}", msisdn, agentId, event.get("event"));
        Map<String, Object> payload = new java.util.LinkedHashMap<>();
        payload.put("senderPhoneNumber", msisdn);
        payload.put("eventType", event.getOrDefault("event", "UNKNOWN"));
        payload.put("eventId", java.util.UUID.randomUUID().toString());
        payload.put("agentId", agentId);
        if (event.get("messageId") != null) {
            payload.put("messageId", event.get("messageId"));
        }
        return dispatcherService.dispatchEvent(agentId, payload)
                .doOnSubscribe(sub -> log.debug("Dispatching agent event agentId={} msisdn={}", agentId, msisdn))
                .thenReturn(ResponseEntity.ok().build());
    }
}
