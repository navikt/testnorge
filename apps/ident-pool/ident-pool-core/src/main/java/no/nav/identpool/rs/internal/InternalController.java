package no.nav.identpool.rs.internal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalController {

    @GetMapping("/isAlive")
    public void isAlive() {
        // Do nothing
    }

    @GetMapping("/isReady")
    public void isReady() {
        // Do nothing
    }
}
