package no.nav.dolly.libs.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

public class DollyApplicationContextTest {

    @Autowired
    public WebTestClient webTestClient;

    @Test
    public void testNonexistingApiEndpoint() {
        webTestClient
                .get()
                .uri("/api/someNonExistingEndpoint")
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    void testLivenessEndpoint() {
        webTestClient
                .get()
                .uri("/internal/health/liveness")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void testReadinessEndpoint() {
        webTestClient
                .get()
                .uri("/internal/health/readiness")
                .exchange()
                .expectStatus()
                .isOk();
    }

    /**
     * <p>Note that {@code WebTestClient} is not configured with {@code LegacyHealthEndpointsForwardingFilter}.</p>
     * <p>Added to ensure no app publishes on legacy endpoints itself.</p>
     */
    @Test
    void testIsAliveEndpoint() {
        webTestClient
                .get()
                .uri("/internal/isAlive")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    /**
     * <p>Note that {@code WebTestClient} is not configured with {@code LegacyHealthEndpointsForwardingFilter}.</p>
     * <p>Added to ensure no app publishes on legacy endpoints itself.</p>
     */
    @Test
    void testIsReadyEndpoint() {
        webTestClient
                .get()
                .uri("/internal/isReady")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testNonexistingInternalEndpoint() {
        webTestClient
                .get()
                .uri("/internal/someNonExistingEndpoint")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

}
