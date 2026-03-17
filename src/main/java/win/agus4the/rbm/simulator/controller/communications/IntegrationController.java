package win.agus4the.rbm.simulator.controller.communications;

import win.agus4the.rbm.simulator.model.communications.Integration;
import win.agus4the.rbm.simulator.service.communications.IntegrationService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import java.util.Map;

/**
 * Controller for integration operations.
 */
@RestController
@RequestMapping("/v1/brands/{brandId}/agents/{agentId}/integrations")
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Dependencies are injected and not exposed")
public class IntegrationController {

    private final IntegrationService integrationService;
    private static final Logger log = LoggerFactory.getLogger(IntegrationController.class);

    public IntegrationController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @PostMapping
    public ResponseEntity<Integration> create(@PathVariable String brandId, @PathVariable String agentId,
                                              @Valid @RequestBody Integration req) {
        return integrationService.create(brandId, agentId, req)
                .map(integration -> {
                    log.info("Integration created brand={} agent={} id={}", brandId, agentId, integration.getName());
                    return ResponseEntity.ok(integration);
                })
                .orElseGet(() -> {
                    log.warn("Failed to create integration brand={} agent={}", brandId, agentId);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping
    public Map<String, Object> list(@PathVariable String brandId, @PathVariable String agentId) {
        List<Integration> list = integrationService.list(brandId, agentId);
        log.debug("Listing integrations brand={} agent={} count={}", brandId, agentId, list.size());
        return Map.of("integrations", list);
    }

    @GetMapping("/{integrationId}")
    public ResponseEntity<Integration> get(@PathVariable String brandId, @PathVariable String agentId,
                                           @PathVariable String integrationId) {
        return integrationService.get(brandId, agentId, integrationId)
                .map(integration -> {
                    log.info("Retrieved integration brand={} agent={} id={}", brandId, agentId, integrationId);
                    return ResponseEntity.ok(integration);
                })
                .orElseGet(() -> {
                    log.warn("Integration not found brand={} agent={} id={}", brandId, agentId, integrationId);
                    return ResponseEntity.notFound().build();
                });
    }

    @PatchMapping("/{integrationId}")
    public ResponseEntity<Integration> patch(
            @PathVariable String brandId,
            @PathVariable String agentId,
            @PathVariable String integrationId,
            @RequestBody Map<String, Object> patch) {
        return integrationService.patch(brandId, agentId, integrationId, patch)
                .map(integration -> {
                    log.info(
                            "Patched integration brand={} agent={} id={} keys={}",
                            brandId,
                            agentId,
                            integrationId,
                            patch.keySet());
                    return ResponseEntity.ok(integration);
                })
                .orElseGet(() -> {
                    log.warn(
                            "Integration not found for patch brand={} agent={} id={}",
                            brandId,
                            agentId,
                            integrationId);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{integrationId}")
    public ResponseEntity<Void> delete(@PathVariable String brandId, @PathVariable String agentId,
                                       @PathVariable String integrationId) {
        boolean deleted = integrationService.delete(brandId, agentId, integrationId);
        if (deleted) {
            log.info("Deleted integration brand={} agent={} id={}", brandId, agentId, integrationId);
            return ResponseEntity.noContent().build();
        }
        log.warn("Integration not found for delete brand={} agent={} id={}", brandId, agentId, integrationId);
        return ResponseEntity.notFound().build();
    }
}
