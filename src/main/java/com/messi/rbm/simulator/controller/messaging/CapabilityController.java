package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.service.BusinessMessagingService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@SuppressFBWarnings("ALL")
public class CapabilityController {

    private final BusinessMessagingService messagingService;

    public CapabilityController(BusinessMessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @GetMapping("/v1/phones/{msisdn}/capabilities")
    public Mono<ResponseEntity<Map<String, Object>>> getCapabilities(@PathVariable String msisdn,
                                                                     @RequestParam String agentId) {
        return messagingService.capabilities(agentId, msisdn)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping("/v1/phones/{msisdn}/capability:requestCapabilityCallback")
    public Mono<Map<String, String>> requestCallback(@PathVariable String msisdn, @RequestParam String agentId) {
        return Mono.just(Map.of("status", "requested"));
    }
}
