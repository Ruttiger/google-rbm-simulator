package com.messi.rbm.simulator.model.communications;

/**
 * Possible states for agent verification.
 */
public enum VerificationState {
    /** Verification requested but not yet resolved. */
    PENDING,
    /** Agent verified successfully. */
    VERIFIED,
    /** Agent verification rejected. */
    REJECTED
}
