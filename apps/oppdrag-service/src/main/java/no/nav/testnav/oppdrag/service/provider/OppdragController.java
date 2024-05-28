package no.nav.testnav.oppdrag.service.provider;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/oppdrag")
public class OppdragController {

    @PostMapping
    public void sendOppdrag() {}
}
