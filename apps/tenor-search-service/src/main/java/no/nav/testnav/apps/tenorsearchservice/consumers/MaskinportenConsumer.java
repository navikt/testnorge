package no.nav.testnav.apps.tenorsearchservice.consumers;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.config.MaskinportenConfig;
import no.nav.testnav.apps.tenorsearchservice.consumers.command.GetAccessTokenCommand;
import no.nav.testnav.apps.tenorsearchservice.consumers.command.GetWellKnownCommand;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Component
public class MaskinportenConsumer {

    private final MaskinportenConfig maskinportenConfig;
    private final Mono<AccessToken> accessToken;

    public MaskinportenConsumer(
            MaskinportenConfig maskinportenConfig,
            WebClient webClient
    ) {
        this.maskinportenConfig = maskinportenConfig;
        var wellKnownMono = cache(
                new GetWellKnownCommand(webClient, maskinportenConfig).call(),
                value -> Duration.ofDays(7)
        );
        this.accessToken = cache(
                wellKnownMono.flatMap(wellKnown -> new GetAccessTokenCommand(webClient, wellKnown, createJwtClaims(wellKnown.issuer())).call()),
                value -> Duration.ofSeconds(value.expiresIn() - 10L)
        );
    }

    private static <T> Mono<T> cache(Mono<T> value, Function<? super T, Duration> ttlForValue) {
        return value.cache(
                ttlForValue,
                throwable -> Duration.ZERO,
                () -> Duration.ZERO
        );
    }

    public Mono<no.nav.testnav.apps.tenorsearchservice.domain.AccessToken> getAccessToken() {
        return accessToken.map(no.nav.testnav.apps.tenorsearchservice.domain.AccessToken::new);
    }

    @SneakyThrows
    private String createJwtClaims(String audience) {
        Instant now = Instant.now();
        var rsaKey = RSAKey.parse(maskinportenConfig.getJwkPrivate());
        return createSignedJWT(rsaKey,
                new JWTClaimsSet.Builder()
                        .audience(audience)
                        .claim("scope", maskinportenConfig.getScope())
                        .issuer(maskinportenConfig.getClientId())
                        .issueTime(Date.from(now))
                        .expirationTime(Date.from(now.plusSeconds(119)))
                        .jwtID(UUID.randomUUID().toString())
                        .build())
                .serialize();
    }

    private SignedJWT createSignedJWT(RSAKey rsaJwk, JWTClaimsSet claimsSet) {
        try {
            JWSHeader.Builder header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .keyID(rsaJwk.getKeyID())
                    .type(JOSEObjectType.JWT);
            SignedJWT signedJWT = new SignedJWT(header.build(), claimsSet);
            JWSSigner signer = new RSASSASigner(rsaJwk.toPrivateKey());
            signedJWT.sign(signer);
            return signedJWT;
        } catch (JOSEException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to sign JWT", e);
        }
    }
}
