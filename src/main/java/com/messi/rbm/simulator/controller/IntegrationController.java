package com.messi.rbm.simulator.controller;

import com.messi.rbm.simulator.model.Integration;
import com.messi.rbm.simulator.service.IntegrationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller for integration operations.
 */
@RestController
@RequestMapping("/v1/brands/{brandId}/agents/{agentId}/integrations")
public class IntegrationController {

    private final IntegrationService integrationService;

    public IntegrationController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @PostMapping
    public ResponseEntity<Integration> create(@PathVariable String brandId, @PathVariable String agentId,
                                              @Valid @RequestBody Integration req) {
        return integrationService.create(brandId, agentId, req)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Map<String, Object> list(@PathVariable String brandId, @PathVariable String agentId) {
        return Map.of("integrations", integrationService.list(brandId, agentId));
    }

    @GetMapping("/{integrationId}")
    public ResponseEntity<Integration> get(@PathVariable String brandId, @PathVariable String agentId,
                                           @PathVariable String integrationId) {
        return integrationService.get(brandId, agentId, integrationId)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{integrationId}")
    public ResponseEntity<Integration> patch(
            @PathVariable String brandId,
            @PathVariable String agentId,
            @PathVariable String integrationId,
            @RequestBody Map<String, Object> patch) {
        return integrationService.patch(brandId, agentId, integrationId, patch)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{integrationId}")
    public ResponseEntity<Void> delete(@PathVariable String brandId, @PathVariable String agentId,
                                       @PathVariable String integrationId) {
        return integrationService.delete(brandId, agentId, integrationId)
                ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
