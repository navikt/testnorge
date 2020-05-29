package no.nav.registre.tp.provider.rs;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InternalController {

    @GetMapping("/internal/isAlive")
    public String isAlive() {
        return "ok";
    }

    @GetMapping("/internal/isReady")
    public String isReady() {
        return "ok";
    }

}
