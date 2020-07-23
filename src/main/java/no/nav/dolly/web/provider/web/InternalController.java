package no.nav.dolly.web.provider.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/internal")
public class InternalController {

    @Value("${dolly-web.internal.suspension-duration:5000}")
    private long suspensionDuration;

    @GetMapping("/isAlive")
    public void isAlive() {
    }

    @GetMapping("/isReady")
    public void isReady() {
    }

    @GetMapping("/suspend")
    public void suspend() {
        pause();
    }

    private void pause() {
        log.info("Received a suspension. Waiting for active requests to complete...");
        try {
            Thread.sleep(suspensionDuration);
        } catch (InterruptedException exception) {
            // It doesn't really matter.
        }
        log.info("Done...");
    }
}
