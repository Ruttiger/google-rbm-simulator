package win.agus4the.rbm.simulator.config;

import org.springframework.stereotype.Service;
import win.agus4the.rbm.simulator.core.model.MaapChannel;

@Service
public class InterfaceActivationService {
    private final MaapSimulatorProperties properties;

    public InterfaceActivationService(MaapSimulatorProperties properties) {
        this.properties = properties;
    }

    public boolean isEnabled(MaapChannel channel) {
        return properties.getEnabledInterfaces().contains(channel);
    }
}
