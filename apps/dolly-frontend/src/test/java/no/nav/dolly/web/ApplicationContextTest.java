package no.nav.dolly.web;

import no.nav.dolly.libs.test.DollyApplicationContextTest;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.web.config.csrf.CsrfProtection;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
class ApplicationContextTest extends DollyApplicationContextTest {

    @Test
    void shouldRequireCsrfTokenEvenWithBearerToken() {
        webTestClient
                .post()
                .uri("/session/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().valueEquals(CsrfProtection.CSRF_HEADER, CsrfProtection.CSRF_INVALID);
    }

    @Test
    void shouldRequireAuthenticationForCsrfToken() {
        webTestClient
                .get()
                .uri("/session/csrf")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldRejectMalformedBearerTokenWithUnauthorized() {
        webTestClient
                .get()
                .uri("/session/csrf")
                .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-jwt")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().value(HttpHeaders.WWW_AUTHENTICATE, header -> assertThat(header).contains("invalid_token"));
    }
}
