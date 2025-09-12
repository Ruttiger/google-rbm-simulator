package com.messi.rbm.simulator.service;

import com.messi.rbm.simulator.model.*;
import com.messi.rbm.simulator.repo.RbmMemoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing agents.
 */
@Service
public class AgentService {
    private final RbmMemoryRepository repo;

    public AgentService(RbmMemoryRepository repo) {
        this.repo = repo;
    }

    public Optional<Agent> create(String brandId, Agent payload) {
        return repo.createAgent(brandId, payload);
    }

    public Optional<Agent> get(String brandId, String agentId) {
        return repo.getAgent(brandId, agentId);
    }

    public List<Agent> list(String brandId) {
        return repo.listAgents(brandId);
    }

    public Optional<Agent> patch(String brandId, String agentId, Map<String, Object> patch) {
        return repo.updateAgent(brandId, agentId, patch);
    }

    public boolean delete(String brandId, String agentId) {
        return repo.deleteAgent(brandId, agentId);
    }

    public Optional<AgentVerification> requestVerification(String brandId, String agentId) {
        Agent agent = repo.getAgent(brandId, agentId).orElse(null);
        if (agent == null || agent.getVerification() != null && agent.getVerification().getState() == VerificationState.PENDING) {
            return Optional.empty();
        }
        return repo.createVerification(brandId, agentId);
    }

    public Optional<AgentVerification> getVerification(String brandId, String agentId) {
        return repo.getAgent(brandId, agentId).map(Agent::getVerification);
    }

    public Optional<AgentVerification> updateVerification(String brandId, String agentId, VerificationState state, String comment) {
        return repo.updateVerification(brandId, agentId, state, comment);
    }

    public Optional<AgentLaunch> requestLaunch(String brandId, String agentId) {
        Agent agent = repo.getAgent(brandId, agentId).orElse(null);
        if (agent == null || !agent.isVerified() || (agent.getLaunch() != null && agent.getLaunch().getState() == LaunchState.PENDING)) {
            return Optional.empty();
        }
        return repo.createLaunch(brandId, agentId);
    }

    public Optional<AgentLaunch> getLaunch(String brandId, String agentId) {
        return repo.getAgent(brandId, agentId).map(Agent::getLaunch);
    }

    public Optional<AgentLaunch> updateLaunch(String brandId, String agentId, LaunchState state, String comment) {
        return repo.updateLaunch(brandId, agentId, state, comment);
    }
}
