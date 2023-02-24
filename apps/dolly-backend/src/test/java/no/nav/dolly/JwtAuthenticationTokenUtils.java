package no.nav.dolly;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class JwtAuthenticationTokenUtils {

    /**
     * @see no.nav.dolly.util.CurrentAuthentication
     */
    public static void setJwtAuthenticationToken() {
        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        new JwtAuthenticationToken(
                                Jwt
                                        .withTokenValue("test")
                                        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                                        .claim("oid", "123")
                                        .claim("name", "BRUKER")
                                        .claim("epost", "@@@@")
                                        .build()
                        )
                );
    }

    public static void clearJwtAuthenticationToken() {
        SecurityContextHolder
                .getContext()
                .setAuthentication(null);
    }

}
