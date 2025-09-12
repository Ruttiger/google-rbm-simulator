package com.messi.rbm.simulator.service;

import com.messi.rbm.simulator.model.Message;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store for Business Messaging simulation state.
 */
@Service
@SuppressFBWarnings("ALL")
public class BusinessMessagingService {

    private final Map<String, Map<String, Message>> agentMessages = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> testers = new ConcurrentHashMap<>();

    public Mono<Void> saveAgentMessage(String msisdn, String messageId, Message message) {
        agentMessages.computeIfAbsent(msisdn, k -> new ConcurrentHashMap<>()).put(messageId, message);
        return Mono.empty();
    }

    public Mono<Message> getAgentMessage(String msisdn, String messageId) {
        Map<String, Message> map = agentMessages.get(msisdn);
        return Mono.justOrEmpty(map == null ? null : map.get(messageId));
    }

    public Mono<Void> deleteAgentMessage(String msisdn, String messageId) {
        Map<String, Message> map = agentMessages.get(msisdn);
        if (map != null) {
            map.remove(messageId);
        }
        return Mono.empty();
    }

    public Mono<Map<String, String>> addTester(String agentId, String msisdn) {
        testers.computeIfAbsent(agentId, k -> ConcurrentHashMap.newKeySet()).add(msisdn);
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
        return Mono.just(users.stream().filter(set::contains).toList());
    }

    public Mono<Map<String, Object>> capabilities(String agentId, String msisdn) {
        if (!isTester(agentId, msisdn)) {
            return Mono.empty();
        }
        return Mono.just(Map.of(
                "name", String.format("phones/%s/capabilities", msisdn),
                "rcsBusinessMessaging", true
        ));
    }

    public void reset() {
        agentMessages.clear();
        testers.clear();
    }
}
