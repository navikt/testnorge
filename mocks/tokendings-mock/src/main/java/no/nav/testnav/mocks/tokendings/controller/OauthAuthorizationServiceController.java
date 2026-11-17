package no.nav.testnav.mocks.tokendings.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.nimbusds.jwt.JWTParser;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.mocks.tokendings.domain.Arguments;
import no.nav.testnav.mocks.tokendings.service.JwtService;

import static no.nav.testnav.mocks.tokendings.TokendingsMockApplicationStarter.Utils.loadJson;

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

    private final JwtService service;

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
        var excludedClaims = Set.of("aud", "nbf", "iss", "exp", "iat", "jtl");
        var verify = service.verify(arguments.getSubjectToken());
        var claims = verify
                .getClaims()
                .entrySet()
                .stream()
                .filter(set -> !excludedClaims.contains(set.getKey()))
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().asString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return Mono.just(new AccessToken(service.jwtWith(claims, arguments.getAudience())));
    }

    @PostMapping(value = "/api/v1/introspect", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> introspect(@RequestBody Map<String, String> request) {
        try {
            var token = request.get("token");
            service.verify(token);
            var claims = new HashMap<>(JWTParser.parse(token).getJWTClaimsSet().toJSONObject());
            claims.put("active", true);
            return Mono.just(claims);
        } catch (JWTVerificationException | ParseException e) {
            return Mono.just(Map.of("active", false, "error", e.getMessage()));
        }
    }

}
