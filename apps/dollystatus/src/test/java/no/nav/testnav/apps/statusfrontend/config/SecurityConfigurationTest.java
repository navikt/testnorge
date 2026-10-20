package no.nav.testnav.apps.statusfrontend.config;

import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

@DollySpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SecurityConfigurationTest {

    private static final String API_PATH = "/api/v1/fagsystem-statuser";

    private WebTestClient webTestClient;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient
                .bindToApplicationContext(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldRejectUnauthenticatedApiRequest() {
        webTestClient
                .get()
                .uri(API_PATH)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void shouldAllowAuthenticatedApiRequest() {
        webTestClient
                .mutateWith(mockJwt().jwt(jwt -> jwt
                        .subject("test-user")
                        .claim("preferred_username", "test-user@nav.no")))
                .get()
                .uri(API_PATH)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void shouldAllowUnauthenticatedHealthRequest() {
        webTestClient
                .get()
                .uri("/internal/health/liveness")
                .exchange()
                .expectStatus()
                .isOk();
    }
}
