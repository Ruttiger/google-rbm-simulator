package win.agus4the.rbm.authsim.controller;

import win.agus4the.rbm.authsim.model.AgentEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

/**
 * Handles callbacks of agent events received in the auth simulator.
 */
@RestController
public class AgentEventController {

    @PostMapping("/v1/phones/{msisdn}/agentEvents")
    public Mono<ResponseEntity<Map<String, Object>>> receiveEvent(
            @PathVariable final String msisdn,
            @RequestParam final String agentId,
            @RequestBody final AgentEvent event) {
        Map<String, Object> response = Map.of(
                "status", "received",
                "msisdn", msisdn,
                "agentId", agentId,
                "eventId", event.eventId(),
                "eventType", event.eventType(),
                "messageId", event.messageId(),
                "timestamp", Instant.now().toString()
        );
        return Mono.just(ResponseEntity.ok(response));
    }
}
