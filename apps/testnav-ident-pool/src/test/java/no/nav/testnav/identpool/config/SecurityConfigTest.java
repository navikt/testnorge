package no.nav.testnav.identpool.config;

import no.nav.testnav.identpool.dto.ValideringFlereRequestDTO;
import no.nav.testnav.identpool.providers.v2.IdentController;
import no.nav.testnav.identpool.providers.v2.IdentValidatorController;
import no.nav.testnav.identpool.repository.PersonidentifikatorRepository;
import no.nav.testnav.identpool.service.Identpool32Service;
import no.nav.testnav.identpool.service.PersonnummerValidatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("security-test")
@ContextConfiguration(classes = {
        SecurityConfig.class,
        SecurityConfigTest.WebConfiguration.class,
        IdentController.class,
        IdentValidatorController.class
})
class SecurityConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @MockitoBean
    private ReactiveJwtDecoder jwtDecoder;

    @MockitoBean
    private PersonnummerValidatorService personnummerValidatorService;

    @MockitoBean
    private Identpool32Service identpool32Service;

    @MockitoBean
    private PersonidentifikatorRepository personidentifikatorRepository;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    void shouldAllowAnonymousPostToValidationEndpoint() {
        var ident = "14505899947";
        when(personnummerValidatorService.validerFoedselsnummer(List.of(ident))).thenReturn(Flux.empty());

        webTestClient.post()
                .uri("/api/v2/ident/validerflere")
                .bodyValue(new ValideringFlereRequestDTO(ident))
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[]");

        verify(personnummerValidatorService).validerFoedselsnummer(List.of(ident));
        verifyNoInteractions(jwtDecoder, identpool32Service, personidentifikatorRepository);
    }

    @ParameterizedTest
    @CsvSource({
            "POST, /api/v2/ident/rekvirer",
            "DELETE, /api/v2/ident/frigjoer/14505899947",
            "GET, /api/v2/ident/validerflere"
    })
    void shouldRequireAuthenticationForOtherRequests(String method, String path) {
        webTestClient.method(HttpMethod.valueOf(method))
                .uri(path)
                .exchange()
                .expectStatus().isUnauthorized();

        verifyNoInteractions(jwtDecoder, personnummerValidatorService, identpool32Service, personidentifikatorRepository);
    }

    @Configuration(proxyBeanMethods = false)
    @EnableWebFlux
    static class WebConfiguration {
    }
}
