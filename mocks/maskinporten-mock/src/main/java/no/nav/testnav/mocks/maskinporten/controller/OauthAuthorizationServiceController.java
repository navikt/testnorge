package no.nav.testnav.mocks.maskinporten.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.mocks.maskinporten.domain.AccessToken;
import no.nav.testnav.mocks.maskinporten.domain.Arguments;
import no.nav.testnav.mocks.maskinporten.service.JwtService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static no.nav.testnav.mocks.maskinporten.MaskinportenMockApplicationStarter.Utils.loadJson;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OauthAuthorizationServiceController {

    private static final String JWKS;
    private static final String WELL_KNOWN;

    static {
        JWKS = loadJson("static/jwks.json");
        WELL_KNOWN = loadJson("static/well-known.json");
    }

    private final JwtService jwtService;

    @GetMapping(value = "/jwks", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getJWKS() {
        return Mono.just(JWKS);
    }

    @GetMapping(value = "/.well-known/oauth-authorization-server", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getWellKnown() {
        return Mono.just(WELL_KNOWN);
    }

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AccessToken> createToken(Arguments arguments) {
        return Mono.just(jwtService.createAccessToken(arguments.getAudience()));
    }

}
