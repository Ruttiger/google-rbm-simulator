package win.agus4the.rbm.simulator.model.communications;

import java.time.Instant;

/**
 * Represents the launch process for an agent.
 */
public class AgentLaunch {
    private String name;
    private LaunchState state;
    private Instant createTime;
    private Instant updateTime;
    private String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LaunchState getState() {
        return state;
    }

    public void setState(LaunchState state) {
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
