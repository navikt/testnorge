package no.nav.testnav.mocks.azuremock.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String jwtSecret;

    static {
        jwtSecret = loadJson("static/jwk.json");
    }

    private static String loadJson(String path) {
        var resource = new ClassPathResource(path);
        try (final InputStreamReader stream = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return new BufferedReader(stream)
                    .lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            throw new RuntimeException("Feil med paring av " + path + ".", e);
        }
    }


    @SneakyThrows
    public DecodedJWT verify(String jwt) {
        var key = RSAKey.parse(jwtSecret);
        var verifier = JWT
                .require(Algorithm.RSA256(key.toRSAPublicKey(), (RSAPrivateKey) key.toPrivateKey()))
                .build();
        return verifier.verify(jwt);
    }


    @SneakyThrows
    public String jwtWith(Map<String, String> claims, String audience) {
        var date = Calendar.getInstance();
        var builder = JWT
                .create()
                .withIssuer("http://azuread:8080")
                .withIssuedAt(date.getTime())
                .withNotBefore(date.getTime())
                .withAudience(audience)
                .withJWTId(UUID.randomUUID().toString())
                .withExpiresAt(new Date(date.getTimeInMillis() + (2 * 60 * 60 * 1000)));
        claims.forEach(builder::withClaim);

        var privateKey = RSAKey.parse(jwtSecret).toPrivateKey();

        return builder
                .sign(Algorithm.RSA256(null, (RSAPrivateKey) privateKey));
    }

}
