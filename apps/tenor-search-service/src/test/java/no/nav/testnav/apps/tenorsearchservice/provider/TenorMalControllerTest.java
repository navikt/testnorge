package no.nav.testnav.apps.tenorsearchservice.provider;

import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalLagreResult;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalResponse;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalType;
import no.nav.testnav.apps.tenorsearchservice.exception.TenorMalNotFoundException;
import no.nav.testnav.apps.tenorsearchservice.service.TenorMalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(MockitoExtension.class)
class TenorMalControllerTest {

    @Mock
    private TenorMalService malService;

    private WebTestClient webTestClient;
    private TenorMalResponse response;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient
                .bindToController(new TenorMalController(malService))
                .controllerAdvice(new TenorMalExceptionAdvice())
                .build();
        var now = Instant.parse("2026-01-01T10:00:00Z");
        response = new TenorMalResponse(
                42L,
                "Min mal",
                TenorMalType.PERSON,
                JsonMapper.builder().build().readTree("{}"),
                "azure-id",
                "Testbruker",
                now,
                now);
    }

    @Test
    void shouldReturnCreatedForNewTemplate() {
        when(malService.save(any()))
                .thenReturn(Mono.just(new TenorMalLagreResult(response, true)));

        webTestClient.post()
                .uri("/api/v1/tenor/maler")
                .contentType(APPLICATION_JSON)
                .bodyValue("""
                        {
                          "malNavn": "Min mal",
                          "malType": "PERSON",
                          "soekKriterier": {}
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("/api/v1/tenor/maler/42")
                .expectBody()
                .jsonPath("$.id").isEqualTo(42)
                .jsonPath("$.brukerId").isEqualTo("azure-id");
    }

    @Test
    void shouldReturnOkForUpdatedTemplate() {
        when(malService.save(any()))
                .thenReturn(Mono.just(new TenorMalLagreResult(response, false)));

        webTestClient.post()
                .uri("/api/v1/tenor/maler")
                .contentType(APPLICATION_JSON)
                .bodyValue("""
                        {
                          "malNavn": "Min mal",
                          "malType": "PERSON",
                          "soekKriterier": {}
                        }
                        """)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnNoContentWhenTemplateIsDeleted() {
        when(malService.delete(42L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/tenor/maler/42")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldReturnProblemDetailWithoutSensitiveContext() {
        when(malService.delete(42L))
                .thenReturn(Mono.error(new TenorMalNotFoundException("Intern detalj.")));

        webTestClient.delete()
                .uri("/api/v1/tenor/maler/42")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/problem+json")
                .expectBody()
                .jsonPath("$.title").isEqualTo("Søkemal ikke funnet")
                .jsonPath("$.detail").isEqualTo("Søkemalen eller brukeren ble ikke funnet.");
    }
}
