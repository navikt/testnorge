package no.nav.testnav.apps.tilgangservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.tilgangservice.consumer.MaskinportenConsumer;

@RestController
@RequestMapping("/api/v1/tilgang")
@RequiredArgsConstructor
public class TilgangConsumer {

    private final MaskinportenConsumer consumer;

    @GetMapping()
    public Mono<String> tilgang() {
        return consumer.generateToken();
    }

}
