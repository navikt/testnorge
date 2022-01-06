package no.nav.testnav.mocks.tokendingsmock.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nimbusds.jose.jwk.RSAKey;
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
import java.util.UUID;
import java.util.stream.Collectors;

import no.nav.testnav.mocks.tokendingsmock.domain.AccessToken;

@Service
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

        var privateKey = RSAKey.parse(jwtSecret).toPrivateKey();

        return new AccessToken(
                builder.sign(Algorithm.RSA256(null, (RSAPrivateKey) privateKey)),
                "Bearer",
                60 * 60 * 1000,
                audience

        );
    }

}
