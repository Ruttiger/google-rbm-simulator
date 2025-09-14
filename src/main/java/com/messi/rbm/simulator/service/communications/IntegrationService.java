package com.messi.rbm.simulator.service.communications;

import com.messi.rbm.simulator.model.communications.Integration;
import com.messi.rbm.simulator.repo.communications.RbmMemoryRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing integrations.
 */
@Service
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Repository is injected and not exposed")
public class IntegrationService {
    private final RbmMemoryRepository repo;

    public IntegrationService(RbmMemoryRepository repo) {
        this.repo = repo;
    }

    public Optional<Integration> create(String brandId, String agentId, Integration payload) {
        return repo.createIntegration(brandId, agentId, payload);
    }

    public List<Integration> list(String brandId, String agentId) {
        return repo.listIntegrations(brandId, agentId);
    }

    public Optional<Integration> get(String brandId, String agentId, String integrationId) {
        return repo.getIntegration(brandId, agentId, integrationId);
    }

    public Optional<Integration> patch(
            String brandId, String agentId, String integrationId, Map<String, Object> patch) {
        return repo.updateIntegration(brandId, agentId, integrationId, patch);
    }

    public boolean delete(String brandId, String agentId, String integrationId) {
        return repo.deleteIntegration(brandId, agentId, integrationId);
    }
}
