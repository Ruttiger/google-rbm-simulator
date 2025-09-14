package win.agus4the.rbm.simulator.model.communications;

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
