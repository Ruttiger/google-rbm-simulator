package win.agus4the.rbm.simulator.service.communications;

import win.agus4the.rbm.simulator.model.communications.Agent;
import win.agus4the.rbm.simulator.model.communications.AgentLaunch;
import win.agus4the.rbm.simulator.model.communications.AgentVerification;
import win.agus4the.rbm.simulator.model.communications.LaunchState;
import win.agus4the.rbm.simulator.model.communications.VerificationState;
import win.agus4the.rbm.simulator.repo.communications.RbmMemoryRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing agents.
 */
@Service
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Repository is injected and not exposed")
public class AgentService {
    private final RbmMemoryRepository repo;
    private static final Logger log = LoggerFactory.getLogger(AgentService.class);

    public AgentService(RbmMemoryRepository repo) {
        this.repo = repo;
    }

    public Optional<Agent> create(String brandId, Agent payload) {
        Optional<Agent> agent = repo.createAgent(brandId, payload);
        agent.ifPresent(a -> log.info("Created agent brand={} id={}", brandId, a.getName()));
        return agent;
    }

    public Optional<Agent> get(String brandId, String agentId) {
        Optional<Agent> agent = repo.getAgent(brandId, agentId);
        log.debug("Get agent brand={} id={} found={}", brandId, agentId, agent.isPresent());
        return agent;
    }

    public List<Agent> list(String brandId) {
        List<Agent> list = repo.listAgents(brandId);
        log.debug("List agents brand={} count={}", brandId, list.size());
        return list;
    }

    public Optional<Agent> patch(String brandId, String agentId, Map<String, Object> patch) {
        Optional<Agent> updated = repo.updateAgent(brandId, agentId, patch);
        updated.ifPresent(a -> log.info("Patched agent brand={} id={} keys={}", brandId, agentId, patch.keySet()));
        return updated;
    }

    public boolean delete(String brandId, String agentId) {
        boolean deleted = repo.deleteAgent(brandId, agentId);
        if (deleted) {
            log.info("Deleted agent brand={} id={}", brandId, agentId);
        } else {
            log.warn("Agent not found for delete brand={} id={}", brandId, agentId);
        }
        return deleted;
    }

    public Optional<AgentVerification> requestVerification(String brandId, String agentId) {
        Agent agent = repo.getAgent(brandId, agentId).orElse(null);
        if (agent == null
                || agent.getVerification() != null
                && agent.getVerification().getState() == VerificationState.PENDING) {
            log.warn("Verification request rejected brand={} agent={}", brandId, agentId);
            return Optional.empty();
        }
        Optional<AgentVerification> verification = repo.createVerification(brandId, agentId);
        verification.ifPresent(v -> log.info("Verification created brand={} agent={} state={}"
                , brandId, agentId, v.getState()));
        return verification;
    }

    public Optional<AgentVerification> getVerification(String brandId, String agentId) {
        Optional<AgentVerification> verification = repo.getAgent(brandId, agentId).map(Agent::getVerification);
        log.debug("Get verification brand={} agent={} present={}"
                , brandId, agentId, verification.isPresent());
        return verification;
    }

    public Optional<AgentVerification> updateVerification(
            String brandId, String agentId, VerificationState state, String comment) {
        Optional<AgentVerification> updated = repo.updateVerification(brandId, agentId, state, comment);
        updated.ifPresent(v -> log.info("Updated verification brand={} agent={} state={}", brandId, agentId, state));
        return updated;
    }

    public Optional<AgentLaunch> requestLaunch(String brandId, String agentId) {
        Agent agent = repo.getAgent(brandId, agentId).orElse(null);
        if (agent == null
                || !agent.isVerified()
                || (agent.getLaunch() != null
                && agent.getLaunch().getState() == LaunchState.PENDING)) {
            log.warn("Launch request rejected brand={} agent={}", brandId, agentId);
            return Optional.empty();
        }
        Optional<AgentLaunch> launch = repo.createLaunch(brandId, agentId);
        launch.ifPresent(l -> log.info("Launch created brand={} agent={} state={}", brandId, agentId, l.getState()));
        return launch;
    }

    public Optional<AgentLaunch> getLaunch(String brandId, String agentId) {
        Optional<AgentLaunch> launch = repo.getAgent(brandId, agentId).map(Agent::getLaunch);
        log.debug("Get launch brand={} agent={} present={}", brandId, agentId, launch.isPresent());
        return launch;
    }

    public Optional<AgentLaunch> updateLaunch(String brandId, String agentId, LaunchState state, String comment) {
        Optional<AgentLaunch> updated = repo.updateLaunch(brandId, agentId, state, comment);
        updated.ifPresent(l -> log.info("Updated launch brand={} agent={} state={}", brandId, agentId, state));
        return updated;
    }
}
