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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static no.nav.dolly.domain.jpa.Testident.Master.PDLF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpprettPersonerFraIdenterMedKriterierServiceTest {

    private static final Long BESTILLING_ID = 1L;
    private static final Long GRUPPE_ID = 2L;
    private static final String CREATION_ERROR = "Feil: Oppretting ikke fullført; annen feil";

    @Mock
    private AvailCheckService availCheckService;
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

    private OpprettPersonerFraIdenterMedKriterierService service;
    private Bestilling bestilling;
    private Sinks.One<Bestilling> completion;
    private Mono<Void> indexing;

    @BeforeEach
    void setUp() {

        service = spy(new OpprettPersonerFraIdenterMedKriterierService(
                availCheckService,
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
        bestilling = Bestilling.builder().id(BESTILLING_ID).gruppeId(GRUPPE_ID)
                .bestKriterier("{}").miljoer("q2").beskrivelse("Test group")
                .opprettFraIdenter("12345678901,12345678902").build();
        completion = Sinks.one();
        indexing = Mono.empty();
        lenient().doAnswer(_ -> indexing).when(service).saveBestillingToOpenSearchServer(any(), eq(bestilling));
        when(transactionHelperService.oppdaterBestillingFerdig(eq(BESTILLING_ID), any()))
                .thenAnswer(invocation -> Mono.fromSupplier(() -> {
                    bestilling.setFerdig(true);
                    bestilling.setFeil(invocation.getArgument(1));
                    completion.emitValue(bestilling, Sinks.EmitFailureHandler.FAIL_FAST);
                    return bestilling;
                }));
    }

    @Test
    void shouldCreateOnlyAvailablePersonsWithRequestedCriteria() {

        var firstProgress = progress(1);
        var secondProgress = progress(2);
        bestilling.setOpprettFraIdenter("12345678901,12345678902,12345678903");
        when(availCheckService.checkAvailable(bestilling.getOpprettFraIdenter()))
                .thenReturn(Flux.just(availability(firstProgress.getIdent(), true),
                        availability(secondProgress.getIdent(), true), availability("12345678903", false)));
        stubPersonCreation(firstProgress, secondProgress);
        stubDollyPersonCreation();
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
            assertThat(criteria.getBeskrivelse()).isEqualTo("Test group");
        });
        assertThat(personCaptor.getAllValues()).allSatisfy(person -> {
            assertThat(person.isOpprett()).isTrue();
            assertThat(person.isLeggTil()).isFalse();
        });
        verify(service).opprettProgress(BESTILLING_ID, firstProgress.getIdent());
        verify(service).opprettProgress(BESTILLING_ID, secondProgress.getIdent());
        verify(service, never()).opprettProgress(BESTILLING_ID, "12345678903");
        verify(service).leggIdentTilGruppe(firstProgress, "Test group");
        verify(service).leggIdentTilGruppe(secondProgress, "Test group");
        verify(counterCustomRegistry, times(2)).invoke(any());
        verify(service).saveBestillingToOpenSearchServer(any(), eq(bestilling));
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldProcessAtMostThreePersonsConcurrentlyAndWaitForAllPersons() {

        var firstProgress = progress(1);
        var secondProgress = progress(2);
        var thirdProgress = progress(3);
        var fourthProgress = progress(4);
        stubAvailablePersons(firstProgress, secondProgress, thirdProgress, fourthProgress);
        stubPersonCreation(firstProgress, secondProgress, thirdProgress, fourthProgress);
        stubDollyPersonCreation();
        stubClients();
        var firstSynchronization = TestPublisher.<BestillingProgress>create();
        var secondSynchronization = TestPublisher.<BestillingProgress>create();
        var thirdSynchronization = TestPublisher.<BestillingProgress>create();
        var fourthSynchronization = TestPublisher.<BestillingProgress>create();
        var synchronizations = Map.of(
                firstProgress.getIdent(), firstSynchronization,
                secondProgress.getIdent(), secondSynchronization,
                thirdProgress.getIdent(), thirdSynchronization,
                fourthProgress.getIdent(), fourthSynchronization);
        when(personServiceClient.syncPerson(any(), any())).thenAnswer(invocation ->
                synchronizations.get(invocation.<BestillingProgress>getArgument(1).getIdent()).mono());

        service.executeAsync(bestilling);

        firstSynchronization.assertSubscribers();
        secondSynchronization.assertSubscribers();
        thirdSynchronization.assertSubscribers();
        fourthSynchronization.assertNoSubscribers();
        verify(service, times(3)).opprettProgress(eq(BESTILLING_ID), anyString());
        verify(service, never()).saveBestillingToOpenSearchServer(any(), any());

        firstSynchronization.emit(firstProgress);

        fourthSynchronization.assertSubscribers();
        secondSynchronization.emit(secondProgress);
        thirdSynchronization.emit(thirdProgress);
        assertThat(bestilling.isFerdig()).isFalse();

        fourthSynchronization.emit(fourthProgress);

        verify(clientRegister, times(4)).gjenopprett(any(), any(), any());
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldFinishWithoutCreatingPersonsWhenNoneAreAvailable(boolean hasUnavailableIdent) {

        when(availCheckService.checkAvailable(bestilling.getOpprettFraIdenter()))
                .thenReturn(hasUnavailableIdent ? Flux.just(availability("12345678901", false)) : Flux.empty());

        service.executeAsync(bestilling);

        verifyNoInteractions(bestillingService, bestillingProgressRepository, personServiceClient, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldSkipCreationWhenBestillingIsStopped() {

        stubAvailablePersons(progress(1));
        when(bestillingService.isStoppet(BESTILLING_ID)).thenReturn(Mono.just(true));

        service.executeAsync(bestilling);

        verify(service, never()).opprettProgress(eq(BESTILLING_ID), anyString());
        verifyNoInteractions(personServiceClient, clientRegister, counterCustomRegistry);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldSkipAvailabilityCheckWhenCriteriaCannotBeRead() {

        bestilling.setBestKriterier("invalid JSON");

        service.executeAsync(bestilling);

        verifyNoInteractions(availCheckService, bestillingService, personServiceClient, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
        assertThat(bestilling.getFeil()).contains("Kunne ikke mappe bestillingskriterier");
    }

    @Test
    void shouldFinishBestillingWithErrorWhenAvailabilityCheckFails() {

        when(availCheckService.checkAvailable(bestilling.getOpprettFraIdenter()))
                .thenReturn(Flux.error(new IllegalStateException("Availability check failed")));

        service.executeAsync(bestilling);

        verify(service, never()).saveBestillingToOpenSearchServer(any(), any());
        verifyNoInteractions(bestillingService, personServiceClient, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
        assertThat(bestilling.getFeil()).isEqualTo("Feil ved utføring av : Availability check failed");
    }

    @Test
    void shouldContinueWithOtherPersonsWhenSynchronizationFails() {

        var firstProgress = progress(1);
        var secondProgress = progress(2);
        stubAvailablePersons(firstProgress, secondProgress);
        stubPersonCreation(firstProgress, secondProgress);
        stubDollyPersonCreation();
        stubClients();
        when(personServiceClient.syncPerson(any(), any())).thenAnswer(invocation ->
                firstProgress.getIdent().equals(invocation.<BestillingProgress>getArgument(1).getIdent()) ?
                        Mono.error(new IllegalStateException("Synchronization failed")) :
                        Mono.just(invocation.getArgument(1)));
        when(transactionHelperService.persister(eq(firstProgress), any(), eq("Feil: Synchronization failed")))
                .thenReturn(Mono.just(firstProgress));

        service.executeAsync(bestilling);

        verify(transactionHelperService).persister(eq(firstProgress), any(), eq("Feil: Synchronization failed"));
        verify(clientRegister).gjenopprett(any(), any(), eq(secondProgress));
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldRecordErrorWhenSynchronizationDoesNotProduceSyncedProgress(boolean emptyResponse) {

        var progress = progress(1);
        progress.setPdlSync(false);
        stubAvailablePersons(progress);
        stubPersonCreation(progress);
        stubDollyPersonCreation();
        when(personServiceClient.syncPerson(any(), any()))
                .thenReturn(emptyResponse ? Mono.empty() : Mono.just(progress));
        when(transactionHelperService.persister(eq(progress), any(), eq(CREATION_ERROR)))
                .thenReturn(Mono.just(progress));

        service.executeAsync(bestilling);

        verify(transactionHelperService).persister(eq(progress), any(), eq(CREATION_ERROR));
        verifyNoInteractions(clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldRecordErrorAndSkipSynchronizationWhenIdentIsInvalid() {

        var progress = progress(1);
        progress.setIdent("invalid");
        stubAvailablePersons(progress);
        stubPersonCreation(progress);
        when(transactionHelperService.persister(eq(progress), any(), eq(CREATION_ERROR)))
                .thenReturn(Mono.just(progress));

        service.executeAsync(bestilling);

        verify(transactionHelperService).persister(eq(progress), any(), eq(CREATION_ERROR));
        verifyNoInteractions(personServiceClient, clientRegister, counterCustomRegistry);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldSaveErrorWhenProgressCannotBeCreated() {

        var ident = progress(1).getIdent();
        stubAvailablePersons(progress(1));
        when(bestillingService.isStoppet(BESTILLING_ID)).thenReturn(Mono.just(false));
        doReturn(Mono.error(new IllegalStateException("Progress creation failed")))
                .when(service).opprettProgress(BESTILLING_ID, ident);
        when(bestillingProgressRepository.save(any())).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0)));

        service.executeAsync(bestilling);

        var progressCaptor = ArgumentCaptor.forClass(BestillingProgress.class);
        verify(bestillingProgressRepository).save(progressCaptor.capture());
        assertThat(progressCaptor.getValue().getBestillingId()).isEqualTo(BESTILLING_ID);
        assertThat(progressCaptor.getValue().getIdent()).isEqualTo(ident);
        assertThat(progressCaptor.getValue().getFeil()).isEqualTo("Feil: Progress creation failed");
        verifyNoInteractions(personServiceClient, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldRecordTimeoutOnProgressAndFinishBestilling() {

        var progress = progress(1);
        stubAvailablePersons(progress);
        stubPersonCreation(progress);
        stubDollyPersonCreation();
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
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldWaitForOpenSearchAndFinishEvenWhenIndexingFails(boolean indexingFails) {

        when(availCheckService.checkAvailable(bestilling.getOpprettFraIdenter())).thenReturn(Flux.empty());
        var pendingIndexing = TestPublisher.<Void>create();
        indexing = pendingIndexing.mono();

        service.executeAsync(bestilling);

        pendingIndexing.assertSubscribers();
        assertThat(bestilling.isFerdig()).isFalse();

        if (indexingFails) {
            pendingIndexing.error(new IllegalStateException("OpenSearch unavailable"));
        } else {
            pendingIndexing.complete();
        }

        assertThat(bestilling.isFerdig()).isTrue();
        assertThat(bestilling.getFeil()).isNull();
    }

    private void stubAvailablePersons(BestillingProgress... progresses) {

        var statuses = Arrays.stream(progresses)
                .map(progress -> availability(progress.getIdent(), true))
                .toList();
        bestilling.setOpprettFraIdenter(String.join(",", statuses.stream()
                .map(AvailCheckService.AvailStatus::getIdent).toList()));
        when(availCheckService.checkAvailable(bestilling.getOpprettFraIdenter()))
                .thenReturn(Flux.fromIterable(statuses));
    }

    private void stubPersonCreation(BestillingProgress... progresses) {

        when(bestillingService.isStoppet(BESTILLING_ID)).thenReturn(Mono.just(false));
        for (var progress : progresses) {
            doReturn(Mono.just(progress)).when(service).opprettProgress(BESTILLING_ID, progress.getIdent());
        }
        doAnswer(invocation -> Mono.just(invocation.getArgument(1)))
                .when(service).opprettPerson(any(RsDollyUtvidetBestilling.class), any(BestillingProgress.class));
        doAnswer(invocation -> Mono.just(invocation.getArgument(0)))
                .when(service).leggIdentTilGruppe(any(), any());
        doAnswer(invocation -> Mono.just(invocation.getArgument(0))).when(service).sendOrdrePerson(any());
    }

    private void stubDollyPersonCreation() {

        when(bestillingRepository.findById(BESTILLING_ID)).thenReturn(Mono.just(bestilling));
        when(testgruppeRepository.findById(GRUPPE_ID))
                .thenReturn(Mono.just(Testgruppe.builder().id(GRUPPE_ID).build()));
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

    private static AvailCheckService.AvailStatus availability(String ident, boolean available) {

        return AvailCheckService.AvailStatus.builder().ident(ident).available(available).build();
    }
}
