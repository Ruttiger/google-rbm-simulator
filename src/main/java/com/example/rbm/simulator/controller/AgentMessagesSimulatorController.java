package com.example.rbm.simulator.controller;

import com.example.rbm.simulator.dto.*;
import com.example.rbm.simulator.error.ErrorResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/v1")
@Validated
public class AgentMessagesSimulatorController {

    @PostMapping(value = "/phones/{e164}/agentMessages", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Object>> receiveAgentMessage(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false) String contentType,
            @PathVariable("e164")
            @Pattern(regexp = "^\\+\\d{7,15}$", message = "Invalid E.164 format") String e164,
            @RequestParam(value = "forceState", required = false) MessageState forceState,
            @RequestParam(value = "echo", defaultValue = "false") boolean echo,
            @Valid @RequestBody AgentMessageRequest request
    ) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header")));
        }

        if (contentType == null || !MediaType.APPLICATION_JSON.isCompatibleWith(MediaType.parseMediaType(contentType))) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(ErrorResponse.of(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Content-Type must be application/json")));
        }

        MessageState state = forceState != null ? forceState : MessageState.QUEUED;
        AgentMessageResponse response = new AgentMessageResponse();
        response.setName("phones/" + e164 + "/agentMessages/" + request.messageId());
        response.setSendTime(OffsetDateTime.now());
        response.setDeliveryState(new DeliveryState(state));
        response.setRepresentative(request.representative());
        if (echo) {
            response.setText(request.text());
            response.setRichCard(request.richCard());
        }
        return Mono.just(ResponseEntity.ok(response));
    }
}
