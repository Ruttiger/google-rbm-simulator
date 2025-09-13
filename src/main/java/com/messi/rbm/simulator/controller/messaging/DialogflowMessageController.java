package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.service.BusinessMessagingService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Dependencies are injected and not exposed")
public class DialogflowMessageController {

    private final BusinessMessagingService messagingService;

    public DialogflowMessageController(BusinessMessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @PostMapping("/v1/phones/{msisdn}/dialogflowMessages")
    public Mono<ResponseEntity<Map<String, String>>> dialogflow(@PathVariable String msisdn,
                                                                @RequestParam String agentId,
                                                                @RequestBody Map<String, Object> body) {
        if (!messagingService.isTester(agentId, msisdn)) {
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
        }
        return Mono.just(ResponseEntity.ok(Map.of("status", "DELIVERED")));
    }
}
