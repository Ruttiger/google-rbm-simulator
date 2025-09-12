package com.messi.rbm.simulator.controller.messaging;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
@SuppressFBWarnings("ALL")
public class FileController {

    @PostMapping(value = "/v1/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Map<String, String>>> upload(@RequestPart("file") FilePart file) {
        String id = UUID.randomUUID().toString();
        return file.content().then(Mono.just(ResponseEntity.ok(Map.of("name", "files/" + id))));
    }
}
