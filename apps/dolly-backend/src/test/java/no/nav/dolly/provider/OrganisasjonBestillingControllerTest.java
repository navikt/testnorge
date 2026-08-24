package no.nav.dolly.provider;

import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.service.OrganisasjonBestillingMalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

class OrganisasjonBestillingControllerTest extends AbstractControllerTest {

    private static final Long BESTILLING_ID = 1L;
    private static final String MAL_NAVN = "Organisasjonsmal";
    private static final String LAGRING_FEILET =
            "Kunne ikke lagre organisasjonsmal 'Organisasjonsmal' fra bestilling med id 1";

    @MockitoBean
    private OrganisasjonBestillingMalService organisasjonBestillingMalService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldReturnInternalServerErrorWhenTemplateIsNotPersisted() {

        when(organisasjonBestillingMalService
                .saveOrganisasjonBestillingMalFromBestillingId(BESTILLING_ID, MAL_NAVN))
                .thenReturn(Mono.error(new DollyFunctionalException(LAGRING_FEILET)));

        webTestClient
                .post()
                .uri("/api/v1/organisasjon/bestilling/malbestilling?bestillingId={bestillingId}&malNavn={malNavn}",
                        BESTILLING_ID, MAL_NAVN)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo(LAGRING_FEILET);
    }
}
