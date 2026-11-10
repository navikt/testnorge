package no.nav.dolly.bestilling.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static no.nav.dolly.domain.jpa.Testident.Master.PDLF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpprettPersonerByKriterierServiceTest {

    private static final Long BESTILLING_ID = 1L;
    private static final Long GRUPPE_ID = 2L;
    private static final String CREATION_ERROR = "Feil: Oppretting ikke fullført; annen feil";

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

    private OpprettPersonerByKriterierService service;
    private Bestilling bestilling;
    private Sinks.One<Bestilling> completion;
    private Mono<Void> indexing;

    @BeforeEach
    void setUp() {

        service = spy(new OpprettPersonerByKriterierService(
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
        bestilling = Bestilling.builder().id(BESTILLING_ID).gruppeId(GRUPPE_ID).antallIdenter(1)
                .bestKriterier("{}").miljoer("q2").beskrivelse("Test group").build();
        completion = Sinks.one();
        indexing = Mono.empty();
        doAnswer(_ -> indexing).when(service).saveBestillingToOpenSearchServer(any(), eq(bestilling));
        when(transactionHelperService.oppdaterBestillingFerdig(eq(BESTILLING_ID), any()))
                .thenAnswer(invocation -> Mono.fromSupplier(() -> {
                    bestilling.setFerdig(true);
                    bestilling.setFeil(invocation.getArgument(1));
                    completion.emitValue(bestilling, Sinks.EmitFailureHandler.FAIL_FAST);
                    return bestilling;
                }));
    }

    @Test
    void shouldCreateRequestedNumberOfPersonsInCreationMode() {

        bestilling.setAntallIdenter(2);
        var firstProgress = progress(1);
        var secondProgress = progress(2);
        stubPersonCreation(firstProgress, secondProgress);
        stubSynchronization();
        stubClients();

        service.executeAsync(bestilling);

        var criteriaCaptor = ArgumentCaptor.forClass(RsDollyUtvidetBestilling.class);
        var personCaptor = ArgumentCaptor.forClass(DollyPerson.class);
        var progressCaptor = ArgumentCaptor.forClass(BestillingProgress.class);
        verify(clientRegister, times(2)).gjenopprett(
                criteriaCaptor.capture(), personCaptor.capture(), progressCaptor.capture());
        assertThat(progressCaptor.getAllValues()).containsExactly(firstProgress, secondProgress);
        assertThat(criteriaCaptor.getAllValues()).allSatisfy(criteria -> {
            assertThat(criteria.getId()).isEqualTo(BESTILLING_ID);
            assertThat(criteria.getEnvironments()).containsExactly("q2");
        });
        assertThat(personCaptor.getAllValues()).allSatisfy(person -> {
            assertThat(person.isOpprett()).isTrue();
            assertThat(person.isLeggTil()).isFalse();
        });
        verify(service, times(2)).opprettProgress(BESTILLING_ID, PDLF);
        verify(service).leggIdentTilGruppe(firstProgress, "Test group");
        verify(service).leggIdentTilGruppe(secondProgress, "Test group");
        verify(counterCustomRegistry, times(2)).invoke(any());
        verify(service).saveBestillingToOpenSearchServer(any(), eq(bestilling));
        verify(transactionHelperService).oppdaterBestillingFerdig(BESTILLING_ID, null);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldProcessAtMostThreePersonsConcurrentlyAndWaitForAllPersons() {

        bestilling.setAntallIdenter(4);
        var firstProgress = progress(1);
        var secondProgress = progress(2);
        var thirdProgress = progress(3);
        var fourthProgress = progress(4);
        stubPersonCreation(firstProgress, secondProgress, thirdProgress, fourthProgress);
        var firstSynchronization = TestPublisher.<BestillingProgress>create();
        var secondSynchronization = TestPublisher.<BestillingProgress>create();
        var thirdSynchronization = TestPublisher.<BestillingProgress>create();
        var fourthSynchronization = TestPublisher.<BestillingProgress>create();
        var synchronizations = Map.of(
                firstProgress, firstSynchronization,
                secondProgress, secondSynchronization,
                thirdProgress, thirdSynchronization,
                fourthProgress, fourthSynchronization);
        when(personServiceClient.syncPerson(any(), any())).thenAnswer(invocation ->
                synchronizations.get(invocation.<BestillingProgress>getArgument(1)).mono());
        stubClients();

        service.executeAsync(bestilling);

        firstSynchronization.assertSubscribers();
        secondSynchronization.assertSubscribers();
        thirdSynchronization.assertSubscribers();
        fourthSynchronization.assertNoSubscribers();
        verify(service, times(3)).opprettProgress(BESTILLING_ID, PDLF);
        verify(service, never()).saveBestillingToOpenSearchServer(any(), any());

        firstSynchronization.emit(firstProgress);

        fourthSynchronization.assertSubscribers();
        secondSynchronization.emit(secondProgress);
        thirdSynchronization.emit(thirdProgress);
        assertThat(bestilling.isFerdig()).isFalse();

        fourthSynchronization.emit(fourthProgress);

        verify(clientRegister, times(4)).gjenopprett(any(), any(), any());
        verify(service).saveBestillingToOpenSearchServer(any(), eq(bestilling));
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldWaitForOpenSearchBeforeMarkingBestillingFinished() {

        var progress = progress(1);
        stubPersonCreation(progress);
        stubSynchronization();
        stubClients();
        var pendingIndexing = TestPublisher.<Void>create();
        indexing = pendingIndexing.mono();

        service.executeAsync(bestilling);

        pendingIndexing.assertSubscribers();
        assertThat(bestilling.isFerdig()).isFalse();

        pendingIndexing.complete();

        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldFinishBestillingWhenOpenSearchFails() {

        bestilling.setAntallIdenter(0);
        indexing = Mono.error(new IllegalStateException("OpenSearch unavailable"));

        service.executeAsync(bestilling);

        verify(service, never()).opprettProgress(BESTILLING_ID, PDLF);
        verifyNoInteractions(personServiceClient, clientRegister);
        verify(transactionHelperService).oppdaterBestillingFerdig(BESTILLING_ID, null);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldSkipCreationWhenBestillingIsStopped() {

        when(bestillingService.isStoppet(BESTILLING_ID)).thenReturn(Mono.just(true));

        service.executeAsync(bestilling);

        verify(service, never()).opprettProgress(BESTILLING_ID, PDLF);
        verifyNoInteractions(personServiceClient, clientRegister, counterCustomRegistry);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldSkipCreationWhenCriteriaCannotBeRead() {

        bestilling.setBestKriterier("invalid JSON");

        service.executeAsync(bestilling);

        verify(service, never()).opprettProgress(BESTILLING_ID, PDLF);
        verifyNoInteractions(bestillingService, personServiceClient, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
        assertThat(bestilling.getFeil()).contains("Kunne ikke mappe bestillingskriterier");
    }

    @Test
    void shouldRecordErrorAndSkipClientsWhenPdlSyncFails() {

        var progress = progress(1);
        progress.setPdlSync(false);
        stubPersonCreation(progress);
        stubSynchronization();
        when(transactionHelperService.persister(eq(progress), any(), eq(CREATION_ERROR)))
                .thenReturn(Mono.just(progress));

        service.executeAsync(bestilling);

        verify(transactionHelperService).persister(eq(progress), any(), eq(CREATION_ERROR));
        verifyNoInteractions(clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldContinueWithOtherPersonsWhenSynchronizationFails() {

        bestilling.setAntallIdenter(2);
        var firstProgress = progress(1);
        var secondProgress = progress(2);
        stubPersonCreation(firstProgress, secondProgress);
        when(personServiceClient.syncPerson(any(), any())).thenAnswer(invocation ->
                firstProgress.equals(invocation.getArgument(1)) ?
                        Mono.error(new IllegalStateException("Synchronization failed")) :
                        Mono.just(invocation.getArgument(1)));
        when(transactionHelperService.persister(eq(firstProgress), any(), eq("Feil: Synchronization failed")))
                .thenReturn(Mono.just(firstProgress));
        stubClients();

        service.executeAsync(bestilling);

        verify(transactionHelperService).persister(eq(firstProgress), any(), eq("Feil: Synchronization failed"));
        verify(clientRegister).gjenopprett(any(), any(), eq(secondProgress));
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldRecordTimeoutOnProgressAndFinishBestilling() {

        var progress = progress(1);
        stubPersonCreation(progress);
        when(personServiceClient.syncPerson(any(), any())).thenReturn(Mono.never());
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
        verifyNoInteractions(clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    private void stubPersonCreation(BestillingProgress... progresses) {

        var nextProgress = new AtomicInteger();
        when(bestillingService.isStoppet(BESTILLING_ID)).thenReturn(Mono.just(false));
        doAnswer(_ -> Mono.just(progresses[nextProgress.getAndIncrement()]))
                .when(service).opprettProgress(BESTILLING_ID, PDLF);
        doAnswer(invocation -> Mono.just(invocation.getArgument(1)))
                .when(service).opprettPerson(any(RsDollyUtvidetBestilling.class), any(BestillingProgress.class));
        doAnswer(invocation -> Mono.just(invocation.getArgument(0)))
                .when(service).leggIdentTilGruppe(any(), any());
        doAnswer(invocation -> Mono.just(invocation.getArgument(0))).when(service).sendOrdrePerson(any());
        when(bestillingRepository.findById(BESTILLING_ID)).thenReturn(Mono.just(bestilling));
        when(testgruppeRepository.findById(GRUPPE_ID)).thenReturn(Mono.just(Testgruppe.builder().id(GRUPPE_ID).build()));
    }

    private void stubSynchronization() {

        when(personServiceClient.syncPerson(any(), any())).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(1)));
    }

    private void stubClients() {

        when(clientRegister.gjenopprett(any(), any(), any())).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(2)));
    }

    private static BestillingProgress progress(int index) {

        return BestillingProgress.builder().id((long) index).bestillingId(BESTILLING_ID)
                .ident("1234567890" + index).master(PDLF).pdlSync(true).build();
    }
}
