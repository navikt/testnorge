package no.nav.dolly.web.provider.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import no.nav.dolly.web.domain.LogEvent;
import no.nav.dolly.web.provider.web.dto.LogEventDTO;
import no.nav.dolly.web.service.LogService;


@Slf4j
@RestController
@RequestMapping("/api/dolly-logg")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @PostMapping
    public Mono<ResponseEntity<HttpStatus>> logg(
            @RequestBody LogEventDTO dto,
            @RequestHeader("user-agent") String userAgent,
            @RequestHeader("host") String host
    ) {
        return logService
                .log(new LogEvent(dto, userAgent, host))
                .map(response -> ResponseEntity.noContent().build());
    }
}
