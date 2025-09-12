package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.service.BusinessMessagingService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@SuppressFBWarnings("ALL")
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
