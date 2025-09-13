package com.messi.rbm.simulator.controller.messaging;

import com.messi.rbm.simulator.service.BusinessMessagingService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Dependencies are injected and not exposed")
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
