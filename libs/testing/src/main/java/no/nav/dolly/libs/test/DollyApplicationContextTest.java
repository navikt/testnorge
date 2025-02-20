package no.nav.dolly.libs.test;

import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Common test base for all tests that simply want to check the application context.
 * Also does a simple check to see if the application is alive and ready.
 * Note that this class is intentionally not annotated with {@link DollySpringBootTest}, for readability.
 */
public class DollyApplicationContextTest {

    @Setter(onMethod_ = @Autowired)
    private WebTestClient webTestClient;

    @Test
    void isAlive() {
        webTestClient
                .get()
                .uri("/internal/isAlive")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void isReady() {
        webTestClient
                .get()
                .uri("/internal/isReady")
                .exchange()
                .expectStatus()
                .isOk();
    }

}
