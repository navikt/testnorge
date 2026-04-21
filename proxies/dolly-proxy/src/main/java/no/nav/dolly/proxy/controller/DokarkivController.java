package no.nav.dolly.proxy.controller;

import no.nav.dolly.proxy.route.Dokarkiv;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Arrays;

/**
 * Support controller for Dokarkiv. Called by dolly-backend's {@code DokarkivGetMiljoeCommand}.
 *
 * @see no.nav.dolly.proxy.route.Dokarkiv
 */
@RestController
@RequestMapping("/dokarkiv")
class DokarkivController {

    private final String[] environments;

    DokarkivController() {
        environments = Arrays
                .stream(Dokarkiv.SpecialCase.values())
                .map(Dokarkiv.SpecialCase::getCode)
                .toArray(String[]::new);
    }

    @GetMapping(value = "/rest/miljoe", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<String[]> getEnvironments() {
        return Mono.just(environments);
    }

}
