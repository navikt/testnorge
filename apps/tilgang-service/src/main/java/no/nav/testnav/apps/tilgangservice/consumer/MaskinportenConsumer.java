package no.nav.testnav.apps.tilgangservice.consumer;

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
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import no.nav.testnav.apps.tilgangservice.config.MaskinportenConfig;
import no.nav.testnav.apps.tilgangservice.config.WellKnown;

@Slf4j
@Component
public class MaskinportenConsumer {

    private final WebClient webClient;
    private final MaskinportenConfig maskinportenConfig;

    public MaskinportenConsumer(MaskinportenConfig maskinportenConfig) {
        this.webClient = WebClient.builder().build();
        this.maskinportenConfig = maskinportenConfig;
    }

    private Mono<WellKnown> getWellKnown() {
        return webClient.get()
                .uri(maskinportenConfig.getWellKnownUrl())
                .retrieve()
                .bodyToMono(WellKnown.class)
                .cache(
                        value -> Duration.ofDays(1),
                        value -> Duration.ZERO,
                        () -> Duration.ZERO
                );
    }

    public Mono<String> generateToken() {
        return getWellKnown().flatMap(wellKnown ->
                webClient.post()
                        .uri(wellKnown.token_endpoint())
                        .body(BodyInserters
                                .fromFormData("grant_type", wellKnown.grant_types_supported().get(0))
                                .with("assertion", createJwtClaims(wellKnown.issuer()))
                        )
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnError(throwable -> {
                            if (throwable instanceof WebClientResponseException) {
                                log.error("Feil ved generering av token. \n{}", ((WebClientResponseException) throwable).getResponseBodyAsString());
                            }
                            log.error("Feil ved generering av token.", throwable);
                        })

        );
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

}
