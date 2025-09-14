package win.agus4the.rbm.simulator.controller.communications;

import win.agus4the.rbm.simulator.model.communications.Agent;
import win.agus4the.rbm.simulator.model.communications.AgentLaunch;
import win.agus4the.rbm.simulator.model.communications.AgentVerification;
import win.agus4the.rbm.simulator.service.communications.AgentService;
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
 * Controller for agent endpoints including verification and launch.
 */
@RestController
@RequestMapping("/v1/brands/{brandId}/agents")
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Dependencies are injected and not exposed")
public class AgentController {

    private final AgentService agentService;
    private static final Logger log = LoggerFactory.getLogger(AgentController.class);

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping
    public ResponseEntity<Agent> create(@PathVariable String brandId, @Valid @RequestBody Agent req) {
        return agentService.create(brandId, req)
                .map(agent -> {
                    log.info("Agent created brand={} id={}", brandId, agent.getName());
                    return ResponseEntity.ok(agent);
                })
                .orElseGet(() -> {
                    log.warn("Failed to create agent for brand={}", brandId);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping
    public Map<String, Object> list(@PathVariable String brandId) {
        List<Agent> list = agentService.list(brandId);
        log.debug("Listing agents brand={} count={}", brandId, list.size());
        return Map.of("agents", list, "nextPageToken", "");
    }

    @GetMapping("/{agentId}")
    public ResponseEntity<Agent> get(@PathVariable String brandId, @PathVariable String agentId) {
        return agentService.get(brandId, agentId)
                .map(agent -> {
                    log.info("Retrieved agent brand={} id={}", brandId, agentId);
                    return ResponseEntity.ok(agent);
                })
                .orElseGet(() -> {
                    log.warn("Agent not found brand={} id={}", brandId, agentId);
                    return ResponseEntity.notFound().build();
                });
    }

    @PatchMapping("/{agentId}")
    public ResponseEntity<Agent> patch(@PathVariable String brandId, @PathVariable String agentId,
                                       @RequestBody Map<String, Object> patch) {
        return agentService.patch(brandId, agentId, patch)
                .map(agent -> {
                    log.info("Patched agent brand={} id={} keys={}", brandId, agentId, patch.keySet());
                    return ResponseEntity.ok(agent);
                })
                .orElseGet(() -> {
                    log.warn("Agent not found for patch brand={} id={}", brandId, agentId);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{agentId}")
    public ResponseEntity<Void> delete(@PathVariable String brandId, @PathVariable String agentId) {
        boolean deleted = agentService.delete(brandId, agentId);
        if (deleted) {
            log.info("Deleted agent brand={} id={}", brandId, agentId);
            return ResponseEntity.noContent().build();
        }
        log.warn("Agent not found for delete brand={} id={}", brandId, agentId);
        return ResponseEntity.notFound().build();
    }

    // Verification
    @PostMapping("/{agentId}:requestVerification")
    public ResponseEntity<AgentVerification> requestVerification(@PathVariable String brandId,
                                                                 @PathVariable String agentId) {
        return agentService.requestVerification(brandId, agentId)
                .map(v -> {
                    log.info("Verification requested brand={} agent={}", brandId, agentId);
                    return ResponseEntity.ok(v);
                })
                .orElseGet(() -> {
                    log.warn("Verification request conflict brand={} agent={}", brandId, agentId);
                    return ResponseEntity.status(409).build();
                });
    }

    @GetMapping("/{agentId}/verification")
    public ResponseEntity<AgentVerification> getVerification(
            @PathVariable String brandId, @PathVariable String agentId) {
        return agentService.getVerification(brandId, agentId)
                .map(v -> {
                    log.info("Retrieved verification brand={} agent={}", brandId, agentId);
                    return ResponseEntity.ok(v);
                })
                .orElseGet(() -> {
                    log.warn("Verification not found brand={} agent={}", brandId, agentId);
                    return ResponseEntity.notFound().build();
                });
    }

    @PatchMapping("/{agentId}/verification")
    public ResponseEntity<AgentVerification> updateVerification(
            @PathVariable String brandId, @PathVariable String agentId,
            @RequestBody AgentVerification patch) {
        if (patch.getState() == null) {
            return ResponseEntity.badRequest().build();
        }
        return agentService.updateVerification(brandId, agentId, patch.getState(), patch.getComment())
                .map(v -> {
                    log.info("Updated verification brand={} agent={} state={}", brandId, agentId, patch.getState());
                    return ResponseEntity.ok(v);
                })
                .orElseGet(() -> {
                    log.warn("Verification not found for update brand={} agent={}", brandId, agentId);
                    return ResponseEntity.notFound().build();
                });
    }

    // Launch
    @PostMapping("/{agentId}:requestLaunch")
    public ResponseEntity<AgentLaunch> requestLaunch(@PathVariable String brandId, @PathVariable String agentId) {
        return agentService.requestLaunch(brandId, agentId)
                .map(l -> {
                    log.info("Launch requested brand={} agent={}", brandId, agentId);
                    return ResponseEntity.ok(l);
                })
                .orElseGet(() -> {
                    log.warn("Launch request conflict brand={} agent={}", brandId, agentId);
                    return ResponseEntity.status(409).build();
                });
    }

    @GetMapping("/{agentId}/launch")
    public ResponseEntity<AgentLaunch> getLaunch(@PathVariable String brandId, @PathVariable String agentId) {
        return agentService.getLaunch(brandId, agentId)
                .map(l -> {
                    log.info("Retrieved launch brand={} agent={}", brandId, agentId);
                    return ResponseEntity.ok(l);
                })
                .orElseGet(() -> {
                    log.warn("Launch not found brand={} agent={}", brandId, agentId);
                    return ResponseEntity.notFound().build();
                });
    }

    @PatchMapping("/{agentId}/launch")
    public ResponseEntity<AgentLaunch> updateLaunch(@PathVariable String brandId, @PathVariable String agentId,
                                                    @RequestBody AgentLaunch patch) {
        if (patch.getState() == null) {
            return ResponseEntity.badRequest().build();
        }
        return agentService.updateLaunch(brandId, agentId, patch.getState(), patch.getComment())
                .map(l -> {
                    log.info("Updated launch brand={} agent={} state={}", brandId, agentId, patch.getState());
                    return ResponseEntity.ok(l);
                })
                .orElseGet(() -> {
                    log.warn("Launch not found for update brand={} agent={}", brandId, agentId);
                    return ResponseEntity.notFound().build();
                });
    }
}
