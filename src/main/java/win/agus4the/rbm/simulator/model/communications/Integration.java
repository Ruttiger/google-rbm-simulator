package win.agus4the.rbm.simulator.model.communications;

import jakarta.validation.constraints.NotNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Represents an agent integration.
 */
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification = "Model acts as a simple data carrier")
public class Integration {
    private String name;
    private IntegrationStatus status = IntegrationStatus.ENABLED;

    private DialogflowEsIntegration dialogflowEsIntegration;
    private DialogflowCxIntegration dialogflowCxIntegration;
    private AgentWebhookIntegration agentWebhookIntegration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IntegrationStatus getStatus() {
        return status;
    }

    public void setStatus(IntegrationStatus status) {
        this.status = status;
    }

    public DialogflowEsIntegration getDialogflowEsIntegration() {
        return dialogflowEsIntegration;
    }

    public void setDialogflowEsIntegration(DialogflowEsIntegration dialogflowEsIntegration) {
        this.dialogflowEsIntegration = dialogflowEsIntegration;
    }

    public DialogflowCxIntegration getDialogflowCxIntegration() {
        return dialogflowCxIntegration;
    }

    public void setDialogflowCxIntegration(DialogflowCxIntegration dialogflowCxIntegration) {
        this.dialogflowCxIntegration = dialogflowCxIntegration;
    }

    public AgentWebhookIntegration getAgentWebhookIntegration() {
        return agentWebhookIntegration;
    }

    public void setAgentWebhookIntegration(AgentWebhookIntegration agentWebhookIntegration) {
        this.agentWebhookIntegration = agentWebhookIntegration;
    }

    /** Integration based on Dialogflow ES. */
    public static class DialogflowEsIntegration {
        @NotNull
        private String dialogflowProjectId;
        private String autoResponseStatus;

        public String getDialogflowProjectId() {
            return dialogflowProjectId;
        }

        public void setDialogflowProjectId(String dialogflowProjectId) {
            this.dialogflowProjectId = dialogflowProjectId;
        }

        public String getAutoResponseStatus() {
            return autoResponseStatus;
        }

        public void setAutoResponseStatus(String autoResponseStatus) {
            this.autoResponseStatus = autoResponseStatus;
        }
    }

    /** Integration based on Dialogflow CX (stub). */
    public static class DialogflowCxIntegration {
        private String dialogflowCxProjectId;

        public String getDialogflowCxProjectId() {
            return dialogflowCxProjectId;
        }

        public void setDialogflowCxProjectId(String dialogflowCxProjectId) {
            this.dialogflowCxProjectId = dialogflowCxProjectId;
        }
    }

    /** Webhook integration. */
    public static class AgentWebhookIntegration {
        @NotNull
        private String webhookUri;
        private String username;
        private String password;

        public String getWebhookUri() {
            return webhookUri;
        }

        public void setWebhookUri(String webhookUri) {
            this.webhookUri = webhookUri;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
