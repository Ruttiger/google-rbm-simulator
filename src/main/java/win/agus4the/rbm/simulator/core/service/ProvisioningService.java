package win.agus4the.rbm.simulator.core.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;
import win.agus4the.rbm.simulator.core.model.MaapChannel;
import win.agus4the.rbm.simulator.core.model.MaapProvisioningConfig;
import win.agus4the.rbm.simulator.core.repo.MaapProvisioningRepository;

import java.util.Optional;

@Service
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed dependency")
public class ProvisioningService {

    private final MaapProvisioningRepository repository;

    public ProvisioningService(MaapProvisioningRepository repository) {
        this.repository = repository;
    }

    public MaapProvisioningConfig upsertPcm(String sender, String drUrl, String inboundUrl, String username, String password) {
        return repository.upsert(new MaapProvisioningConfig(MaapChannel.PCM, sender, drUrl, inboundUrl, username, password));
    }

    public Optional<MaapProvisioningConfig> getPcm(String sender) {
        return repository.find(MaapChannel.PCM, sender);
    }

    public void deletePcm(String sender) {
        repository.delete(MaapChannel.PCM, sender);
    }
}
