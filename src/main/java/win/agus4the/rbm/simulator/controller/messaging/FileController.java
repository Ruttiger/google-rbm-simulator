package win.agus4the.rbm.simulator.controller.messaging;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @PostMapping(value = "/v1/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Map<String, String>>> upload(@RequestPart("file") FilePart file) {
        String id = UUID.randomUUID().toString();
        return file.content()
                .doOnSubscribe(sub -> log.info("Uploading file {}", file.filename()))
                .then(Mono.just(ResponseEntity.ok(Map.of("name", "files/" + id))));
    }
}
