package win.agus4the.rbm.simulator.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;
import win.agus4the.rbm.simulator.core.model.MaapChannel;

@Service
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed dependency")
public class InterfaceActivationService {
    private final MaapSimulatorProperties properties;

    public InterfaceActivationService(MaapSimulatorProperties properties) {
        this.properties = properties;
    }

    public boolean isEnabled(MaapChannel channel) {
        return properties.getEnabledInterfaces().contains(channel);
    }
}
