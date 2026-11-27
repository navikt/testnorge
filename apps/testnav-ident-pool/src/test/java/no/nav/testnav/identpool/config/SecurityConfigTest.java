package no.nav.testnav.identpool.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import no.nav.testnav.libs.dto.identpool.v2.ValideringFlereRequestDTO;
import no.nav.testnav.identpool.providers.v2.IdentController;
import no.nav.testnav.identpool.providers.v2.IdentValidatorController;
import no.nav.testnav.identpool.repository.PersonidentifikatorRepository;
import no.nav.testnav.identpool.service.Identpool32Service;
import no.nav.testnav.identpool.service.PersonnummerValidatorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setup() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
        logAppender = new ListAppender<>();
        logAppender.start();
        ((Logger) LoggerFactory.getLogger(IdentValidatorController.class)).addAppender(logAppender);
    }

    @AfterEach
    void tearDown() {
        ((Logger) LoggerFactory.getLogger(IdentValidatorController.class)).detachAppender(logAppender);
        logAppender.stop();
    }

    @Test
    void shouldRejectAnonymousPostToValidationEndpoint() {
        var ident = "14505899947";

        webTestClient.post()
                .uri("/api/v2/ident/validerflere")
                .bodyValue(new ValideringFlereRequestDTO(ident))
                .exchange()
                .expectStatus().isUnauthorized();

        verifyNoInteractions(jwtDecoder, personnummerValidatorService, identpool32Service, personidentifikatorRepository);
    }

    @Test
    void shouldRejectInvalidTokenForValidationEndpoint() {
        var ident = "14505899947";
        var token = "invalid-token";
        when(jwtDecoder.decode(token)).thenReturn(Mono.error(new BadJwtException("Invalid token")));

        webTestClient.post()
                .uri("/api/v2/ident/validerflere")
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(new ValideringFlereRequestDTO(ident))
                .exchange()
                .expectStatus().isUnauthorized();

        verify(jwtDecoder).decode(token);
        verifyNoInteractions(personnummerValidatorService, identpool32Service, personidentifikatorRepository);
    }

    @Test
    void shouldAllowAuthenticatedPostToValidationEndpoint() {
        var ident = "14505899947";
        var token = "test-token";
        var jwt = Jwt.withTokenValue(token).header("alg", "RS256").subject("test-client")
                .claim("NAVident", "Z123456").build();
        when(jwtDecoder.decode(token)).thenReturn(Mono.just(jwt));
        when(personnummerValidatorService.validerFoedselsnummer(List.of(ident))).thenReturn(Flux.empty());

        webTestClient.post()
                .uri("/api/v2/ident/validerflere")
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(new ValideringFlereRequestDTO(ident))
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[]");

        verify(jwtDecoder).decode(token);
        verify(personnummerValidatorService).validerFoedselsnummer(List.of(ident));
        verifyNoInteractions(identpool32Service, personidentifikatorRepository);
        assertThat(logAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Identvalidering utført av: Z123456, totalt antall: 0, herav i prod: 0");
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
