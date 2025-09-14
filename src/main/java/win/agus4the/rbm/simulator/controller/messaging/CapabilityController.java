package win.agus4the.rbm.simulator.controller.messaging;

import win.agus4the.rbm.simulator.service.messaging.BusinessMessagingService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(CapabilityController.class);

    public CapabilityController(BusinessMessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @GetMapping("/v1/phones/{msisdn}/capabilities")
    public Mono<ResponseEntity<Map<String, Object>>> getCapabilities(@PathVariable String msisdn,
                                                                     @RequestParam String agentId) {
        log.info("Capabilities requested for msisdn={} agentId={}", msisdn, agentId);
        return messagingService.capabilities(agentId, msisdn)
                .map(body -> {
                    log.debug("Capabilities found for msisdn={} agentId={}", msisdn, agentId);
                    return ResponseEntity.ok(body);
                })
                .switchIfEmpty(Mono.fromSupplier(() -> {
                    log.warn("Capabilities not found for msisdn={} agentId={}", msisdn, agentId);
                    return ResponseEntity.notFound().build();
                }));
    }

    @PostMapping("/v1/phones/{msisdn}/capability:requestCapabilityCallback")
    public Mono<Map<String, String>> requestCallback(@PathVariable String msisdn, @RequestParam String agentId) {
        log.info("Capability callback requested msisdn={} agentId={}", msisdn, agentId);
        return Mono.just(Map.of("status", "requested"));
    }
}
