package no.nav.testnav.libs.reactivecore.web;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;

/**
 * Convenience class for configuring {@link org.springframework.web.reactive.function.client.WebClient} instances.
 */
@UtilityClass
public class WebClientHeaders {

    /**
     * Add a bearer token to the headers.
     * @param token Token to add.
     * @return Consumer that adds the token to the headers.
     */
    public static Consumer<HttpHeaders> bearer(String token) {
        return headers -> headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

}
