package no.nav.testnav.apps.statusfrontend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NaisController {

    @GetMapping("/internal/isAlive")
    public String isAlive() {
        return "OK";
    }

    @GetMapping("/internal/isReady")
    public String isReady() {
        return "OK";
    }
}
