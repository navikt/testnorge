package no.nav.dolly.budpro.test;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.libs.logging.Markers;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/log")
@Slf4j
public class LogController {

    @PostMapping("/insecure")
    String insecure(String message) {
        log.info("Logging insecure test message: {}", message, new NullPointerException("For reference"));
        return "Logged insecure message";
    }

    @PostMapping("/secure")
    String secure(String message) {
        log.info(Markers.SECURE, "Logging secure test message: {}", message, new NullPointerException("For reference"));
        return "Logged secure message";
    }

}
