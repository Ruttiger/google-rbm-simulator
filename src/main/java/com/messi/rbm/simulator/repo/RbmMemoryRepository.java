package com.messi.rbm.simulator.repo;

import com.messi.rbm.simulator.model.Agent;
import com.messi.rbm.simulator.model.AgentLaunch;
import com.messi.rbm.simulator.model.AgentVerification;
import com.messi.rbm.simulator.model.Brand;
import com.messi.rbm.simulator.model.Integration;
import com.messi.rbm.simulator.model.IntegrationStatus;
import com.messi.rbm.simulator.model.LaunchState;
import com.messi.rbm.simulator.model.RbmAgentInfo;
import com.messi.rbm.simulator.model.VerificationState;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simple in-memory storage for RBM entities.
 */
@Component
public class RbmMemoryRepository {

    private final ConcurrentHashMap<String, Brand> brands = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Agent>> agentsByBrand = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Integration>> integrationsByAgent =
            new ConcurrentHashMap<>();

    private final AtomicLong brandSeq = new AtomicLong(1);
    private final AtomicLong agentSeq = new AtomicLong(1);
    private final AtomicLong integrationSeq = new AtomicLong(1);

    /** Creates a new brand. */
    public Brand createBrand(String displayName) {
        String id = String.valueOf(brandSeq.getAndIncrement());
        Brand brand = new Brand("brands/" + id, displayName);
        brands.put(id, brand);
        return brand;
    }

    /** Returns a brand if it exists. */
    public Optional<Brand> getBrand(String brandId) {
        return Optional.ofNullable(brands.get(brandId));
    }

    /** Lists all brands. */
    public List<Brand> listBrands() {
        return new ArrayList<>(brands.values());
    }

    /** Updates a brand. */
    public Optional<Brand> updateBrand(String brandId, String displayName) {
        Brand b = brands.get(brandId);
        if (b == null) {
            return Optional.empty();
        }
        b.setDisplayName(displayName);
        return Optional.of(b);
    }

    /** Deletes a brand cascading its agents and integrations. */
    public boolean deleteBrand(String brandId) {
        Brand removed = brands.remove(brandId);
        if (removed != null) {
            ConcurrentHashMap<String, Agent> agents = agentsByBrand.remove(brandId);
            if (agents != null) {
                agents.keySet().forEach(aid -> integrationsByAgent.remove(brandId + "/" + aid));
            }
            return true;
        }
        return false;
    }

    /** Creates an agent under a brand. */
    public Optional<Agent> createAgent(String brandId, Agent payload) {
        Brand b = brands.get(brandId);
        if (b == null) {
            return Optional.empty();
        }
        String id = String.valueOf(agentSeq.getAndIncrement());
        payload.setBrandName(b.getName());
        payload.setName(b.getName() + "/agents/" + id);
        agentsByBrand.computeIfAbsent(brandId, k -> new ConcurrentHashMap<>()).put(id, payload);
        return Optional.of(payload);
    }

    /** Gets an agent. */
    public Optional<Agent> getAgent(String brandId, String agentId) {
        ConcurrentHashMap<String, Agent> agents = agentsByBrand.get(brandId);
        return Optional.ofNullable(agents == null ? null : agents.get(agentId));
    }

    /** Lists agents for a brand. */
    public List<Agent> listAgents(String brandId) {
        ConcurrentHashMap<String, Agent> agents = agentsByBrand.get(brandId);
        return agents == null ? List.of() : new ArrayList<>(agents.values());
    }

    /** Updates an agent. */
    public Optional<Agent> updateAgent(String brandId, String agentId, Map<String, Object> patch) {
        ConcurrentHashMap<String, Agent> agents = agentsByBrand.get(brandId);
        Agent agent = agents == null ? null : agents.get(agentId);
        if (agent == null) {
            return Optional.empty();
        }
        if (patch.containsKey("displayName")) {
            agent.setDisplayName((String) patch.get("displayName"));
        }
        if (patch.containsKey("rcsBusinessMessagingAgent")) {
            // naive merge
            RbmAgentInfo info = agent.getRcsBusinessMessagingAgent();
            Map<?, ?> map = (Map<?, ?>) patch.get("rcsBusinessMessagingAgent");
            if (map.containsKey("description")) {
                info.setDescription((String) map.get("description"));
            }
            if (map.containsKey("logoUri")) {
                info.setLogoUri((String) map.get("logoUri"));
            }
            if (map.containsKey("heroUri")) {
                info.setHeroUri((String) map.get("heroUri"));
            }
        }
        return Optional.of(agent);
    }

    /** Deletes an agent. */
    public boolean deleteAgent(String brandId, String agentId) {
        ConcurrentHashMap<String, Agent> agents = agentsByBrand.get(brandId);
        if (agents != null) {
            Agent removed = agents.remove(agentId);
            integrationsByAgent.remove(brandId + "/" + agentId);
            return removed != null;
        }
        return false;
    }

