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
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.jpa.Testident.Master.PDL;
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
class ImportAvPersonerFraPdlServiceTest {

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

    private ImportAvPersonerFraPdlService service;
    private Bestilling bestilling;
    private Sinks.One<Bestilling> completion;
    private Mono<Void> indexing;

    @BeforeEach
    void setUp() {

        service = spy(new ImportAvPersonerFraPdlService(
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
                .bestKriterier("{}").miljoer("q2").beskrivelse("Imported persons")
                .pdlImport(progress(1).getIdent()).build();
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
    void shouldImportAllRequestedIdentsWithCriteriaAndCreationMode() {

        var firstProgress = progress(1);
        var secondProgress = progress(2);
        stubPersonImport(firstProgress, secondProgress);
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
            assertThat(criteria.getBeskrivelse()).isEqualTo("Imported persons");
        });
        assertThat(personCaptor.getAllValues()).allSatisfy(person -> {
            assertThat(person.isOpprett()).isTrue();
            assertThat(person.isLeggTil()).isFalse();
            assertThat(person.getMaster()).isEqualTo(PDL);
        });
        assertThat(personCaptor.getAllValues()).extracting(DollyPerson::getIdent)
                .containsExactly(firstProgress.getIdent(), secondProgress.getIdent());
        verify(service).oppdaterPerson(any(), eq(firstProgress), eq(firstProgress.getIdent()));
        verify(service).oppdaterPerson(any(), eq(secondProgress), eq(secondProgress.getIdent()));
        verify(service).leggIdentTilGruppe(firstProgress, "Imported persons");
        verify(service).leggIdentTilGruppe(secondProgress, "Imported persons");
        verify(service, never()).opprettPerson(any(), any(BestillingProgress.class));
        verify(counterCustomRegistry, times(2)).invoke(any());
        verify(service).saveBestillingToOpenSearchServer(any(), eq(bestilling));
        assertThat(bestilling.isFerdig()).isTrue();
        assertThat(bestilling.getFeil()).isNull();
    }

    @Test
    void shouldImportAtMostThreePersonsConcurrentlyAndWaitForAllPersons() {

        var firstProgress = progress(1);
        var secondProgress = progress(2);
        var thirdProgress = progress(3);
        var fourthProgress = progress(4);
        stubPersonImport(firstProgress, secondProgress, thirdProgress, fourthProgress);
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

    @Test
    void shouldSkipImportWhenBestillingIsStopped() {

        when(bestillingService.isStoppet(BESTILLING_ID)).thenReturn(Mono.just(true));

        service.executeAsync(bestilling);

        verify(service, never()).opprettProgress(eq(BESTILLING_ID), anyString());
        verifyNoInteractions(personServiceClient, clientRegister, counterCustomRegistry);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldSkipImportWhenCriteriaCannotBeRead() {

        bestilling.setBestKriterier("invalid JSON");

        service.executeAsync(bestilling);

        verifyNoInteractions(bestillingService, personServiceClient, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
        assertThat(bestilling.getFeil()).contains("Kunne ikke mappe bestillingskriterier");
    }

    @Test
    void shouldContinueWithOtherPersonsWhenSynchronizationFails() {

        var firstProgress = progress(1);
        var secondProgress = progress(2);
        stubPersonImport(firstProgress, secondProgress);
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
        assertThat(bestilling.getFeil()).isNull();
    }

    @Test
    void shouldRecordUpdateFailureBeforeSynchronization() {

        var progress = progress(1);
        when(bestillingService.isStoppet(BESTILLING_ID)).thenReturn(Mono.just(false));
        doReturn(Mono.just(progress)).when(service).opprettProgress(BESTILLING_ID, progress.getIdent());
        doReturn(Mono.error(new IllegalStateException("PDL update failed")))
                .when(service).oppdaterPerson(any(), eq(progress), eq(progress.getIdent()));
        when(transactionHelperService.persister(eq(progress), any(), eq("Feil: PDL update failed")))
                .thenReturn(Mono.just(progress));

        service.executeAsync(bestilling);

        verify(transactionHelperService).persister(eq(progress), any(), eq("Feil: PDL update failed"));
        verify(service, never()).leggIdentTilGruppe(any(), any());
        verifyNoInteractions(personServiceClient, clientRegister, counterCustomRegistry);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldRecordErrorWhenSynchronizationDoesNotProduceSyncedProgress(boolean emptyResponse) {

        var progress = progress(1);
        progress.setPdlSync(false);
        stubPersonImport(progress);
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
        stubPersonImport(progress);
        when(transactionHelperService.persister(eq(progress), any(), eq(CREATION_ERROR)))
                .thenReturn(Mono.just(progress));

        service.executeAsync(bestilling);

        verify(transactionHelperService).persister(eq(progress), any(), eq(CREATION_ERROR));
        verifyNoInteractions(personServiceClient, clientRegister, counterCustomRegistry);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldRecordTimeoutAndFinishBestilling() {

        var progress = progress(1);
        stubPersonImport(progress);
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
    @NullSource
    @ValueSource(strings = {"State check failed"})
    void shouldFinishBestillingWithErrorWhenStopCheckFails(String message) {

        when(bestillingService.isStoppet(BESTILLING_ID))
                .thenReturn(Mono.error(new IllegalStateException(message)));

        service.executeAsync(bestilling);

        verify(service, never()).saveBestillingToOpenSearchServer(any(), any());
        verifyNoInteractions(personServiceClient, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
        assertThat(bestilling.getFeil()).isEqualTo("Feil ved utføring av : " +
                                                  (isNull(message) ? "IllegalStateException" : message));
    }

    @Test
    void shouldTruncateBestillingErrorToOneThousandCharacters() {

        when(bestillingService.isStoppet(BESTILLING_ID))
                .thenReturn(Mono.error(new IllegalStateException("x".repeat(1100))));

        service.executeAsync(bestilling);

        assertThat(bestilling.isFerdig()).isTrue();
        assertThat(bestilling.getFeil()).hasSize(1000).startsWith("Feil ved utføring av : ");
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldWaitForIndexingAndFinishEvenWhenOpenSearchFails(boolean indexingFails) {

        when(bestillingService.isStoppet(BESTILLING_ID)).thenReturn(Mono.just(true));
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

    private void stubPersonImport(BestillingProgress... progresses) {

        bestilling.setPdlImport(String.join(",", Arrays.stream(progresses)
                .map(BestillingProgress::getIdent).toList()));
        when(bestillingService.isStoppet(BESTILLING_ID)).thenReturn(Mono.just(false));
        for (var progress : progresses) {
            doReturn(Mono.just(progress)).when(service).opprettProgress(BESTILLING_ID, progress.getIdent());
        }
        doAnswer(invocation -> Mono.just(invocation.getArgument(1)))
                .when(service).oppdaterPerson(any(), any(BestillingProgress.class), anyString());
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
                .ident("0181011234" + index).master(PDL).pdlSync(true).build();
    }
}
