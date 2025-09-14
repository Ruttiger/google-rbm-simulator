package com.messi.rbm.simulator.model.communications;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Representation of an RBM agent.
 */
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification = "Model acts as a simple data carrier")
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
        return rcsBusinessMessagingAgent;
    }

    public void setRcsBusinessMessagingAgent(RbmAgentInfo rcsBusinessMessagingAgent) {
        this.rcsBusinessMessagingAgent = rcsBusinessMessagingAgent;
    }

    public AgentVerification getVerification() {
        return verification;
    }

    public void setVerification(AgentVerification verification) {
        this.verification = verification;
    }

    public AgentLaunch getLaunch() {
        return launch;
    }

    public void setLaunch(AgentLaunch launch) {
        this.launch = launch;
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
}
