package no.nav.dolly.web.provider.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.dolly.web.service.LogService;
import no.nav.dolly.web.domain.LogEvent;
import no.nav.dolly.web.provider.web.dto.LogEventDTO;


@Slf4j
@RestController
@RequestMapping("/api/v1/logg")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @PostMapping
    public void logg(@RequestBody LogEventDTO dto, @RequestHeader("user-agent") String userAgent) {
        logService.log(new LogEvent(dto, userAgent));
    }
}
