package com.messi.rbm.simulator.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthPropertiesTest {

    @Test
    void rejectsInvalidClientsInStrictMode() {
        AuthProperties props = new AuthProperties();
        props.setMode(AuthProperties.Mode.STRICT);
        AuthProperties.Client c = new AuthProperties.Client();
        c.setClientId("client");
        c.setClientSecret("secret");
        props.getAcceptedClients().add(c);

        assertTrue(props.isClientAccepted("client", "secret"));
        assertFalse(props.isClientAccepted("bad", "wrong"));
    }

    @Test
    void filtersOnlyAllowedScopes() {
        AuthProperties props = new AuthProperties();
        props.setAllowedScopes(List.of("s1", "s2"));
        List<String> filtered = props.filterScopes(List.of("s1", "s3"));
        assertEquals(List.of("s1"), filtered);
    }

    @Test
    void returnsAllAllowedScopesWhenRequestEmpty() {
        AuthProperties props = new AuthProperties();
        props.setAllowedScopes(List.of("a", "b"));
        List<String> filtered = props.filterScopes(List.of());
        assertEquals(List.of("a", "b"), filtered);
    }
}
