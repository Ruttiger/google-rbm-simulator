package win.agus4the.rbm.simulator.config;

import org.junit.jupiter.api.Test;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.test.MockHttpRequest;
import org.zalando.logbook.test.MockHttpResponse;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FunctionalHttpLogFormatterTest {

    @Test
    void formatsRequestAndResponseUsingSuppliedFunctions() throws IOException {
        FunctionalHttpLogFormatter formatter = new FunctionalHttpLogFormatter(
                (pre, req) -> req.getMethod() + " " + req.getRequestUri(),
                (corr, res) -> "status=" + res.getStatus()
        );

        MockHttpRequest request = MockHttpRequest.create()
                .withMethod("GET")
                .withScheme("https")
                .withHost("example.com")
                .withPort(java.util.Optional.empty())
                .withPath("/test");
        MockHttpResponse response = MockHttpResponse.create().withStatus(200);

        Correlation correlation = new Correlation() {
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

        Precorrelation precorrelation = new Precorrelation() {
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

        assertEquals("GET https://example.com/test", formatter.format(precorrelation, request));
        assertEquals("status=200", formatter.format(correlation, response));
    }
}
