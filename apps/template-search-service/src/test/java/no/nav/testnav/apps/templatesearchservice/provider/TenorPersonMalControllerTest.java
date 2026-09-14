package no.nav.testnav.apps.templatesearchservice.provider;

import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMalLagreResult;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMalBrukerResponse;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMalOversiktResponse;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMalResponse;
import no.nav.testnav.apps.templatesearchservice.exception.TenorMalNotFoundException;
import no.nav.testnav.apps.templatesearchservice.service.TenorPersonMalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(MockitoExtension.class)
class TenorPersonMalControllerTest {

    @Mock
    private TenorPersonMalService malService;

    private WebTestClient webTestClient;
    private TenorPersonMalResponse response;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient
                .bindToController(new TenorPersonMalController(malService))
                .controllerAdvice(new TenorMalExceptionAdvice())
                .build();
        var now = Instant.parse("2026-01-01T10:00:00Z");
        response = new TenorPersonMalResponse(
                42L,
                "Min mal",
                JsonMapper.builder().build().readTree("{}"),
                now,
                now);
    }

    @Test
    void shouldReturnCreatedForNewTemplate() {
        when(malService.save(any()))
                .thenReturn(Mono.just(new TenorPersonMalLagreResult(response, true)));

        webTestClient.post()
                .uri("/api/v1/tenor/maler/personer")
                .contentType(APPLICATION_JSON)
                .bodyValue("""
                        {
                          "malNavn": "Min mal",
                          "soekKriterier": {}
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("/api/v1/tenor/maler/personer/42")
                .expectBody()
                .jsonPath("$.id").isEqualTo(42);
    }

    @Test
    void shouldReturnOkForUpdatedTemplate() {
        when(malService.save(any()))
                .thenReturn(Mono.just(new TenorPersonMalLagreResult(response, false)));

        webTestClient.post()
                .uri("/api/v1/tenor/maler/personer")
                .contentType(APPLICATION_JSON)
                .bodyValue("""
                        {
                          "malNavn": "Min mal",
                          "soekKriterier": {}
                        }
                        """)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldReturnUsersWithTemplatesOverview() {
        when(malService.getMalOversikt()).thenReturn(Mono.just(new TenorPersonMalOversiktResponse(
                List.of(
                        new TenorPersonMalBrukerResponse("ALLE", "ALLE"),
                        new TenorPersonMalBrukerResponse("azure-id", "Testbruker")))));

        webTestClient.get()
                .uri("/api/v1/tenor/maler/personer/oversikt")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.standardBrukerId").doesNotExist()
                .jsonPath("$.brukereMedMaler[0].brukerId").isEqualTo("ALLE")
                .jsonPath("$.brukereMedMaler[1].brukerId").isEqualTo("azure-id")
                .jsonPath("$.brukereMedMaler[1].maler").doesNotExist();
    }

    @Test
    void shouldReturnTemplatesForSelectedUser() {
        when(malService.getMaler("azure-id")).thenReturn(Flux.just(response));

        webTestClient.get()
                .uri("/api/v1/tenor/maler/personer/brukerId/azure-id")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(42)
                .jsonPath("$[0].malNavn").isEqualTo("Min mal");
    }

    @Test
    void shouldReturnNoContentWhenTemplateIsDeleted() {
        when(malService.delete(42L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/tenor/maler/personer/42")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldReturnExceptionInformationWithoutSensitiveContext() {
        when(malService.delete(42L))
                .thenReturn(Mono.error(new TenorMalNotFoundException("Intern detalj.")));

        webTestClient.delete()
                .uri("/api/v1/tenor/maler/personer/42")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Not Found")
                .jsonPath("$.message").isEqualTo("Malen ble ikke funnet.")
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.path").isEqualTo("/api/v1/tenor/maler/personer/42");
    }
}
