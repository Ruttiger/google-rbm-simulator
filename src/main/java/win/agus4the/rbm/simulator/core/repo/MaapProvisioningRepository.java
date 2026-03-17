package win.agus4the.rbm.simulator.core.repo;

import org.springframework.stereotype.Repository;
import win.agus4the.rbm.simulator.core.model.MaapChannel;
import win.agus4the.rbm.simulator.core.model.MaapProvisioningConfig;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MaapProvisioningRepository {

    private final Map<String, MaapProvisioningConfig> entries = new ConcurrentHashMap<>();

    public MaapProvisioningConfig upsert(MaapProvisioningConfig config) {
        entries.put(toMapKey(config.channel(), config.key()), config);
        return config;
    }

    public Optional<MaapProvisioningConfig> find(MaapChannel channel, String key) {
        return Optional.ofNullable(entries.get(toMapKey(channel, key)));
    }

    public void delete(MaapChannel channel, String key) {
        entries.remove(toMapKey(channel, key));
    }

    private String toMapKey(MaapChannel channel, String key) {
        return channel + "::" + key;
    }
}
