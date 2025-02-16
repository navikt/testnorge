package no.nav.dolly.web.provider.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.web.domain.LogEvent;
import no.nav.dolly.web.provider.web.dto.LogEventDTO;
import no.nav.dolly.web.service.LogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


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
            @RequestHeader("host") String host,
            ServerWebExchange exchange
    ) {
        return logService
                .log(new LogEvent(dto, userAgent, host), exchange)
                .then(Mono.fromCallable(() -> ResponseEntity.noContent().build()));
    }
}
