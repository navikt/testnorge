package no.nav.testnav.mocks.tokendings.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static no.nav.testnav.mocks.tokendings.TokendingsMockApplicationStarter.Utils.loadJson;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String JWK_JSON = loadJson("static/jwk.json");
    private static final RSAKey RSA_KEY = parseRsaKey(JWK_JSON);
    private static final RSAPublicKey PUBLIC_KEY = toPublicKey(RSA_KEY);
    private static final RSAPrivateKey PRIVATE_KEY = toPrivateKey(RSA_KEY);

    public DecodedJWT verify(String jwt) {
        return JWT
                .require(Algorithm.RSA256(PUBLIC_KEY, null))
                .build()
                .verify(jwt);
    }

    public String jwtWith(Map<String, String> claims, String audience) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        var builder = JWT
                .create()
                .withIssuer("http://tokendings:8080")
                .withIssuedAt(now)
                .withNotBefore(now)
                .withAudience(audience)
                .withJWTId(UUID.randomUUID().toString())
                .withExpiresAt(new Date(calendar.getTimeInMillis() + (2L * 60 * 60 * 1000)));

        claims.forEach(builder::withClaim);

        return builder.sign(Algorithm.RSA256(null, PRIVATE_KEY));
    }

    private static RSAKey parseRsaKey(String jwkJson) {
        try {
            return RSAKey.parse(jwkJson);
        } catch (ParseException e) {
            throw new IllegalStateException("Unable to parse JWK", e);
        }
    }

    private static RSAPublicKey toPublicKey(RSAKey rsaKey) {
        try {
            return rsaKey.toRSAPublicKey();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create RSA public key", e);
        }
    }

    private static RSAPrivateKey toPrivateKey(RSAKey rsaKey) {
        try {
            return rsaKey.toRSAPrivateKey();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create RSA private key", e);
        }
    }
}
