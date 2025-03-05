package no.nav.testnav.altinn3tilgangservice.consumer.maskinporten;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.config.MaskinportenConfig;
import no.nav.testnav.altinn3tilgangservice.consumer.maskinporten.command.GetAccessTokenCommand;
import no.nav.testnav.altinn3tilgangservice.consumer.maskinporten.command.GetWellKnownCommand;
import no.nav.testnav.altinn3tilgangservice.consumer.maskinporten.dto.AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaskinportenConsumer {

    private final WebClient webClient;
    private final MaskinportenConfig maskinportenConfig;

    public Mono<String> getAccessToken() {

        return new GetWellKnownCommand(webClient, maskinportenConfig).call()
                .flatMap(wellKnown -> new GetAccessTokenCommand(webClient, wellKnown,
                        createJwtClaims(wellKnown.issuer())).call())
                .map(AccessToken::accessToken);
    }

    @SneakyThrows
    private String createJwtClaims(String audience) {

        var now = Instant.now();
        var rsaKey = RSAKey.parse(maskinportenConfig.getMaskinportenClientJwk());
        return createSignedJWT(rsaKey,
                new JWTClaimsSet.Builder()
                        .audience(audience)
                        .claim("scope", maskinportenConfig.getMaskinportenScopes())
                        .issuer(maskinportenConfig.getMaskinportenClientId())
                        .issueTime(Date.from(now))
                        .expirationTime(Date.from(now.plusSeconds(120)))
                        .jwtID(UUID.randomUUID().toString())
                        .build())
                .serialize();
    }

    @SneakyThrows
    private SignedJWT createSignedJWT(RSAKey rsaJwk, JWTClaimsSet claimsSet) {

        var header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(rsaJwk.getKeyID())
                .type(JOSEObjectType.JWT);
        var signedJWT = new SignedJWT(header.build(), claimsSet);
        var signer = new RSASSASigner(rsaJwk.toPrivateKey());
        signedJWT.sign(signer);

        return signedJWT;
    }
}
