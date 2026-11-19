package no.nav.testnav.apps.statusfrontend.config;

import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

@DollySpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
        "AZURE_OPENID_CONFIG_ISSUER=" + SecurityConfigurationTest.AZURE_ISSUER,
        "TOKEN_X_ISSUER=" + SecurityConfigurationTest.TOKEN_X_ISSUER
})
class SecurityConfigurationTest {

    static final String AZURE_ISSUER = "https://login.microsoftonline.com/test/v2.0";
    static final String TOKEN_X_ISSUER = "https://tokenx.test.nais.io";

    private static final String FAGSYSTEM_STATUSER_PATH = "/api/v1/fagsystem-statuser";
    private static final String TESTKJORINGER_PATH = "/api/v1/testkjoringer";
    private static final String UKJENT_API_PATH = "/api/v1/finnes-ikke";

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
                .uri(FAGSYSTEM_STATUSER_PATH)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void shouldRejectUnauthenticatedPost() {
        webTestClient
                .post()
                .uri(TESTKJORINGER_PATH)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void shouldAllowAzureUserApiRequest() {
        webTestClient
                .mutateWith(azureBruker())
                .get()
                .uri(FAGSYSTEM_STATUSER_PATH)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void shouldAllowAzureUserPost() {
        webTestClient
                .mutateWith(azureBruker())
                .post()
                .uri(UKJENT_API_PATH)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void shouldAllowTokenXToReadStatusoversikt() {
        webTestClient
                .mutateWith(tokenXBruker())
                .get()
                .uri(SecurityConfiguration.STATUSOVERSIKT_PATH)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void shouldAllowAzureUserToReadStatusoversikt() {
        webTestClient
                .mutateWith(azureBruker())
                .get()
                .uri(SecurityConfiguration.STATUSOVERSIKT_PATH)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void shouldForbidTokenXOtherApiRequests() {
        webTestClient
                .mutateWith(tokenXBruker())
                .get()
                .uri(FAGSYSTEM_STATUSER_PATH)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void shouldForbidTokenXPost() {
        webTestClient
                .mutateWith(tokenXBruker())
                .post()
                .uri(TESTKJORINGER_PATH)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void shouldForbidTokenXPostToStatusoversikt() {
        webTestClient
                .mutateWith(tokenXBruker())
                .post()
                .uri(SecurityConfiguration.STATUSOVERSIKT_PATH)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void shouldForbidAzureAppTokenPost() {
        webTestClient
                .mutateWith(mockJwt().jwt(jwt -> jwt
                        .issuer(AZURE_ISSUER)
                        .subject("test-app")
                        .claim("idtyp", "app")))
                .post()
                .uri(TESTKJORINGER_PATH)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void shouldForbidUnknownIssuer() {
        webTestClient
                .mutateWith(mockJwt().jwt(jwt -> jwt
                        .issuer("https://ukjent.example")
                        .subject("test-user")))
                .get()
                .uri(SecurityConfiguration.STATUSOVERSIKT_PATH)
                .exchange()
                .expectStatus()
                .isForbidden();
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

    private static SecurityMockServerConfigurers.JwtMutator azureBruker() {
        return mockJwt().jwt(jwt -> jwt
                .issuer(AZURE_ISSUER)
                .subject("test-user")
                .claim("preferred_username", "test-user@nav.no"));
    }

    private static SecurityMockServerConfigurers.JwtMutator tokenXBruker() {
        return mockJwt().jwt(jwt -> jwt
                .issuer(TOKEN_X_ISSUER)
                .subject("test-idporten-user"));
    }
}
