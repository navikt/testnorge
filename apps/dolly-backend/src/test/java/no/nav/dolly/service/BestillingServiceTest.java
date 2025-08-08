package no.nav.dolly.service;

import no.nav.dolly.MockedJwtAuthenticationTokenUtils;
import no.nav.dolly.bestilling.tpsmessagingservice.MiljoerConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingKontroll;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingKontrollRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BestillingServiceTest {

    private final static String BRUKERID = "123";

    private static final long BEST_ID = 1L;

    @Mock
    private BestillingRepository bestillingRepository;

    @Mock
    private BestillingKontrollRepository bestillingKontrollRepository;

    @Mock
    private TestgruppeRepository testgruppeRepository;

    @Mock
    private BrukerService brukerService;

    @Mock
    private MiljoerConsumer miljoerConsumer;

    @InjectMocks
    private BestillingService bestillingService;

    @BeforeEach
    public void setup() {
        MockedJwtAuthenticationTokenUtils.setJwtAuthenticationToken();
    }

    @Test
    void fetchBestillingByIdKasterExceptionHvisBestillingIkkeFunnet() {
        Optional<Bestilling> bes = Optional.empty();

//        when(bestillingRepository.findById(any())).thenReturn(bes);

        Assertions.assertThrows(NotFoundException.class, () ->
                bestillingService.fetchBestillingById(1L));
    }

    @Test
    void fetchBestillingByIdKasterReturnererBestillingHvisBestillingErFunnet() {
        Bestilling mock = mock(Bestilling.class);

        when(bestillingRepository.findById(any())).thenReturn(Mono.just(mock));

//        Bestilling bestilling = bestillingService.fetchBestillingById(1L);

//        assertThat(bestilling, is(mock));
    }

    @Test
    void saveBestillingToDBKasterExceptionHvisDBConstraintBlirBrutt() {
        when(bestillingRepository.save(any())).thenThrow(DataIntegrityViolationException.class);
//        Assertions.assertThrows(ConstraintViolationException.class, () ->
//                bestillingService.saveBestillingToDB(new Bestilling()));
    }

    @Test
    void saveBestillingByGruppeIdAndAntallIdenterInkludererAlleMiljoerOgIdenterIBestilling() {
        long gruppeId = 1L;
        Testgruppe gruppe = mock(Testgruppe.class);
        Set<String> miljoer = Set.of("a1", "b2", "c3", "d4");
        int antallIdenter = 4;

//        when(testgruppeRepository.findById(gruppeId)).thenReturn(Optional.of(gruppe));
//        when(miljoerConsumer.getMiljoer()).thenReturn(Mono.just(new ArrayList<>(miljoer)));
//        when(bestillingRepository.save(any(Bestilling.class))).thenReturn(Bestilling.builder().id(1L).build());

        bestillingService.saveBestilling(gruppeId, RsDollyBestilling.builder().environments(miljoer).build(),
                antallIdenter, null, null, null);

        ArgumentCaptor<Bestilling> argCap = ArgumentCaptor.forClass(Bestilling.class);
        verify(bestillingRepository).save(argCap.capture());

        Bestilling bes = argCap.getValue();

//        assertThat(bes.getGruppe(), is(gruppe));
        assertThat(bes.getAntallIdenter(), is(antallIdenter));
        assertThat(
                Set.of(bes.getMiljoer().split(",")),
                containsInAnyOrder("a1", "b2", "c3", "d4")
        );
    }

    @Test
    void cancelBestilling_OK() {

//        when(bestillingRepository.findById(BEST_ID)).thenReturn(Optional.of(Bestilling.builder().build()));
//        when(brukerService.fetchOrCreateBruker(BRUKERID)).thenReturn(Bruker.builder().build());
        bestillingService.cancelBestilling(1L);

        verify(bestillingKontrollRepository).findByBestillingId(BEST_ID);
        verify(bestillingKontrollRepository).save(any(BestillingKontroll.class));
    }

    @Test
    void cancelBestilling_NotFound() {

        when(bestillingRepository.findById(BEST_ID)).thenThrow(NotFoundException.class);
        Assertions.assertThrows(NotFoundException.class, () ->
                bestillingService.cancelBestilling(1L));
    }

    @Test
    void createBestillingForGjenopprett_Ok() {

//        when(bestillingRepository.findById(BEST_ID)).thenReturn(Optional.of(Bestilling.builder()
//                .gruppe(Testgruppe.builder()
//                        .testidenter(List.of(Testident.builder().build())).build())
//                .ferdig(true).build()));
//        when(brukerService.fetchOrCreateBruker(BRUKERID)).thenReturn(Bruker.builder().build());

        when(miljoerConsumer.getMiljoer()).thenReturn(Mono.just(List.of("u1")));

        bestillingService.createBestillingForGjenopprettFraBestilling(BEST_ID, "u1");

        verify(bestillingRepository).save(any(Bestilling.class));
    }

    @Test
    void createBestillingForGjenopprett_notFerdig() {

//        when(bestillingRepository.findById(BEST_ID)).thenReturn(Optional.of(Bestilling.builder().build()));

        Assertions.assertThrows(DollyFunctionalException.class, () ->
                bestillingService.createBestillingForGjenopprettFraBestilling(BEST_ID, "u1"));
    }

    @Test
    void createBestillingForGjenopprett_noTestidenter() {

//        when(bestillingRepository.findById(BEST_ID)).thenReturn(Optional.of(
//                Bestilling.builder().ferdig(true)
//                        .gruppe(Testgruppe.builder().build())
//                        .build()));

        Assertions.assertThrows(NotFoundException.class, () ->
                bestillingService.createBestillingForGjenopprettFraBestilling(BEST_ID, "u1"));
    }
}
