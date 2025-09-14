package win.agus4the.rbm.simulator.model.communications;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Core RBM agent information.
 */
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification = "Model acts as a simple data carrier")
public class RbmAgentInfo {

    @NotBlank
    @Size(max = 100)
    private String description;

    @NotBlank
    private String logoUri;

    @NotBlank
    private String heroUri;

    @NotNull
    private Info privacy;

    @NotNull
    private Info termsConditions;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$")
    private String color;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoUri() {
        return logoUri;
    }

    public void setLogoUri(String logoUri) {
        this.logoUri = logoUri;
    }

    public String getHeroUri() {
        return heroUri;
    }

    public void setHeroUri(String heroUri) {
        this.heroUri = heroUri;
    }

    public Info getPrivacy() {
        return privacy;
    }

    public void setPrivacy(Info privacy) {
        this.privacy = privacy;
    }

    public Info getTermsConditions() {
        return termsConditions;
    }

    public void setTermsConditions(Info termsConditions) {
        this.termsConditions = termsConditions;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Simple structure with a URI and label.
     */
    public static class Info {
        @NotBlank
        private String uri;
        @NotBlank
        private String label;

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}
