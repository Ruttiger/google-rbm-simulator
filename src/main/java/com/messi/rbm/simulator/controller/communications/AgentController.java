package com.messi.rbm.simulator.controller.communications;

import com.messi.rbm.simulator.model.communications.Agent;
import com.messi.rbm.simulator.model.communications.AgentLaunch;
import com.messi.rbm.simulator.model.communications.AgentVerification;
import com.messi.rbm.simulator.service.communications.AgentService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
 * Controller for agent endpoints including verification and launch.
 */
@RestController
@RequestMapping("/v1/brands/{brandId}/agents")
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Dependencies are injected and not exposed")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping
    public ResponseEntity<Agent> create(@PathVariable String brandId, @Valid @RequestBody Agent req) {
        return agentService.create(brandId, req).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Map<String, Object> list(@PathVariable String brandId) {
        return Map.of("agents", agentService.list(brandId), "nextPageToken", "");
    }

    @GetMapping("/{agentId}")
    public ResponseEntity<Agent> get(@PathVariable String brandId, @PathVariable String agentId) {
        return agentService.get(brandId, agentId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{agentId}")
    public ResponseEntity<Agent> patch(@PathVariable String brandId, @PathVariable String agentId,
                                       @RequestBody Map<String, Object> patch) {
        return agentService.patch(brandId, agentId, patch)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{agentId}")
    public ResponseEntity<Void> delete(@PathVariable String brandId, @PathVariable String agentId) {
        return agentService.delete(brandId, agentId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // Verification
    @PostMapping("/{agentId}:requestVerification")
    public ResponseEntity<AgentVerification> requestVerification(@PathVariable String brandId,
                                                                 @PathVariable String agentId) {
        return agentService.requestVerification(brandId, agentId)
                .map(ResponseEntity::ok).orElse(ResponseEntity.status(409).build());
    }

    @GetMapping("/{agentId}/verification")
    public ResponseEntity<AgentVerification> getVerification(
            @PathVariable String brandId, @PathVariable String agentId) {
        return agentService.getVerification(brandId, agentId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{agentId}/verification")
    public ResponseEntity<AgentVerification> updateVerification(
            @PathVariable String brandId, @PathVariable String agentId,
            @RequestBody AgentVerification patch) {
        if (patch.getState() == null) {
            return ResponseEntity.badRequest().build();
        }
        return agentService.updateVerification(brandId, agentId, patch.getState(), patch.getComment())
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Launch
    @PostMapping("/{agentId}:requestLaunch")
    public ResponseEntity<AgentLaunch> requestLaunch(@PathVariable String brandId, @PathVariable String agentId) {
        return agentService.requestLaunch(brandId, agentId)
                .map(ResponseEntity::ok).orElse(ResponseEntity.status(409).build());
    }

    @GetMapping("/{agentId}/launch")
    public ResponseEntity<AgentLaunch> getLaunch(@PathVariable String brandId, @PathVariable String agentId) {
        return agentService.getLaunch(brandId, agentId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{agentId}/launch")
    public ResponseEntity<AgentLaunch> updateLaunch(@PathVariable String brandId, @PathVariable String agentId,
                                                    @RequestBody AgentLaunch patch) {
        if (patch.getState() == null) {
            return ResponseEntity.badRequest().build();
        }
        return agentService.updateLaunch(brandId, agentId, patch.getState(), patch.getComment())
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
