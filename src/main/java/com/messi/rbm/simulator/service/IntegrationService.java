package com.messi.rbm.simulator.service;

import com.messi.rbm.simulator.model.Integration;
import com.messi.rbm.simulator.repo.RbmMemoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing integrations.
 */
@Service
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
