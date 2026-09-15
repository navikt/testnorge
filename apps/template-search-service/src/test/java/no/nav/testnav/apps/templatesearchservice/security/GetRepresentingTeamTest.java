package no.nav.testnav.apps.templatesearchservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import no.nav.dolly.libs.security.config.ReactiveRequestContext;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetRepresentingTeamTest {

    private static final String SECRET = "secret";
    private static final String AZURE_USER_ID = "00000000-0000-0000-0000-000000000000";

    private final GetRepresentingTeam getRepresentingTeam = new GetRepresentingTeam(SECRET);

    @Test
    void shouldReadRepresentingTeamFromVerifiedAzureUserJwt() {
        assertThat(resolveAzureRepresentingTeam(userJwt(AZURE_USER_ID, "team-bruker-id-42")))
                .isEqualTo("team-bruker-id-42");
    }

    @Test
    void shouldReturnEmptyWhenAzureUserJwtHasNoTeamClaim() {
        assertThat(resolveAzureRepresentingTeam(userJwt(AZURE_USER_ID, null))).isNull();
    }

    @Test
    void shouldRejectAzureUserJwtBelongingToAnotherUser() {
        assertThatThrownBy(() ->
                resolveAzureRepresentingTeam(userJwt("another-user-id", "team-bruker-id-42")))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void shouldRejectUserJwtWithInvalidSignature() {
        var invalidToken = JWT.create()
                .withClaim(UserConstant.USER_CLAIM_ID, AZURE_USER_ID)
                .withClaim(UserConstant.USER_CLAIM_REPRESENTING_TEAM, "team-bruker-id-42")
                .sign(Algorithm.HMAC256("different-secret"));

        assertThatThrownBy(() -> resolveAzureRepresentingTeam(invalidToken))
                .isInstanceOf(com.auth0.jwt.exceptions.SignatureVerificationException.class);
    }

    private String resolveAzureRepresentingTeam(String userJwt) {
        var request = MockServerHttpRequest.get("/test")
                .header(UserConstant.USER_HEADER_JWT, userJwt)
                .build();
        var exchange = MockServerWebExchange.from(request);
        var result = new AtomicReference<String>();
        WebFilterChain chain = _ -> getRepresentingTeam.call()
                .doOnNext(result::set)
                .then();
        var securityContext = new SecurityContextImpl(new JwtAuthenticationToken(azureJwt()));

        new ReactiveRequestContext()
                .filter(exchange, chain)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                .block();

        return result.get();
    }

    private static Jwt azureJwt() {
        return Jwt.withTokenValue("azure-token")
                .header("alg", "none")
                .claim("iss", "https://login.microsoftonline.com/tenant/v2.0")
                .claim("oid", AZURE_USER_ID)
                .build();
    }

    private static String userJwt(String userId, String representingTeam) {
        var issuedAt = Instant.now();
        var builder = JWT.create()
                .withIssuer("issuer")
                .withClaim(UserConstant.USER_CLAIM_ID, userId)
                .withClaim(UserConstant.USER_CLAIM_USERNAME, "Azure User")
                .withClaim(UserConstant.USER_CLAIM_ORG, UserConstant.NAV_ORGANIZATION_NUMBER)
                .withIssuedAt(issuedAt)
                .withExpiresAt(issuedAt.plusSeconds(3600));
        if (representingTeam != null) {
            builder.withClaim(UserConstant.USER_CLAIM_REPRESENTING_TEAM, representingTeam);
        }
        return builder.sign(Algorithm.HMAC256(SECRET));
    }
}
