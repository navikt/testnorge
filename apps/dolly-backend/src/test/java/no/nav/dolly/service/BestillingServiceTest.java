package no.nav.dolly.service;

import no.nav.dolly.bestilling.tpsmessagingservice.MiljoerConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingKontroll;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingKontrollRepository;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BestillingServiceTest {

    private static final long BEST_ID = 1L;

    @Mock
    private BestillingRepository bestillingRepository;

    @Mock
    private BestillingKontrollRepository bestillingKontrollRepository;

    @Mock
    private BestillingProgressRepository bestillingProgressRepository;

    @Mock
    private TestgruppeRepository testgruppeRepository;

    @Mock
    private BrukerService brukerService;

    @Mock
    private MiljoerConsumer miljoerConsumer;

    @InjectMocks
    private BestillingService bestillingService;

    @Test
    void fetchBestillingByIdKasterExceptionHvisBestillingIkkeFunnet() {

        when(bestillingRepository.findById(BEST_ID)).thenReturn(Mono.empty());

        StepVerifier.create(bestillingService.fetchBestillingById(BEST_ID))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void fetchBestillingByIdKasterReturnererBestillingHvisBestillingErFunnet() {

        var bestilling = Bestilling.builder().id(BEST_ID).build();
        when(bestillingRepository.findById(any())).thenReturn(Mono.just(bestilling));
        when(bestillingProgressRepository.findAllByBestillingId(BEST_ID)).thenReturn(Flux.empty());
        when(brukerService.findById(any())).thenReturn(Mono.just(Bruker.builder().build()));

        StepVerifier.create(bestillingService.fetchBestillingById(BEST_ID))
                .assertNext(bestilling1 -> assertThat(bestilling1, is(bestilling)))
                .verifyComplete();
    }

    @Test
    void saveBestillingByGruppeIdAndAntallIdenterInkludererAlleMiljoerOgIdenterIBestilling() {

        var gruppeId = 1L;
        var gruppe = Testgruppe.builder().id(gruppeId).build();
        var miljoer = Set.of("a1", "b2", "c3", "d4");
        var antallIdenter = 4;

        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(Bruker.builder().build()));
        when(testgruppeRepository.findById(gruppeId)).thenReturn(Mono.just(gruppe));
        when(miljoerConsumer.getMiljoer()).thenReturn(Mono.just(new ArrayList<>(miljoer)));
        when(bestillingRepository.save(any(Bestilling.class))).thenReturn(Mono.just(Bestilling.builder().id(1L)
                .gruppeId(gruppeId)
                .antallIdenter(antallIdenter)
                .miljoer("a1,b2,c3,d4")
                .build()));
        when(bestillingProgressRepository.findAllByBestillingId(BEST_ID)).thenReturn(Flux.empty());

        StepVerifier.create(bestillingService.saveBestilling(gruppeId, RsDollyBestilling.builder().environments(miljoer).build(),
                        antallIdenter, null, null, null))
                .assertNext(bestilling -> {
                    assertThat(bestilling.getGruppeId(), is(gruppeId));
                    assertThat(bestilling.getAntallIdenter(), is(antallIdenter));
                    assertThat(
                            Set.of(bestilling.getMiljoer().split(",")),
                            containsInAnyOrder("a1", "b2", "c3", "d4")
                    );
                })
                .verifyComplete();

        verify(bestillingRepository, times(2)).save(any());
    }

    @Test
    void cancelBestilling_OK() {

        when(bestillingRepository.findById(BEST_ID)).thenReturn(Mono.just(Bestilling.builder().build()));
        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(Bruker.builder().build()));
        when(bestillingKontrollRepository.findByBestillingId(BEST_ID)).thenReturn(Mono.empty());
        when(bestillingKontrollRepository.save(any())).thenReturn(Mono.just(BestillingKontroll.builder()
                .bestillingId(BEST_ID)
                .stoppet(true)
                .build()));
        when(bestillingProgressRepository.findAllByBestillingId(BEST_ID)).thenReturn(Flux.empty());
        when(bestillingRepository.save(any())).thenReturn(Mono.just(Bestilling.builder().id(BEST_ID).build()));

        StepVerifier.create(bestillingService.cancelBestilling(1L))
                .assertNext(bestilling -> {
                    assertThat(bestilling.getId(), is(BEST_ID));
                    assertThat(bestilling.isFerdig(), is(false));
                })
                .verifyComplete();

        verify(bestillingKontrollRepository).findByBestillingId(BEST_ID);
        verify(bestillingKontrollRepository).save(any(BestillingKontroll.class));
    }

    @Test
    void cancelBestilling_NotFound() {

        when(bestillingRepository.findById(BEST_ID)).thenReturn(Mono.empty());

        StepVerifier.create(bestillingService.cancelBestilling(BEST_ID))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void createBestillingForGjenopprett_Ok() {

        var miljoe = "q2";
        var gruppeId = 1L;
        var bestilling = Bestilling.builder()
                .id(BEST_ID)
                .miljoer(miljoe)
                .gruppeId(gruppeId)
                .ferdig(true)
                .brukerId(27L)
                .build();

        when(bestillingRepository.findById(BEST_ID)).thenReturn(Mono.just(bestilling));
        when(bestillingProgressRepository.findAllByBestillingId(BEST_ID)).thenReturn(Flux.just(BestillingProgress.builder()
                .ident("12345678901")
                .build()));
        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(Bruker.builder().build()));
        when(miljoerConsumer.getMiljoer()).thenReturn(Mono.just(List.of(miljoe)));
        when(bestillingRepository.save(any())).thenReturn(Mono.just(bestilling));
        when(brukerService.findById(any())).thenReturn(Mono.just(Bruker.builder().build()));

        StepVerifier.create(bestillingService.createBestillingForGjenopprettFraBestilling(BEST_ID, miljoe))
                .assertNext(bestilling1 -> {
                    assertThat(bestilling1.getGruppeId(), is(gruppeId));
                    assertThat(bestilling1.getMiljoer(), is(miljoe));
                    assertThat(bestilling1.getBrukerId(), is(27L));
                })
                .verifyComplete();

        verify(bestillingRepository).save(any(Bestilling.class));
    }

    @Test
    void createBestillingForGjenopprett_notFerdig() {

        var miljoe = "q2";
        var gruppeId = 1L;
        var bestilling = Bestilling.builder()
                .id(BEST_ID)
                .gruppeId(gruppeId)
                .ferdig(false)
                .build();

        when(bestillingRepository.findById(BEST_ID)).thenReturn(Mono.just(bestilling));
        when(bestillingProgressRepository.findAllByBestillingId(BEST_ID)).thenReturn(Flux.just(BestillingProgress.builder()
                .ident("12345678901")
                .build()));
        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(Bruker.builder().build()));
        when(miljoerConsumer.getMiljoer()).thenReturn(Mono.just(List.of(miljoe)));
        when(brukerService.findById(any())).thenReturn(Mono.just(Bruker.builder().build()));

        StepVerifier.create(bestillingService.createBestillingForGjenopprettFraBestilling(BEST_ID, miljoe))
                .expectError(DollyFunctionalException.class)
                .verify();
    }

    @Test
    void createBestillingForGjenopprett_noTestidenter() {

        var miljoe = "q2";
        var gruppeId = 1L;
        var bestilling = Bestilling.builder()
                .id(BEST_ID)
                .gruppeId(gruppeId)
                .ferdig(true)
                .build();

        when(bestillingRepository.findById(BEST_ID)).thenReturn(Mono.just(bestilling));
        when(bestillingProgressRepository.findAllByBestillingId(BEST_ID)).thenReturn(Flux.empty());
        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(Bruker.builder().build()));
        when(miljoerConsumer.getMiljoer()).thenReturn(Mono.just(List.of(miljoe)));
        when(brukerService.findById(any())).thenReturn(Mono.just(Bruker.builder().build()));

        StepVerifier.create(bestillingService.createBestillingForGjenopprettFraBestilling(BEST_ID, miljoe))
                .expectError(NotFoundException.class)
                .verify();
    }
}
