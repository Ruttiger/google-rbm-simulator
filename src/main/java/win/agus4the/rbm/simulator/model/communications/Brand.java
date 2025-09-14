package win.agus4the.rbm.simulator.model.communications;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Representation of an RBM brand.
 */
public class Brand {

    /** Resource name in form brands/{brandId}. */
    private String name;

    @NotBlank
    @Size(max = 100)
    private String displayName;

    public Brand() {
    }

    public Brand(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
