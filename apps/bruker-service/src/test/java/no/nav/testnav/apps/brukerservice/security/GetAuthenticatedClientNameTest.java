package no.nav.testnav.apps.brukerservice.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class GetAuthenticatedClientNameTest {

    private final GetAuthenticatedClientName getAuthenticatedClientName = new GetAuthenticatedClientName();

    @Test
    void shouldReturnVerifiedAzureClientName() {
        var authentication = new JwtAuthenticationToken(Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("azp_name", "team-dolly-local")
                .build());

        StepVerifier.create(Mono.defer(getAuthenticatedClientName::call)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)))
                .expectNext("team-dolly-local")
                .verifyComplete();
    }
}
