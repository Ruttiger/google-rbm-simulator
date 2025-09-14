package win.agus4the.rbm.simulator.config;

import org.junit.jupiter.api.Test;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.test.MockHttpRequest;
import org.zalando.logbook.test.MockHttpResponse;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FunctionalHttpLogFormatterTest {

    private final Correlation correlation = new Correlation() {
        @Override
        public String getId() {
            return "id";
        }

        @Override
        public Instant getStart() {
            return Instant.EPOCH;
        }

        @Override
        public Instant getEnd() {
            return Instant.EPOCH;
        }

        @Override
        public Duration getDuration() {
            return Duration.ZERO;
        }

        @Override
        public Correlation correlate() {
            return this;
        }
    };

    private final Precorrelation precorrelation = new Precorrelation() {
        @Override
        public String getId() {
            return "id";
        }

        @Override
        public Instant getStart() {
            return Instant.EPOCH;
        }

        @Override
        public Correlation correlate() {
            return correlation;
        }
    };

    @Test
    void logsMethodUriHeadersAndBodyWhenEnabled() throws IOException {
        LogbookConfig config = new LogbookConfig(true, 100);
        HttpLogFormatter formatter = config.httpLogFormatter();

        MockHttpRequest request = MockHttpRequest.create()
                .withMethod("POST")
                .withScheme("https")
                .withHost("example.com")
                .withPort(java.util.Optional.empty())
                .withPath("/test")
                .withHeaders(HttpHeaders.of("X-Test", "value"))
                .withBodyAsString("hello");

        String log = formatter.format(precorrelation, request);
        assertTrue(log.contains("[IN]"));
        assertTrue(log.contains("id=id"));
        assertTrue(log.contains("method=POST"));
        assertTrue(log.contains("uri=https://example.com/test"));
        assertTrue(log.contains("headers={X-Test=[value]"));
        assertTrue(log.contains("body=hello"));
    }

    @Test
    void omitsBodyWhenDisabled() throws IOException {
        LogbookConfig config = new LogbookConfig(false, 100);
        HttpLogFormatter formatter = config.httpLogFormatter();

        MockHttpRequest request = MockHttpRequest.create()
                .withMethod("GET")
                .withScheme("https")
                .withHost("example.com")
                .withPort(java.util.Optional.empty())
                .withPath("/test")
                .withBodyAsString("secret");

        String log = formatter.format(precorrelation, request);
        assertFalse(log.contains("body="));
    }

    @Test
    void truncatesResponseBody() throws IOException {
        LogbookConfig config = new LogbookConfig(true, 5);
        HttpLogFormatter formatter = config.httpLogFormatter();

        MockHttpResponse response = MockHttpResponse.create()
                .withStatus(200)
                .withHeaders(HttpHeaders.of("Content-Type", "text/plain"))
                .withBodyAsString("123456789");

        String log = formatter.format(correlation, response);
        assertTrue(log.contains("[OUT]"));
        assertTrue(log.contains("id=id"));
        assertTrue(log.contains("status=200"));
        assertTrue(log.contains("headers={Content-Type=[text/plain]"));
        assertTrue(log.contains("body=12345"));
    }
}
