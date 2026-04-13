package no.nav.dolly.proxy.controller;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.proxy.service.DokarkivUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/dokarkiv/upload")
@RequiredArgsConstructor
public class DokarkivUploadController {

    private final DokarkivUploadService uploadService;

    @PostMapping("/init")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Map<String, String>> initUpload() {
        return Mono.fromCallable(uploadService::initUpload)
                .map(uploadId -> Map.of("uploadId", uploadId));
    }

    @PostMapping("/{uploadId}/append")
    public Mono<Map<String, String>> appendChunk(@PathVariable String uploadId, @RequestBody String chunk) {
        return Mono.fromRunnable(() -> {
                    try {
                        uploadService.appendChunk(uploadId, chunk);
                    } catch (IllegalArgumentException e) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
                    }
                })
                .thenReturn(Map.of("status", "ok"));
    }

}
