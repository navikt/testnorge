package no.nav.dolly.bestilling.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.opensearch.service.OpenSearchService;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
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
class GjenopprettBestillingServiceTest {

    private static final Long BESTILLING_ID = 1L;
    private static final Long SOURCE_BESTILLING_ID = 2L;
    private static final String IDENT_1 = "12345678901";
    private static final String IDENT_2 = "12345678902";
    private static final String RESTORATION_ERROR = "Feil: Gjenopprett bestilling ikke fullført; annen feil";

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

    private GjenopprettBestillingService service;
    private Bestilling bestilling;
    private Bestilling sourceBestilling;
    private Sinks.One<Bestilling> completion;

    @BeforeEach
    void setUp() {

        service = spy(new GjenopprettBestillingService(
                bestillingProgressRepository,
                bestillingRepository,
                bestillingService,
                cacheManager,
                counterCustomRegistry,
                errorStatusDecoder,
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
        bestilling = Bestilling.builder().id(BESTILLING_ID).opprettetFraId(SOURCE_BESTILLING_ID).build();
        sourceBestilling = Bestilling.builder().id(SOURCE_BESTILLING_ID)
                .bestKriterier("{\"instdata\":[]}").miljoer("q2").build();
        completion = Sinks.one();
        when(transactionHelperService.oppdaterBestillingFerdig(eq(BESTILLING_ID), any()))
                .thenAnswer(invocation -> Mono.fromSupplier(() -> {
                    bestilling.setFerdig(true);
                    bestilling.setFeil(invocation.getArgument(1));
                    completion.emitValue(bestilling, Sinks.EmitFailureHandler.FAIL_FAST);
                    return bestilling;
                }));
    }

    @Test
    void shouldRestoreValidSourcePersonsUsingSourceCriteriaAndNewProgress() {

        stubSourceProgress(Flux.just(sourceProgress(IDENT_1), sourceProgress("invalid"), sourceProgress(IDENT_2)));
        var firstProgress = stubPerson(IDENT_1);
        var secondProgress = stubPerson(IDENT_2);
        stubClientRestoration();

        service.executeAsync(bestilling);

        var criteriaCaptor = ArgumentCaptor.forClass(RsDollyUtvidetBestilling.class);
        var personCaptor = ArgumentCaptor.forClass(DollyPerson.class);
        var progressCaptor = ArgumentCaptor.forClass(BestillingProgress.class);
        verify(clientRegister, times(2)).gjenopprett(
                criteriaCaptor.capture(), personCaptor.capture(), progressCaptor.capture());
        assertThat(criteriaCaptor.getAllValues()).allSatisfy(criteria -> {
            assertThat(criteria.getId()).isEqualTo(SOURCE_BESTILLING_ID);
            assertThat(criteria.getEnvironments()).containsExactly("q2");
            assertThat(criteria.getEkskluderEksternePersoner()).isTrue();
        });
        assertThat(personCaptor.getAllValues()).extracting(DollyPerson::getIdent)
                .containsExactly(IDENT_1, IDENT_2);
        assertThat(personCaptor.getAllValues()).allSatisfy(person -> {
            assertThat(person.isOpprett()).isFalse();
            assertThat(person.isLeggTil()).isFalse();
        });
        assertThat(progressCaptor.getAllValues()).containsExactly(firstProgress, secondProgress);
        verify(service, never()).opprettProgress(BESTILLING_ID, "invalid");
        verify(transactionHelperService).oppdaterBestillingFerdig(BESTILLING_ID, null);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldWaitForAllPersonsBeforeFinishingBestilling() {

        stubSourceProgress(Flux.just(sourceProgress(IDENT_1), sourceProgress(IDENT_2)));
        var firstProgress = stubPerson(IDENT_1);
        var secondProgress = stubPerson(IDENT_2);
        var firstRestoration = TestPublisher.<BestillingProgress>create();
        var secondRestoration = TestPublisher.<BestillingProgress>create();
        when(clientRegister.gjenopprett(any(), any(), eq(firstProgress))).thenReturn(firstRestoration.mono());
        when(clientRegister.gjenopprett(any(), any(), eq(secondProgress))).thenReturn(secondRestoration.mono());

        service.executeAsync(bestilling);

        firstRestoration.assertSubscribers();
        secondRestoration.assertSubscribers();
        verify(transactionHelperService, never()).oppdaterBestillingFerdig(any(), any());

        firstRestoration.emit(firstProgress);

        assertThat(bestilling.isFerdig()).isFalse();

        secondRestoration.emit(secondProgress);

        assertThat(bestilling.isFerdig()).isTrue();
        verify(transactionHelperService).oppdaterBestillingFerdig(BESTILLING_ID, null);
    }

    @Test
    void shouldUseProgressReturnedFromOrderAndSynchronizationInLaterSteps() {

        stubSourceProgress(Flux.just(sourceProgress(IDENT_1)));
        var createdProgress = progress(IDENT_1);
        var orderedProgress = progress(IDENT_1);
        orderedProgress.setId(99L);
        var synchronizedProgress = progress(IDENT_1);
        synchronizedProgress.setId(100L);
        stubPerson(createdProgress, orderedProgress, Mono.just(synchronizedProgress));
        stubClientRestoration();

        service.executeAsync(bestilling);

        verify(service).opprettDollyPersonForGjenopprett(orderedProgress);
        verify(service).gjenopprettKlienterStart(any(), any(), eq(orderedProgress));
        verify(personServiceClient).syncPerson(any(), eq(orderedProgress));
        verify(clientRegister).gjenopprett(any(), any(), eq(synchronizedProgress));
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldFinishWithoutRestoringWhenSourceHasNoValidPersons(boolean hasInvalidPerson) {

        stubSourceProgress(hasInvalidPerson ? Flux.just(sourceProgress("invalid")) : Flux.empty());

        service.executeAsync(bestilling);

        verify(service, never()).opprettProgress(any(), anyString());
        verifyNoInteractions(bestillingService, personServiceClient, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldSkipRestorationWhenSourceCriteriaCannotBeRead() {

        sourceBestilling.setBestKriterier("invalid JSON");
        when(bestillingRepository.findById(SOURCE_BESTILLING_ID)).thenReturn(Mono.just(sourceBestilling));

        service.executeAsync(bestilling);

        verifyNoInteractions(bestillingProgressRepository, bestillingService, personServiceClient, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldSkipRestorationWithoutRecordingErrorWhenBestillingIsStopped() {

        stubSourceProgress(Flux.just(sourceProgress(IDENT_1)));
        when(bestillingService.isStoppet(BESTILLING_ID)).thenReturn(Mono.just(true));
        service.executeAsync(bestilling);

        verify(bestillingProgressRepository, never()).save(any());
        verify(service, never()).opprettProgress(any(), anyString());
        verifyNoInteractions(personServiceClient, clientRegister);
        verify(transactionHelperService).oppdaterBestillingFerdig(BESTILLING_ID, null);
        assertThat(bestilling.isFerdig()).isTrue();
        assertThat(bestilling.getFeil()).isNull();
    }

    @Test
    void shouldRecordErrorAndSkipClientsWhenPdlSyncFails() {

        stubSourceProgress(Flux.just(sourceProgress(IDENT_1)));
        var progress = stubPerson(IDENT_1);
        progress.setPdlSync(false);
        when(transactionHelperService.persister(eq(progress), any(), eq(RESTORATION_ERROR)))
                .thenReturn(Mono.just(progress));

        service.executeAsync(bestilling);

        verify(transactionHelperService).persister(eq(progress), any(), eq(RESTORATION_ERROR));
        verifyNoInteractions(clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldContinueWithOtherPersonsWhenProgressCreationFails() {

        stubSourceProgress(Flux.just(sourceProgress(IDENT_1), sourceProgress(IDENT_2)));
        var secondProgress = stubPerson(IDENT_2);
        doReturn(Mono.error(new IllegalStateException("Could not save progress")))
                .when(service).opprettProgress(BESTILLING_ID, IDENT_1);
        when(bestillingProgressRepository.save(any())).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0)));
        stubClientRestoration();

        service.executeAsync(bestilling);

        var progressCaptor = ArgumentCaptor.forClass(BestillingProgress.class);
        verify(bestillingProgressRepository).save(progressCaptor.capture());
        assertThat(progressCaptor.getValue().getBestillingId()).isEqualTo(BESTILLING_ID);
        assertThat(progressCaptor.getValue().getIdent()).isEqualTo(IDENT_1);
        assertThat(progressCaptor.getValue().getFeil()).isEqualTo("Feil: Could not save progress");
        verify(clientRegister).gjenopprett(any(), any(), eq(secondProgress));
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldFinishBestillingWithErrorWhenSourceLookupFails() {

        when(bestillingRepository.findById(SOURCE_BESTILLING_ID))
                .thenReturn(Mono.error(new IllegalStateException("Database unavailable")));

        service.executeAsync(bestilling);

        verify(transactionHelperService).oppdaterBestillingFerdig(
                BESTILLING_ID, "Feil ved utføring av : Database unavailable");
        verify(service, never()).doFerdig(any());
        verifyNoInteractions(bestillingProgressRepository, personServiceClient, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
        assertThat(bestilling.getFeil()).contains("Database unavailable");
    }

    @Test
    void shouldRecordTimeoutOnProgressAndCompleteOtherPersons() {

        stubSourceProgress(Flux.just(sourceProgress(IDENT_1), sourceProgress(IDENT_2)));
        var progress = progress(IDENT_1);
        stubPerson(progress, progress, Mono.never());
        var secondProgress = stubPerson(IDENT_2);
        stubClientRestoration();
        when(transactionHelperService.persister(eq(progress), any(), contains("60000ms")))
                .thenReturn(Mono.just(progress));

        StepVerifier.withVirtualTime(() ->
                        Mono.fromRunnable(() -> service.executeAsync(bestilling)).then(completion.asMono()))
                .then(() -> assertThat(bestilling.isFerdig()).isFalse())
                .thenAwait(Duration.ofSeconds(60))
                .expectNext(bestilling)
                .expectComplete()
                .verify(Duration.ofSeconds(5));

        assertThat(bestilling.isFerdig()).isTrue();
        assertThat(bestilling.getFeil()).isNull();
        verify(transactionHelperService).persister(eq(progress), any(), contains("60000ms"));
        verify(clientRegister).gjenopprett(any(), any(), eq(secondProgress));
        verify(transactionHelperService).oppdaterBestillingFerdig(BESTILLING_ID, null);
    }

    private void stubSourceProgress(Flux<BestillingProgress> sourceProgress) {

        when(bestillingRepository.findById(SOURCE_BESTILLING_ID)).thenReturn(Mono.just(sourceBestilling));
        when(bestillingProgressRepository.findAllByBestillingId(SOURCE_BESTILLING_ID)).thenReturn(sourceProgress);
    }

    private BestillingProgress stubPerson(String ident) {

        var progress = progress(ident);
        stubPerson(progress, progress, Mono.just(progress));
        return progress;
    }

    private void stubPerson(BestillingProgress createdProgress, BestillingProgress orderedProgress,
                            Mono<BestillingProgress> synchronization) {

        var person = DollyPerson.builder().ident(orderedProgress.getIdent()).master(PDLF).build();
        when(bestillingService.isStoppet(BESTILLING_ID)).thenReturn(Mono.just(false));
        doReturn(Mono.just(createdProgress)).when(service).opprettProgress(BESTILLING_ID, createdProgress.getIdent());
        doReturn(Mono.just(orderedProgress)).when(service).sendOrdrePerson(createdProgress);
        doReturn(Mono.just(person)).when(service).opprettDollyPersonForGjenopprett(orderedProgress);
        when(personServiceClient.syncPerson(person, orderedProgress)).thenReturn(synchronization);
    }

    private void stubClientRestoration() {

        when(clientRegister.gjenopprett(any(), any(), any())).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(2)));
    }

    private static BestillingProgress sourceProgress(String ident) {

        return BestillingProgress.builder().bestillingId(SOURCE_BESTILLING_ID).ident(ident).master(PDLF).build();
    }

    private static BestillingProgress progress(String ident) {

        return BestillingProgress.builder().id(Long.valueOf(ident)).bestillingId(BESTILLING_ID)
                .ident(ident).master(PDLF).pdlSync(true).build();
    }
}
