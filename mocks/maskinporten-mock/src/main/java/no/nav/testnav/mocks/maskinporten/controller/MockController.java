package no.nav.testnav.mocks.maskinporten.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.mocks.maskinporten.domain.AccessToken;
import no.nav.testnav.mocks.maskinporten.domain.Arguments;
import no.nav.testnav.mocks.maskinporten.service.JwtService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mock")
public class MockController {

    private final JwtService service;

    @PostMapping(
            value = "/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<AccessToken> getToken(Arguments arguments) {
        return Mono.just(service.createAccessToken(arguments.getAudience()));
    }

}