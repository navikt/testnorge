package no.nav.testnav.mocks.azuremock.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.mocks.azuremock.domain.Arguments;
import no.nav.testnav.mocks.azuremock.service.JwtService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mock")
public class MockController {

    private final JwtService jwtService;

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AccessToken> createToken(Arguments arguments) {
        var jwt = jwtService.jwtWith(Map.of("oid", arguments.getOid(), "sub", arguments.getSub()), arguments.getAudience());
        return Mono.just(new AccessToken(jwt));
    }

}
