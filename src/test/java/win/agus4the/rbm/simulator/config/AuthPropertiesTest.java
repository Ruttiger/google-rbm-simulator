package win.agus4the.rbm.simulator.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthPropertiesTest {

    @Test
    void rejectsInvalidClientsInStrictMode() {
        AuthProperties props = new AuthProperties();
        props.setMode(AuthProperties.Mode.STRICT);
        AuthProperties.Client c = new AuthProperties.Client();
        c.setClientId("client");
        c.setClientSecret("secret");
        props.setAcceptedClients(List.of(c));

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
