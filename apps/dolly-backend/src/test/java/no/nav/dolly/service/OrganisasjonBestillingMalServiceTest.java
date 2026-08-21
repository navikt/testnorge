package no.nav.dolly.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingMal;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.repository.OrganisasjonBestillingMalRepository;
import no.nav.dolly.repository.OrganisasjonBestillingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganisasjonBestillingMalServiceTest {

    private static final Long BESTILLING_ID = 1L;
    private static final Long BRUKER_ID = 2L;
    private static final String MAL_NAVN = "Organisasjonsmal";

    @Mock
    private OrganisasjonBestillingMalRepository organisasjonBestillingMalRepository;

    @Mock
    private BrukerService brukerService;

    @Mock
    private OrganisasjonBestillingRepository organisasjonBestillingRepository;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private OrganisasjonBestillingMalService organisasjonBestillingMalService;

    @Test
    void shouldSetBrukerIdWhenCreatingTemplateFromExistingOrder() {

        mockExistingOrder();
        when(organisasjonBestillingMalRepository.save(any(OrganisasjonBestillingMal.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(organisasjonBestillingMalService
                        .saveOrganisasjonBestillingMalFromBestillingId(BESTILLING_ID, MAL_NAVN))
                .assertNext(mal -> assertThat(mal.getBrukerId(), is(BRUKER_ID)))
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenTemplateIsNotPersisted() {

        mockExistingOrder();
        when(organisasjonBestillingMalRepository.save(any(OrganisasjonBestillingMal.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(organisasjonBestillingMalService
                        .saveOrganisasjonBestillingMalFromBestillingId(BESTILLING_ID, MAL_NAVN))
                .expectErrorMatches(error -> error instanceof DollyFunctionalException
                        && error.getMessage().equals(
                        "Kunne ikke lagre organisasjonsmal 'Organisasjonsmal' fra bestilling med id 1"))
                .verify();
    }

    private void mockExistingOrder() {

        var bestilling = OrganisasjonBestilling.builder()
                .id(BESTILLING_ID)
                .bestKriterier("{}")
                .miljoer("q1")
                .build();
        var bruker = Bruker.builder()
                .id(BRUKER_ID)
                .build();

        when(organisasjonBestillingRepository.findById(BESTILLING_ID)).thenReturn(Mono.just(bestilling));
        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(bruker));
        when(organisasjonBestillingMalRepository.findByBrukerIdAndMalNavnOrderByMalNavn(BRUKER_ID, MAL_NAVN))
                .thenReturn(Flux.empty());
    }
}
