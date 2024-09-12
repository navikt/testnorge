package no.nav.testnav.mocks.tokendings.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static no.nav.testnav.mocks.tokendings.TokendingsMockApplicationStarter.Utils.loadJson;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String JWK;

    static {
        JWK = loadJson("static/jwk.json");
    }

    @SneakyThrows
    public DecodedJWT verify(String jwt) {
        var key = RSAKey.parse(JWK);
        return JWT
                .require(Algorithm.RSA256(key.toRSAPublicKey(), (RSAPrivateKey) key.toPrivateKey()))
                .build()
                .verify(jwt);
    }


    @SneakyThrows
    public String jwtWith(Map<String, String> claims, String audience) {
        var date = Calendar.getInstance();
        var builder = JWT
                .create()
                .withIssuer("http://tokendings:8080")
                .withIssuedAt(date.getTime())
                .withNotBefore(date.getTime())
                .withAudience(audience)
                .withJWTId(UUID.randomUUID().toString())
                .withExpiresAt(new Date(date.getTimeInMillis() + (2 * 60 * 60 * 1000)));
        claims.forEach(builder::withClaim);
        var privateKey = (RSAPrivateKey) RSAKey
                .parse(JWK)
                .toPrivateKey();
        return builder
                .sign(Algorithm.RSA256(null, privateKey));
    }

}
