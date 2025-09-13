package com.messi.rbm.simulator.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Stores webhook configuration for an agent.
 *
 * @param webhookUrl webhook endpoint URL
 * @param clientToken optional verification token
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record WebhookConfig(
        String webhookUrl,
        String clientToken
) {
}

