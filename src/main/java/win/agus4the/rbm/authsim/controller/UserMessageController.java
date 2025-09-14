package win.agus4the.rbm.authsim.controller;

import win.agus4the.rbm.authsim.model.Message;
import jakarta.validation.Valid;
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
 * Receives user messages sent to the auth simulator.
 */
@RestController
public class UserMessageController {

    @PostMapping("/v1/phones/{msisdn}/messages")
    public Mono<ResponseEntity<Map<String, Object>>> receiveUserMessage(
            @PathVariable final String msisdn,
            @RequestParam final String agentId,
            @Valid @RequestBody final Message message) {
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
