package win.agus4the.rbm.simulator.service.messaging;

import win.agus4the.rbm.simulator.model.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store for Business Messaging simulation state.
 */
@Service
public class BusinessMessagingService {

    private final Map<String, Map<String, Message>> agentMessages = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> testers = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(BusinessMessagingService.class);

    public Mono<Void> saveAgentMessage(String msisdn, String messageId, Message message) {
        agentMessages.computeIfAbsent(msisdn, k -> new ConcurrentHashMap<>()).put(messageId, message);
        log.info("Saved agent message msisdn={} messageId={} type={}", msisdn, messageId, messageType(message));
        return Mono.empty();
    }

    public Mono<Message> getAgentMessage(String msisdn, String messageId) {
        Map<String, Message> map = agentMessages.get(msisdn);
        Message msg = map == null ? null : map.get(messageId);
        if (msg != null) {
            log.debug("Retrieved agent message msisdn={} messageId={}", msisdn, messageId);
        } else {
            log.debug("Agent message not found msisdn={} messageId={}", msisdn, messageId);
        }
        return Mono.justOrEmpty(msg);
    }

    public Mono<Void> deleteAgentMessage(String msisdn, String messageId) {
        Map<String, Message> map = agentMessages.get(msisdn);
        if (map != null) {
            map.remove(messageId);
        }
        log.info("Deleted agent message msisdn={} messageId={}", msisdn, messageId);
        return Mono.empty();
    }

    public Mono<Map<String, String>> addTester(String agentId, String msisdn) {
        testers.computeIfAbsent(agentId, k -> ConcurrentHashMap.newKeySet()).add(msisdn);
        log.info("Tester added msisdn={} agentId={}", msisdn, agentId);
        return Mono.just(Map.of(
                "name", String.format("agents/%s/phones/%s/testers", agentId, msisdn),
                "inviteStatus", "ACCEPTED"
        ));
    }

    public boolean isTester(String agentId, String msisdn) {
        return testers.getOrDefault(agentId, Set.of()).contains(msisdn);
    }

    public Mono<List<String>> reachableUsers(String agentId, List<String> users) {
        Set<String> set = testers.getOrDefault(agentId, Set.of());
        List<String> reachable = users.stream().filter(set::contains).toList();
        log.debug("Reachable users agentId={} count={}", agentId, reachable.size());
        return Mono.just(reachable);
    }

    public Mono<Map<String, Object>> capabilities(String agentId, String msisdn) {
        if (!isTester(agentId, msisdn)) {
            log.debug("Capabilities requested for non-tester msisdn={} agentId={}", msisdn, agentId);
            return Mono.empty();
        }
        log.info("Returning capabilities for msisdn={} agentId={}", msisdn, agentId);
        return Mono.just(Map.of(
                "name", String.format("phones/%s/capabilities", msisdn),
                "rcsBusinessMessaging", true
        ));
    }

    public void reset() {
        agentMessages.clear();
        testers.clear();
        log.warn("BusinessMessagingService state reset");
    }

    private String messageType(Message message) {
        if (message.contentMessage() != null && message.contentMessage().richCard() != null) {
            return "RICH_CARD";
        }
        return "TEXT";
    }
}
