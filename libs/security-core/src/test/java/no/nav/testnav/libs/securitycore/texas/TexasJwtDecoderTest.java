package no.nav.testnav.libs.securitycore.texas;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.BadJwtException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TexasJwtDecoderTest {

    private static final String TOKENX_ISSUER = "https://tokenx";

    private final TexasClient texasClient = mock(TexasClient.class);
    private final TexasJwtDecoder decoder = new TexasJwtDecoder(texasClient, Map.of(TOKENX_ISSUER, TexasClient.TOKENX));

    @Test
    void shouldBuildJwtFromClaimsReturnedByTexas() {
        var token = token(TOKENX_ISSUER);
        var expiresAt = Instant.now().plusSeconds(300).getEpochSecond();
        when(texasClient.introspect(TexasClient.TOKENX, token)).thenReturn(Mono.just(Map.of(
                "active", true,
                "iss", TOKENX_ISSUER,
                "sub", "subject",
                "aud", "app",
                "exp", expiresAt)));

        var jwt = decoder.decode(token).block();

        assertThat(jwt.getTokenValue()).isEqualTo(token);
        assertThat(jwt.getSubject()).isEqualTo("subject");
        assertThat(jwt.getAudience()).isEqualTo(List.of("app"));
        assertThat(jwt.getExpiresAt()).isEqualTo(Instant.ofEpochSecond(expiresAt));
        assertThat(jwt.getHeaders()).containsEntry("alg", "none");
        assertThat(jwt.getClaims()).doesNotContainKey("active");
    }

    @Test
    void shouldRejectTokenFromUnknownIssuerWithoutCallingTexas() {
        var jwt = decoder.decode(token("https://unknown"));

        assertThatThrownBy(jwt::block).isInstanceOf(BadJwtException.class);
        verifyNoInteractions(texasClient);
    }

    @Test
    void shouldRejectMalformedTokenWithoutCallingTexas() {
        var jwt = decoder.decode("not-a-jwt");

        assertThatThrownBy(jwt::block).isInstanceOf(BadJwtException.class);
        verifyNoInteractions(texasClient);
    }

    @Test
    void shouldRejectEncryptedTokenWithoutCallingTexas() {
        var header = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"RSA-OAEP\",\"enc\":\"A128GCM\"}".getBytes());

        var jwt = decoder.decode(header + ".a.b.c.d");

        assertThatThrownBy(jwt::block).isInstanceOf(BadJwtException.class);
        verifyNoInteractions(texasClient);
    }

    private static String token(String issuer) {
        return new PlainJWT(new JWTClaimsSet.Builder().issuer(issuer).subject("subject").build()).serialize();
    }
}
