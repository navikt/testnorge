package no.nav.dolly;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class MockedJwtAuthenticationTokenUtils {

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
                                        .claim("oid", "testbruker_en")
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
