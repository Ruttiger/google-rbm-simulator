package com.messi.rbm.simulator.model.communications;

import java.time.Instant;

/**
 * Represents the verification process for an agent.
 */
public class AgentVerification {
    private String name;
    private VerificationState state;
    private Instant createTime;
    private Instant updateTime;
    private String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VerificationState getState() {
        return state;
    }

    public void setState(VerificationState state) {
        this.state = state;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
