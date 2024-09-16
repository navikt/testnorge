package no.nav.testnav.mocks.maskinporten.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.SneakyThrows;
import no.nav.testnav.mocks.maskinporten.domain.AccessToken;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static no.nav.testnav.mocks.maskinporten.MaskinportenMockApplicationStarter.Utils.loadJson;

@Service
public class JwtService {

    private static final String JWK;

    static {
        JWK = loadJson("static/jwk.json");
    }

    @SneakyThrows
    public AccessToken createAccessToken(String audience) {

        var date = Calendar.getInstance();
        var expiresAt = date.getTimeInMillis() + (60 * 60 * 1000);
        var builder = JWT
                .create()
                .withIssuer("http://tokendings:8080")
                .withIssuedAt(date.getTime())
                .withNotBefore(date.getTime())
                .withAudience(audience)
                .withJWTId(UUID.randomUUID().toString())
                .withExpiresAt(new Date(expiresAt));
        var privateKey = RSAKey.parse(JWK).toPrivateKey();
        return new AccessToken(
                builder.sign(Algorithm.RSA256(null, (RSAPrivateKey) privateKey)),
                "Bearer",
                60 * 60 * 1000,
                audience
        );

    }

}
