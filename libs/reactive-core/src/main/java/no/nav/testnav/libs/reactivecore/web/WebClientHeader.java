package no.nav.testnav.libs.reactivecore.web;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;

/**
 * Convenience class for configuring {@link org.springframework.web.reactive.function.client.WebClient} instances.
 */
@UtilityClass
public class WebClientHeader {

    /**
     * Add a bearer token to the headers.
     * @param token Token to add.
     * @return Consumer that adds the token to the headers.
     */
    public static Consumer<HttpHeaders> bearer(String token) {
        return headers -> headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    /**
     * Add basic authentication to the headers.
     * @param username Username.
     * @param password Password.
     * @return Consumer that adds the basic authentication to the headers.
     */
    public static Consumer<HttpHeaders> basic(String username, String password) {
        return headers -> headers.setBasicAuth(username, password);
    }

    public static Consumer<HttpHeaders> jwt(String userJwt) {
        return headers -> headers.set("User-Jwt", userJwt);
    }
}
