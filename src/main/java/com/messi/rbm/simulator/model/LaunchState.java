package com.messi.rbm.simulator.model;

/**
 * Possible states for agent launch requests.
 */
public enum LaunchState {
    /** Launch requested but pending approval. */
    PENDING,
    /** Launch approved. */
    APPROVED,
    /** Launch denied. */
    DENIED
}
