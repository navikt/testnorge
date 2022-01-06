package no.nav.testnav.mocks.tokendingsmock.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import no.nav.testnav.mocks.tokendingsmock.domain.AccessToken;
import no.nav.testnav.mocks.tokendingsmock.domain.Arguments;
import no.nav.testnav.mocks.tokendingsmock.service.JwtService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OauthAuthorizationServiceController {

    private static final String jwks;
    private static final String wellKnwon;

    static {
        jwks = loadJson("static/jwks.json");
        wellKnwon = loadJson("static/well-known.json");
    }

    private final JwtService jwtService;

    private static String loadJson(String path) {
        var resource = new ClassPathResource(path);
        try (final InputStreamReader stream = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return new BufferedReader(stream)
                    .lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            throw new RuntimeException("Feil med paring av " + path + ".", e);
        }
    }

    @GetMapping(value = "/jwks", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getJwks() {
        return Mono.just(jwks);
    }

    @GetMapping(value = "/.well-known/oauth-authorization-server", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getWellKnown() {
        return Mono.just(wellKnwon);
    }

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AccessToken> createToken(Arguments arguments) {
        return Mono.just(jwtService.createAccessToken(arguments.getAudience()));
    }

}
