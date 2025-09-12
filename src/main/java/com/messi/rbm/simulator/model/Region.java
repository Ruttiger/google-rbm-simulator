package com.messi.rbm.simulator.model;

/**
 * Represents a region supported by RBM.
 */
public class Region {
    private String name;
    private String displayName;
    private ManagementType managementType;

    public Region() {
    }

    public Region(String name, String displayName, ManagementType managementType) {
        this.name = name;
        this.displayName = displayName;
        this.managementType = managementType;
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

    public ManagementType getManagementType() {
        return managementType;
    }

    public void setManagementType(ManagementType managementType) {
        this.managementType = managementType;
    }
}