    /** Creates integration for an agent. */
    public Optional<Integration> createIntegration(String brandId, String agentId, Integration payload) {
        if (!agentsByBrand.containsKey(brandId) || !agentsByBrand.get(brandId).containsKey(agentId)) {
            return Optional.empty();
        }
        String key = brandId + "/" + agentId;
        String id = String.valueOf(integrationSeq.getAndIncrement());
        payload.setName(agentsByBrand.get(brandId).get(agentId).getName() + "/integrations/" + id);
        integrationsByAgent.computeIfAbsent(key, k -> new ConcurrentHashMap<>()).put(id, payload);
        return Optional.of(payload);
    }

    /** Lists integrations. */
    public List<Integration> listIntegrations(String brandId, String agentId) {
        String key = brandId + "/" + agentId;
        ConcurrentHashMap<String, Integration> map = integrationsByAgent.get(key);
        return map == null ? List.of() : new ArrayList<>(map.values());
    }

    /** Gets integration. */
    public Optional<Integration> getIntegration(String brandId, String agentId, String integrationId) {
        String key = brandId + "/" + agentId;
        ConcurrentHashMap<String, Integration> map = integrationsByAgent.get(key);
        return Optional.ofNullable(map == null ? null : map.get(integrationId));
    }

    /** Updates integration. */
    public Optional<Integration> updateIntegration(
            String brandId, String agentId, String integrationId, Map<String, Object> patch) {
        Integration integ = getIntegration(brandId, agentId, integrationId).orElse(null);
        if (integ == null) {
            return Optional.empty();
        }
        if (patch.containsKey("status")) {
            integ.setStatus(IntegrationStatus.valueOf((String) patch.get("status")));
        }
        if (integ.getAgentWebhookIntegration() != null && patch.containsKey("agentWebhookIntegration")) {
            Map<?, ?> map = (Map<?, ?>) patch.get("agentWebhookIntegration");
            if (map.containsKey("webhookUri")) {
                integ.getAgentWebhookIntegration().setWebhookUri((String) map.get("webhookUri"));
            }
            if (map.containsKey("username")) {
                integ.getAgentWebhookIntegration().setUsername((String) map.get("username"));
            }
            if (map.containsKey("password")) {
                integ.getAgentWebhookIntegration().setPassword((String) map.get("password"));
            }
        }
        return Optional.of(integ);
    }

    /** Deletes integration. */
    public boolean deleteIntegration(String brandId, String agentId, String integrationId) {
        String key = brandId + "/" + agentId;
        ConcurrentHashMap<String, Integration> map = integrationsByAgent.get(key);
        if (map != null) {
            return map.remove(integrationId) != null;
        }
        return false;
    }

    /** Creates verification entry. */
    public Optional<AgentVerification> createVerification(String brandId, String agentId) {
        ConcurrentHashMap<String, Agent> agents = agentsByBrand.get(brandId);
        Agent agent = agents == null ? null : agents.get(agentId);
        if (agent == null) {
            return Optional.empty();
        }
        AgentVerification v = new AgentVerification();
        v.setName(agent.getName() + "/verification");
        v.setState(VerificationState.PENDING);
        v.setCreateTime(Instant.now());
        agent.setVerification(v);
        agent.setVerified(false);
        return Optional.of(v);
    }

    /** Updates verification. */
    public Optional<AgentVerification> updateVerification(
            String brandId, String agentId, VerificationState state, String comment) {
        ConcurrentHashMap<String, Agent> agents = agentsByBrand.get(brandId);
        Agent agent = agents == null ? null : agents.get(agentId);
        if (agent == null || agent.getVerification() == null) {
            return Optional.empty();
        }
        AgentVerification v = agent.getVerification();
        v.setState(state);
        v.setUpdateTime(Instant.now());
        v.setComment(comment);
        if (state == VerificationState.VERIFIED) {
            agent.setVerified(true);
        }
        return Optional.of(v);
    }

    /** Creates launch entry. */
    public Optional<AgentLaunch> createLaunch(String brandId, String agentId) {
        ConcurrentHashMap<String, Agent> agents = agentsByBrand.get(brandId);
        Agent agent = agents == null ? null : agents.get(agentId);
        if (agent == null || !agent.isVerified()) {
            return Optional.empty();
        }
        AgentLaunch l = new AgentLaunch();
        l.setName(agent.getName() + "/launch");
        l.setState(LaunchState.PENDING);
        l.setCreateTime(Instant.now());
        agent.setLaunch(l);
        agent.setLaunched(false);
        return Optional.of(l);
    }

    /** Updates launch. */
    public Optional<AgentLaunch> updateLaunch(
            String brandId, String agentId, LaunchState state, String comment) {
        ConcurrentHashMap<String, Agent> agents = agentsByBrand.get(brandId);
        Agent agent = agents == null ? null : agents.get(agentId);
        if (agent == null || agent.getLaunch() == null) {
            return Optional.empty();
        }
        AgentLaunch l = agent.getLaunch();
        l.setState(state);
        l.setUpdateTime(Instant.now());
        l.setComment(comment);
        if (state == LaunchState.APPROVED) {
            agent.setLaunched(true);
        }
        return Optional.of(l);
    }
}
