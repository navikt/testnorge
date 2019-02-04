package no.nav.registre.aareg.provider.rs;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalController {

    @GetMapping("/isReady")
    public String isReady() {
        return "OK";
    }

    @GetMapping("/isAlive")
    public String isAlive() {
        return "OK";
    }
}
