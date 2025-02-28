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
    public WebTestClient webTestClient;

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

    @Test
    public void testNonexistingApiEndpoint() {
        webTestClient
                .get()
                .uri("/api/someNonExistingEndpoint")
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

}
