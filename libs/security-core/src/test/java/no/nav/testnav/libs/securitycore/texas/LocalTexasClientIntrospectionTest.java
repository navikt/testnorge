package no.nav.testnav.libs.securitycore.texas;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import no.nav.testnav.libs.securitycore.domain.azuread.AzureClientCredential;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.BadJwtException;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LocalTexasClientIntrospectionTest {

    private static final String ENTRA_ID_CLIENT_ID = "local-client";
    private static final String TOKENX_CLIENT_ID = "dev-gcp:dolly:app";

    private DisposableServer server;
    private RSAKey signingKey;
    private String issuer;
    private AzureClientCredential credential;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        signingKey = new RSAKeyGenerator(2048).keyID("test-key").generate();
        var jwks = new JWKSet(signingKey.toPublicJWK()).toString();
        server = HttpServer.create()
                .port(0)
                .route(routes -> routes
                        .get("/.well-known/openid-configuration", (request, response) -> response
                                .header("Content-Type", "application/json")
                                .sendString(Mono.just("{\"issuer\":\"%s\",\"jwks_uri\":\"%s/jwks\"}".formatted(issuer, issuer))))
                        .get("/jwks", (request, response) -> response
                                .header("Content-Type", "application/json")
                                .sendString(Mono.just(jwks))))
                .bindNow();
        issuer = "http://localhost:" + server.port();
        credential = mock(AzureClientCredential.class);
        when(credential.getTokenEndpoint()).thenReturn(issuer + "/entraid/oauth2/token");
        when(credential.getClientId()).thenReturn(ENTRA_ID_CLIENT_ID);
    }

    @AfterEach
    void tearDown() {
        server.disposeNow();
    }

    @Test
    void shouldReturnClaimsForEntraIdTokenWithLocalAudience() {
        var claims = texasClient(null).introspect(TexasClient.ENTRA_ID, signedToken(ENTRA_ID_CLIENT_ID)).block();

        assertThat(claims).containsEntry("sub", "test-subject");
    }

    @Test
    void shouldRejectEntraIdTokenWithOtherAudience() {
        var claims = texasClient(null).introspect(TexasClient.ENTRA_ID, signedToken("another-app"));

        assertThatThrownBy(claims::block).isInstanceOf(BadJwtException.class);
    }

    @Test
    void shouldValidateTokenXTokenAgainstConfiguredClientId() {
        var claims = texasClient(TOKENX_CLIENT_ID).introspect(TexasClient.TOKENX, signedToken(TOKENX_CLIENT_ID)).block();

        assertThat(claims).containsEntry("sub", "test-subject");
    }

    @Test
    void shouldRejectTokenXTokenWhenTokenXClientIdIsMissing() {
        var claims = texasClient(null).introspect(TexasClient.TOKENX, signedToken(TOKENX_CLIENT_ID));

        assertThatThrownBy(claims::block).isInstanceOf(BadJwtException.class);
    }

    private LocalTexasClient texasClient(String tokenXClientId) {
        return new LocalTexasClient(issuer + "/api/v1/token", issuer + "/api/v1/token/exchange", null, credential, issuer, issuer, tokenXClientId);
    }

    @SneakyThrows
    private String signedToken(String audience) {
        var now = Instant.now();
        var claims = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .audience(audience)
                .subject("test-subject")
                .issueTime(Date.from(now))
                .notBeforeTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(300)))
                .build();
        var jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(signingKey.getKeyID()).build(), claims);
        jwt.sign(new RSASSASigner(signingKey));
        return jwt.serialize();
    }
}
