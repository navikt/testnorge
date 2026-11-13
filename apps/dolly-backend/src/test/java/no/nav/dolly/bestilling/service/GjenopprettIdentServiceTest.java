package no.nav.dolly.bestilling.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.projection.GruppeBestillingIdent;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.opensearch.service.OpenSearchService;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TransactionHelperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;

import static no.nav.dolly.domain.jpa.Testident.Master.PDLF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GjenopprettIdentServiceTest {

    private static final Long BESTILLING_ID = 1L;
    private static final String IDENT = "12345678901";

    @Mock
    private BestillingProgressRepository bestillingProgressRepository;
    @Mock
    private BestillingRepository bestillingRepository;
    @Mock
    private BestillingService bestillingService;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private CounterCustomRegistry counterCustomRegistry;
    @Mock
    private ErrorStatusDecoder errorStatusDecoder;
    @Mock
    private IdentRepository identRepository;
    @Mock
    private IdentService identService;
    @Mock
    private MapperFacade mapperFacade;
    @Mock
    private OpenSearchService openSearchService;
    @Mock
    private PdlDataConsumer pdlDataConsumer;
    @Mock
    private PersonServiceClient personServiceClient;
    @Mock
    private TestgruppeRepository testgruppeRepository;
    @Mock
    private TransactionHelperService transactionHelperService;
    @Mock
    private ClientRegister clientRegister;

    private GjenopprettIdentService service;
    private Bestilling bestilling;
    private BestillingProgress progress;
    private Sinks.One<Bestilling> completion;

    @BeforeEach
    void setUp() {

        service = spy(new GjenopprettIdentService(
                bestillingProgressRepository,
                bestillingRepository,
                bestillingService,
                cacheManager,
                counterCustomRegistry,
                errorStatusDecoder,
                identRepository,
                identService,
                JsonMapper.builder().build(),
                List.of(clientRegister),
                mapperFacade,
                openSearchService,
                pdlDataConsumer,
                personServiceClient,
                testgruppeRepository,
                transactionHelperService));
        ReflectionTestUtils.setField(service, "opprettPersonTimeout", 60L);
        bestilling = Bestilling.builder().id(BESTILLING_ID).gjenopprettetFraIdent(IDENT).miljoer("q1").build();
        progress = progress(3L);
        completion = Sinks.one();
        when(transactionHelperService.oppdaterBestillingFerdig(eq(BESTILLING_ID), any()))
                .thenAnswer(invocation -> Mono.fromSupplier(() -> {
                    bestilling.setFerdig(true);
                    bestilling.setFeil(invocation.getArgument(1));
                    completion.emitValue(bestilling, Sinks.EmitFailureHandler.FAIL_FAST);
                    return bestilling;
                }));
    }

    @ParameterizedTest
    @ValueSource(strings = {"q1", ""})
    void shouldRestoreFirstOrderAndSubsequentNonEmptyOrdersInRequestedEnvironments(String miljoer) {

        bestilling.setMiljoer(miljoer);
        stubPerson();
        when(identRepository.getBestillingerByIdent(IDENT)).thenReturn(Flux.just(
                historicalOrder(10L, "{}"),
                historicalOrder(11L, "{}"),
                historicalOrder(12L, "{\"instdata\":[]}"),
                historicalOrder(13L, "{}")));
        stubClientRestoration();

        service.executeAsync(bestilling);

        var criteriaCaptor = ArgumentCaptor.forClass(RsDollyUtvidetBestilling.class);
        var personCaptor = ArgumentCaptor.forClass(DollyPerson.class);
        verify(clientRegister, times(2)).gjenopprett(criteriaCaptor.capture(), personCaptor.capture(), eq(progress));
        assertThat(criteriaCaptor.getAllValues()).extracting(RsDollyUtvidetBestilling::getId)
                .containsExactly(10L, 12L);
        assertThat(criteriaCaptor.getAllValues()).allSatisfy(criteria ->
                assertThat(criteria.getEnvironments()).containsExactly(miljoer.isEmpty() ? "q2" : miljoer));
        assertThat(personCaptor.getAllValues()).allSatisfy(person -> {
            assertThat(person.getIdent()).isEqualTo(IDENT);
            assertThat(person.isOpprett()).isFalse();
            assertThat(person.isLeggTil()).isFalse();
        });
        verify(transactionHelperService).oppdaterBestillingFerdig(BESTILLING_ID, null);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldRestoreClientsSequentiallyAndWaitBeforeFinishing() {

        stubPerson();
        when(identRepository.getBestillingerByIdent(IDENT)).thenReturn(Flux.just(
                historicalOrder(10L, "{}"), historicalOrder(11L, "{\"instdata\":[]}")));
        var firstRestoration = TestPublisher.<BestillingProgress>create();
        var secondRestoration = TestPublisher.<BestillingProgress>create();
        doReturn(firstRestoration.mono()).when(clientRegister).gjenopprett(
                argThat(criteria -> Long.valueOf(10L).equals(criteria.getId())), any(), eq(progress));
        doReturn(secondRestoration.mono()).when(clientRegister).gjenopprett(
                argThat(criteria -> Long.valueOf(11L).equals(criteria.getId())), any(), eq(progress));

        service.executeAsync(bestilling);

        firstRestoration.assertSubscribers();
        secondRestoration.assertNoSubscribers();
        verify(transactionHelperService, never()).oppdaterBestillingFerdig(any(), any());

        firstRestoration.emit(progress);

        secondRestoration.assertSubscribers();
        assertThat(bestilling.isFerdig()).isFalse();

        secondRestoration.emit(progress);

        verify(transactionHelperService).oppdaterBestillingFerdig(BESTILLING_ID, null);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldUseProgressReturnedFromOrderAndSynchronizationInLaterSteps() {

        var orderedProgress = progress(4L);
        var synchronizedProgress = progress(5L);
        stubPerson(orderedProgress, Mono.just(synchronizedProgress));
        when(identRepository.getBestillingerByIdent(IDENT))
                .thenReturn(Flux.just(historicalOrder(10L, "{}")));
        stubClientRestoration();

        service.executeAsync(bestilling);

        verify(service).opprettDollyPersonForGjenopprett(orderedProgress);
        verify(service).gjenopprettKlienterStart(any(), any(), eq(orderedProgress));
        verify(personServiceClient).syncPerson(any(), eq(orderedProgress));
        verify(clientRegister).gjenopprett(any(), any(), eq(synchronizedProgress));
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldFinishWhenThereAreNoHistoricalOrders() {

        stubPerson();
        when(identRepository.getBestillingerByIdent(IDENT)).thenReturn(Flux.empty());

        service.executeAsync(bestilling);

        verifyNoInteractions(clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldSkipHistoricalOrdersWhenPdlSyncFails() {

        stubPerson();
        progress.setPdlSync(false);

        service.executeAsync(bestilling);

        verifyNoInteractions(identRepository, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldSaveErrorForRequestedIdentWhenProgressCreationFails() {

        doReturn(Mono.error(new IllegalStateException("Could not save progress")))
                .when(service).opprettProgress(BESTILLING_ID, IDENT);
        when(bestillingProgressRepository.save(any())).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0)));

        service.executeAsync(bestilling);

        var progressCaptor = ArgumentCaptor.forClass(BestillingProgress.class);
        verify(bestillingProgressRepository).save(progressCaptor.capture());
        assertThat(progressCaptor.getValue().getBestillingId()).isEqualTo(BESTILLING_ID);
        assertThat(progressCaptor.getValue().getIdent()).isEqualTo(IDENT);
        assertThat(progressCaptor.getValue().getFeil()).isEqualTo("Feil: Could not save progress");
        verifyNoInteractions(personServiceClient, identRepository, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldRecordHistoricalLookupFailureOnProgress() {

        stubPerson();
        when(identRepository.getBestillingerByIdent(IDENT))
                .thenReturn(Flux.error(new IllegalStateException("History unavailable")));
        when(transactionHelperService.persister(eq(progress), any(), eq("Feil: History unavailable")))
                .thenReturn(Mono.just(progress));

        service.executeAsync(bestilling);

        verify(transactionHelperService).persister(eq(progress), any(), eq("Feil: History unavailable"));
        verifyNoInteractions(clientRegister);
        verify(transactionHelperService).oppdaterBestillingFerdig(BESTILLING_ID, null);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldMarkBestillingFailedWhenProgressErrorCannotBePersisted() {

        stubPerson(progress, Mono.error(new IllegalStateException("Synchronization failed")));
        when(transactionHelperService.persister(eq(progress), any(), eq("Feil: Synchronization failed")))
                .thenReturn(Mono.error(new IllegalStateException("Error persistence failed")));

        service.executeAsync(bestilling);

        verify(transactionHelperService).oppdaterBestillingFerdig(
                BESTILLING_ID, "Feil ved utføring av : Error persistence failed");
        verify(service, never()).doFerdig(any());
        verifyNoInteractions(identRepository, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
        assertThat(bestilling.getFeil()).contains("Error persistence failed");
    }

    @Test
    void shouldRecordTimeoutOnProgressAndFinishBestilling() {

        stubPerson(progress, Mono.never());
        when(transactionHelperService.persister(eq(progress), any(), contains("60000ms")))
                .thenReturn(Mono.just(progress));

        StepVerifier.withVirtualTime(() ->
                        Mono.fromRunnable(() -> service.executeAsync(bestilling)).then(completion.asMono()))
                .then(() -> assertThat(bestilling.isFerdig()).isFalse())
                .thenAwait(Duration.ofSeconds(60))
                .expectNext(bestilling)
                .expectComplete()
                .verify(Duration.ofSeconds(5));

        verify(transactionHelperService).persister(eq(progress), any(), contains("60000ms"));
        verify(transactionHelperService).oppdaterBestillingFerdig(BESTILLING_ID, null);
        verifyNoInteractions(identRepository, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    private void stubPerson() {

        stubPerson(progress, Mono.just(progress));
    }

    private void stubPerson(BestillingProgress orderedProgress, Mono<BestillingProgress> synchronization) {

        var person = DollyPerson.builder().ident(IDENT).master(PDLF).build();
        doReturn(Mono.just(progress)).when(service).opprettProgress(BESTILLING_ID, IDENT);
        doReturn(Mono.just(orderedProgress)).when(service).sendOrdrePerson(progress);
        doReturn(Mono.just(person)).when(service).opprettDollyPersonForGjenopprett(orderedProgress);
        when(personServiceClient.syncPerson(person, orderedProgress)).thenReturn(synchronization);
    }

    private void stubClientRestoration() {

        when(clientRegister.gjenopprett(any(), any(), any())).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(2)));
    }

    private static BestillingProgress progress(Long id) {

        return BestillingProgress.builder().id(id).bestillingId(BESTILLING_ID)
                .ident(IDENT).master(PDLF).pdlSync(true).build();
    }

    private static GruppeBestillingIdent historicalOrder(Long id, String criteria) {

        return new GruppeBestillingIdent(IDENT, id, criteria, "q2");
    }
}
