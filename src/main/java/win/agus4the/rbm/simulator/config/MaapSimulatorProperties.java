package win.agus4the.rbm.simulator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import win.agus4the.rbm.simulator.core.model.MaapChannel;

import java.util.EnumSet;
import java.util.Set;

@ConfigurationProperties(prefix = "maap.simulator")
public class MaapSimulatorProperties {

    private Set<MaapChannel> enabledInterfaces = EnumSet.of(MaapChannel.RBM, MaapChannel.PCM);
    private boolean strictPcmDeliveryReportRouting;

    public Set<MaapChannel> getEnabledInterfaces() {
        return EnumSet.copyOf(enabledInterfaces);
    }

    public void setEnabledInterfaces(Set<MaapChannel> enabledInterfaces) {
        this.enabledInterfaces = enabledInterfaces == null || enabledInterfaces.isEmpty()
                ? EnumSet.noneOf(MaapChannel.class)
                : EnumSet.copyOf(enabledInterfaces);
    }

    public boolean isStrictPcmDeliveryReportRouting() {
        return strictPcmDeliveryReportRouting;
    }

    public void setStrictPcmDeliveryReportRouting(boolean strictPcmDeliveryReportRouting) {
        this.strictPcmDeliveryReportRouting = strictPcmDeliveryReportRouting;
    }
}
