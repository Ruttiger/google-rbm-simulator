package win.agus4the.rbm.simulator.service.communications;

import win.agus4the.rbm.simulator.model.communications.Integration;
import win.agus4the.rbm.simulator.repo.communications.RbmMemoryRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(IntegrationService.class);

    public IntegrationService(RbmMemoryRepository repo) {
        this.repo = repo;
    }

    public Optional<Integration> create(String brandId, String agentId, Integration payload) {
        Optional<Integration> created = repo.createIntegration(brandId, agentId, payload);
        created.ifPresent(i -> log.info("Created integration brand={} agent={} id={}", brandId, agentId, i.getName()));
        return created;
    }

    public List<Integration> list(String brandId, String agentId) {
        List<Integration> list = repo.listIntegrations(brandId, agentId);
        log.debug("List integrations brand={} agent={} count={}", brandId, agentId, list.size());
        return list;
    }

    public Optional<Integration> get(String brandId, String agentId, String integrationId) {
        Optional<Integration> integration = repo.getIntegration(brandId, agentId, integrationId);
        log.debug("Get integration brand={} agent={} id={} found={}", brandId, agentId, integrationId, integration.isPresent());
        return integration;
    }

    public Optional<Integration> patch(
            String brandId, String agentId, String integrationId, Map<String, Object> patch) {
        Optional<Integration> updated = repo.updateIntegration(brandId, agentId, integrationId, patch);
        updated.ifPresent(i -> log.info("Patched integration brand={} agent={} id={} keys={}", brandId, agentId, integrationId, patch.keySet()));
        return updated;
    }

    public boolean delete(String brandId, String agentId, String integrationId) {
        boolean deleted = repo.deleteIntegration(brandId, agentId, integrationId);
        if (deleted) {
            log.info("Deleted integration brand={} agent={} id={}", brandId, agentId, integrationId);
        } else {
            log.warn("Integration not found for delete brand={} agent={} id={}", brandId, agentId, integrationId);
        }
        return deleted;
    }
}
