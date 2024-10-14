package no.nav.testnav.mocks.tokendings.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.mocks.tokendings.domain.Arguments;
import no.nav.testnav.mocks.tokendings.service.JwtService;

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
    public Mono<AccessToken> createToken(Arguments arguments) {
        var jwt = service.jwtWith(Map.of("pid", arguments.getPid()), arguments.getAudience());
        return Mono.just(new AccessToken(jwt));
    }

}
