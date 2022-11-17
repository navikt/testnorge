package no.nav.testnav.proxies.dokarkivproxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MiljoController {

    @Value("${environments}")
    private String environments;

    @GetMapping(value = "/internal/miljoe", produces = MediaType.APPLICATION_JSON_VALUE)
    public String[] getMiljoe() {
        return environments.split(",");
    }
}
