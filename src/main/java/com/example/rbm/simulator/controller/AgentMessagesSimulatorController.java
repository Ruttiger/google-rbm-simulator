package com.example.rbm.simulator.controller;

import com.example.rbm.simulator.dto.AgentMessageRequest;
import com.example.rbm.simulator.dto.AgentMessageResponse;
import com.example.rbm.simulator.dto.DeliveryState;
import com.example.rbm.simulator.dto.MessageState;
import com.example.rbm.simulator.error.ErrorResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/v1")
@Validated
public class AgentMessagesSimulatorController {

    @PostMapping(value = "/phones/{e164}/agentMessages", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> sendAgentMessage(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @PathVariable("e164") @Pattern(regexp = "^\\+\\d{7,15}$", message = "e164 must match ^\\+\\d{7,15}$") String e164,
            @RequestParam(value = "forceState", required = false) MessageState forceState,
            @RequestParam(value = "echo", defaultValue = "false") boolean echo,
            @Valid @RequestBody AgentMessageRequest request) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Mono.just(ErrorResponse.build(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header"));
        }

        if (contentType == null || !MediaType.APPLICATION_JSON_VALUE.equals(contentType)) {
            return Mono.just(ErrorResponse.build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Content-Type must be application/json"));
        }

        AgentMessageResponse response = new AgentMessageResponse();
        response.setName("phones/" + e164 + "/agentMessages/" + request.messageId());
        response.setSendTime(OffsetDateTime.now(ZoneOffset.UTC));
        response.setDeliveryState(new DeliveryState(forceState != null ? forceState : MessageState.QUEUED));
        response.setRepresentative(request.representative());
        if (echo) {
            response.setText(request.text());
            response.setRichCard(request.richCard());
        }

        return Mono.just(ResponseEntity.ok(response));
    }
}
