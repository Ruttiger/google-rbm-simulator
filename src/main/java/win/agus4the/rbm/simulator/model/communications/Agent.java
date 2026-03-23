package win.agus4the.rbm.simulator.model.communications;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Representation of an RBM agent.
 */
public class Agent {

    private String name;
    private String brandName;

    @NotBlank
    @Size(max = 100)
    private String displayName;

    @NotNull
    private RbmAgentInfo rcsBusinessMessagingAgent;

    private AgentVerification verification;
    private AgentLaunch launch;
    private boolean verified;
    private boolean launched;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public RbmAgentInfo getRcsBusinessMessagingAgent() {
        return copyRbmAgentInfo(rcsBusinessMessagingAgent);
    }

    public void setRcsBusinessMessagingAgent(RbmAgentInfo rcsBusinessMessagingAgent) {
        this.rcsBusinessMessagingAgent = copyRbmAgentInfo(rcsBusinessMessagingAgent);
    }

    public AgentVerification getVerification() {
        return copyVerification(verification);
    }

    public void setVerification(AgentVerification verification) {
        this.verification = copyVerification(verification);
    }

    public AgentLaunch getLaunch() {
        return copyLaunch(launch);
    }

    public void setLaunch(AgentLaunch launch) {
        this.launch = copyLaunch(launch);
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isLaunched() {
        return launched;
    }

    public void setLaunched(boolean launched) {
        this.launched = launched;
    }

    private static AgentVerification copyVerification(AgentVerification source) {
        if (source == null) {
            return null;
        }
        AgentVerification copy = new AgentVerification();
        copy.setName(source.getName());
        copy.setState(source.getState());
        copy.setCreateTime(source.getCreateTime());
        copy.setUpdateTime(source.getUpdateTime());
        copy.setComment(source.getComment());
        return copy;
    }

    private static AgentLaunch copyLaunch(AgentLaunch source) {
        if (source == null) {
            return null;
        }
        AgentLaunch copy = new AgentLaunch();
        copy.setName(source.getName());
        copy.setState(source.getState());
        copy.setCreateTime(source.getCreateTime());
        copy.setUpdateTime(source.getUpdateTime());
        copy.setComment(source.getComment());
        return copy;
    }

    private static RbmAgentInfo copyRbmAgentInfo(RbmAgentInfo source) {
        if (source == null) {
            return null;
        }
        RbmAgentInfo copy = new RbmAgentInfo();
        copy.setDescription(source.getDescription());
        copy.setLogoUri(source.getLogoUri());
        copy.setHeroUri(source.getHeroUri());
        copy.setPrivacy(source.getPrivacy());
        copy.setTermsConditions(source.getTermsConditions());
        copy.setColor(source.getColor());
        return copy;
    }
}
