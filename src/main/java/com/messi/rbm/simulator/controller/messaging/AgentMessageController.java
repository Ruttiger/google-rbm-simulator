package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.model.Message;
import com.messi.rbm.simulator.service.BusinessMessagingService;
import com.messi.rbm.simulator.service.WebhookDispatcherService;
import com.messi.rbm.simulator.service.WebhookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Endpoint for sending agent messages to user phones in the simulator.
 */
@RestController
public class AgentMessageController {

    private final WebhookDispatcherService dispatcherService;
    private final BusinessMessagingService messagingService;
    private final WebhookService webhookService;

    private static final Pattern EVENT_PATTERN =
            Pattern.compile("#(READ|DELIVERED|REVOKED)(?:\\(delay=(\\d+)\\))?");

    public AgentMessageController(
            WebhookDispatcherService dispatcherService,
            BusinessMessagingService messagingService,
            WebhookService webhookService) {
        this.dispatcherService = dispatcherService;
        this.messagingService = messagingService;
        this.webhookService = webhookService;
    }

    @PostMapping("/v1/phones/{msisdn}/agentMessages")
    public Mono<ResponseEntity<Map<String, Object>>> receiveMessage(
            @PathVariable String msisdn,
            @RequestParam String agentId,
            @RequestParam String messageId,
            @Valid @RequestBody Message message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("name", "phones/" + msisdn + "/agentMessages/" + messageId);
        response.put("sendTime", Instant.now().toString());
        response.put("agentId", agentId);
        if (message.contentMessage() != null) {
            response.put("contentMessage", message.contentMessage());
        }
        return messagingService.saveAgentMessage(msisdn, messageId, message)
                .then(Mono.just(ResponseEntity.ok(response)))
                  .doOnSuccess(resp -> handleTriggers(agentId, msisdn, messageId, message));
    }

    @GetMapping("/v1/phones/{msisdn}/agentMessages/{messageId}")
    public Mono<ResponseEntity<Message>> getMessage(@PathVariable String msisdn, @PathVariable String messageId) {
        return messagingService.getAgentMessage(msisdn, messageId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/v1/phones/{msisdn}/agentMessages/{messageId}")
    public Mono<ResponseEntity<Void>> deleteMessage(@PathVariable String msisdn, @PathVariable String messageId) {
        return messagingService.deleteAgentMessage(msisdn, messageId)
                .thenReturn(ResponseEntity.noContent().build());
    }

      private void handleTriggers(String agentId, String msisdn, String messageId, Message message) {
        if (message.contentMessage() == null) {
            return;
        }
        String text = message.contentMessage().text();
        if (text == null) {
            return;
        }
        webhookService.getConfig(agentId).subscribe(cfg -> {
            if (text.startsWith("#USER:")) {
                String userText = text.substring(6);
                Map<String, Object> payload = new LinkedHashMap<>();
                payload.put("senderPhoneNumber", msisdn);
                payload.put("eventType", "USER_MESSAGE");
                payload.put("eventId", java.util.UUID.randomUUID().toString());
                payload.put("agentId", agentId);
                payload.put("text", userText);
                dispatcherService.dispatchEvent(agentId, payload).subscribe();
            }

            Matcher matcher = EVENT_PATTERN.matcher(text);
            while (matcher.find()) {
                String type = matcher.group(1);
                String delayGroup = matcher.group(2);
                long delay = delayGroup != null ? Long.parseLong(delayGroup) : 0L;
                scheduleEvent(agentId, msisdn, messageId, type, delay);
            }

            if (text.contains("#IS_TYPING")) {
                dispatcherService.dispatchEvent(agentId, eventMap("IS_TYPING", msisdn, messageId, agentId)).subscribe();
            }
            if (text.contains("#SUBSCRIBE")) {
                dispatcherService.dispatchEvent(agentId, eventMap("SUBSCRIBE", msisdn, messageId, agentId))
                        .subscribe();
            }
            if (text.contains("#UNSUBSCRIBE")) {
                dispatcherService.dispatchEvent(
                                agentId, eventMap("UNSUBSCRIBE", msisdn, messageId, agentId))
                        .subscribe();
            }
        });
    }

      private void scheduleEvent(String agentId, String msisdn, String messageId, String type, long delay) {
          Mono.delay(Duration.ofMillis(delay))
                  .then(dispatcherService.dispatchEvent(agentId, eventMap(type, msisdn, messageId, agentId)))
                  .subscribe();
      }

      private Map<String, Object> eventMap(String type, String msisdn, String messageId, String agentId) {
          Map<String, Object> map = new LinkedHashMap<>();
          map.put("senderPhoneNumber", msisdn);
          map.put("eventType", type);
          map.put("eventId", java.util.UUID.randomUUID().toString());
          map.put("agentId", agentId);
          if (messageId != null) {
              map.put("messageId", messageId);
          }
          return map;
      }
}

