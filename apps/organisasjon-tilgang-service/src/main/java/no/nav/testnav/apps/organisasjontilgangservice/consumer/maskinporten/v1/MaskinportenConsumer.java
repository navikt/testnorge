package no.nav.testnav.apps.organisasjontilgangservice.consumer.maskinporten.v1;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.organisasjontilgangservice.config.MaskinportenConfig;
import no.nav.testnav.apps.organisasjontilgangservice.consumer.maskinporten.v1.command.GetAccessTokenCommand;
import no.nav.testnav.apps.organisasjontilgangservice.consumer.maskinporten.v1.command.GetWellKnownCommand;
import no.nav.testnav.apps.organisasjontilgangservice.consumer.maskinporten.v1.dto.AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Component
public class MaskinportenConsumer {

    private final WebClient webClient;
    private final MaskinportenConfig maskinportenConfig;
    private final Mono<AccessToken> accessToken;

    public MaskinportenConsumer(MaskinportenConfig maskinportenConfig, WebClient.Builder webClientBuilder) {

        this.webClient = webClientBuilder
                .build();

        this.maskinportenConfig = maskinportenConfig;
        var wellKnownMono = cache(
                new GetWellKnownCommand(webClient, maskinportenConfig).call(),
                value -> Duration.ofDays(1)
        );
        this.accessToken = cache(
                wellKnownMono.flatMap(wellKnown -> new GetAccessTokenCommand(webClient, wellKnown, createJwtClaims(wellKnown.issuer())).call()),
                value -> Duration.ofSeconds(value.expiresIn() - 10L)
        );
    }

    public Mono<no.nav.testnav.apps.organisasjontilgangservice.domain.AccessToken> getAccessToken() {
        return accessToken.map(no.nav.testnav.apps.organisasjontilgangservice.domain.AccessToken::new);
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
            throw new RuntimeException(e);
        }
    }

    private static <T> Mono<T> cache(Mono<T> value, Function<? super T, Duration> ttlForValue) {
        return value.cache(
                ttlForValue,
                throwable -> Duration.ZERO,
                () -> Duration.ZERO
        );
    }

}
