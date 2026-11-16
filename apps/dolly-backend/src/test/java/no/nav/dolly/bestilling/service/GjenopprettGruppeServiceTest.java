package no.nav.dolly.bestilling.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.projection.GruppeBestillingIdent;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
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
class GjenopprettGruppeServiceTest {

    private static final Long BESTILLING_ID = 1L;
    private static final Long GRUPPE_ID = 2L;
    private static final String IDENT_1 = "12345678901";
    private static final String IDENT_2 = "12345678902";

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

    private GjenopprettGruppeService service;
    private Bestilling bestilling;
    private RsDollyBestillingRequest bestKriterier;
    private Sinks.One<Bestilling> completion;

    @BeforeEach
    void setUp() {

        service = spy(new GjenopprettGruppeService(
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
        bestilling = Bestilling.builder().id(BESTILLING_ID).gruppeId(GRUPPE_ID).miljoer("q2").build();
        bestKriterier = new RsDollyBestillingRequest();
        completion = Sinks.one();
        doReturn(Mono.just(bestKriterier)).when(service).getDollyBestillingRequest(bestilling);
        when(transactionHelperService.oppdaterBestillingFerdig(eq(BESTILLING_ID), any()))
                .thenAnswer(invocation -> Mono.fromSupplier(() -> {
                    bestilling.setFerdig(true);
                    bestilling.setFeil(invocation.getArgument(1));
                    completion.emitValue(bestilling, Sinks.EmitFailureHandler.FAIL_FAST);
                    return bestilling;
                }));
    }

    @Test
    void shouldRestoreEligibleHistoricalOrdersForEachPerson() {

        stubGroup(IDENT_1, IDENT_2);
        var firstProgress = stubPerson(IDENT_1);
        var secondProgress = stubPerson(IDENT_2);
        when(identRepository.getBestillingerFromGruppe(GRUPPE_ID)).thenReturn(Flux.just(
                historicalOrder(10L, IDENT_2, "{}"),
                historicalOrder(11L, IDENT_1, "{}"),
                historicalOrder(12L, IDENT_1, "{}"),
                historicalOrder(13L, IDENT_1, "{\"instdata\":[]}"),
                historicalOrder(14L, IDENT_2, "{}"),
                historicalOrder(15L, IDENT_2, "{\"instdata\":[]}")));
        stubClientRestoration();

        service.executeAsync(bestilling);

        var requestCaptor = ArgumentCaptor.forClass(RsDollyUtvidetBestilling.class);
        var personCaptor = ArgumentCaptor.forClass(DollyPerson.class);
        var progressCaptor = ArgumentCaptor.forClass(BestillingProgress.class);
        verify(clientRegister, times(4)).gjenopprett(
                requestCaptor.capture(), personCaptor.capture(), progressCaptor.capture());
        assertThat(requestCaptor.getAllValues()).extracting(RsDollyUtvidetBestilling::getId)
                .containsExactly(11L, 13L, 10L, 15L);
        assertThat(personCaptor.getAllValues()).extracting(DollyPerson::getIdent)
                .containsExactly(IDENT_1, IDENT_1, IDENT_2, IDENT_2);
        assertThat(personCaptor.getAllValues()).allSatisfy(person -> {
            assertThat(person.isOpprett()).isFalse();
            assertThat(person.isLeggTil()).isFalse();
        });
        assertThat(progressCaptor.getAllValues())
                .containsExactly(firstProgress, firstProgress, secondProgress, secondProgress);
        assertThat(bestKriterier.getEkskluderEksternePersoner()).isTrue();
        assertThat(bestilling.isFerdig()).isTrue();
        verify(transactionHelperService).oppdaterBestillingFerdig(BESTILLING_ID, null);
    }

    @Test
    void shouldWaitForHistoricalOrdersBeforeFinishingBestilling() {

        stubGroup(IDENT_1);
        var progress = stubPerson(IDENT_1);
        when(identRepository.getBestillingerFromGruppe(GRUPPE_ID)).thenReturn(Flux.just(
                historicalOrder(10L, IDENT_1, "{}"),
                historicalOrder(11L, IDENT_1, "{\"instdata\":[]}")));
        var firstRestoration = TestPublisher.<BestillingProgress>create();
        var secondRestoration = TestPublisher.<BestillingProgress>create();
        doReturn(firstRestoration.mono()).when(clientRegister).gjenopprett(
                argThat(request -> Long.valueOf(10L).equals(request.getId())), any(), eq(progress));
        doReturn(secondRestoration.mono()).when(clientRegister).gjenopprett(
                argThat(request -> Long.valueOf(11L).equals(request.getId())), any(), eq(progress));

        service.executeAsync(bestilling);

        firstRestoration.assertSubscribers();
        secondRestoration.assertNoSubscribers();
        verify(transactionHelperService, never()).oppdaterBestillingFerdig(any(), any());

        firstRestoration.emit(progress);

        secondRestoration.assertSubscribers();
        assertThat(bestilling.isFerdig()).isFalse();

        secondRestoration.emit(progress);

        assertThat(bestilling.isFerdig()).isTrue();
        verify(transactionHelperService).oppdaterBestillingFerdig(BESTILLING_ID, null);
    }

    @Test
    void shouldFinishBestillingWhenGroupIsEmpty() {

        when(identService.getTestidenterByGruppeId(GRUPPE_ID)).thenReturn(Flux.empty());

        service.executeAsync(bestilling);

        assertThat(bestilling.isFerdig()).isTrue();
        verifyNoInteractions(personServiceClient, identRepository, clientRegister);
    }

    @Test
    void shouldSkipPersonsWhenBestillingIsStopped() {

        stubGroup(IDENT_1, IDENT_2);
        when(bestillingService.isStoppet(BESTILLING_ID)).thenReturn(Mono.just(true));

        service.executeAsync(bestilling);

        verify(service, never()).opprettProgress(any(), anyString());
        verifyNoInteractions(personServiceClient, identRepository, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldFinishWithoutProcessingPersonsWhenCriteriaHaveError() {

        bestKriterier.setFeil("Feil: Kunne ikke mappe bestillingskriterier, se logg!");

        service.executeAsync(bestilling);

        verifyNoInteractions(identService, personServiceClient, identRepository, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldSkipHistoricalOrdersWhenPdlSyncFails() {

        stubGroup(IDENT_1);
        var progress = stubPerson(IDENT_1);
        progress.setPdlSync(false);

        service.executeAsync(bestilling);

        verifyNoInteractions(identRepository, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldFinishBestillingWhenPersonHasNoHistoricalOrders() {

        stubGroup(IDENT_1);
        stubPerson(IDENT_1);
        when(identRepository.getBestillingerFromGruppe(GRUPPE_ID)).thenReturn(Flux.empty());

        service.executeAsync(bestilling);

        verifyNoInteractions(clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldContinueWithOtherPersonsWhenRestorationFails() {

        stubGroup(IDENT_1, IDENT_2);
        var firstProgress = stubPerson(IDENT_1);
        var secondProgress = stubPerson(IDENT_2);
        when(identRepository.getBestillingerFromGruppe(GRUPPE_ID)).thenReturn(Flux.just(
                historicalOrder(10L, IDENT_1, "{}"),
                historicalOrder(11L, IDENT_2, "{}")));
        when(clientRegister.gjenopprett(any(), any(), eq(firstProgress)))
                .thenReturn(Mono.error(new IllegalStateException("Restoration failed")));
        when(clientRegister.gjenopprett(any(), any(), eq(secondProgress)))
                .thenReturn(Mono.just(secondProgress));
        when(errorStatusDecoder.getErrorText(any(), any())).thenReturn("Feil: Restoration failed");
        when(transactionHelperService.persister(eq(firstProgress), any(),
                eq("Feil: Restoration failed"))).thenReturn(Mono.just(firstProgress));

        service.executeAsync(bestilling);

        verify(transactionHelperService).persister(eq(firstProgress), any(),
                eq("Feil: Restoration failed"));
        verify(clientRegister).gjenopprett(any(), any(), eq(secondProgress));
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldPersistErrorWhenProgressCannotBeCreated() {

        stubGroup(IDENT_1);
        doReturn(Mono.error(new IllegalStateException("Could not save progress")))
                .when(service).opprettProgress(BESTILLING_ID, IDENT_1);
        when(bestillingProgressRepository.save(any())).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0)));

        service.executeAsync(bestilling);

        var progressCaptor = ArgumentCaptor.forClass(BestillingProgress.class);
        verify(bestillingProgressRepository).save(progressCaptor.capture());
        assertThat(progressCaptor.getValue().getBestillingId()).isEqualTo(BESTILLING_ID);
        assertThat(progressCaptor.getValue().getIdent()).isEqualTo(IDENT_1);
        assertThat(progressCaptor.getValue().getFeil()).isEqualTo("Feil: Could not save progress");
        verifyNoInteractions(personServiceClient, clientRegister);
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldRecordTimeoutAndFinishBestilling() {

        stubGroup(IDENT_1);
        var progress = progress(IDENT_1);
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
        assertThat(bestilling.isFerdig()).isTrue();
    }

    @Test
    void shouldFinishBestillingWithErrorWhenFetchingPersonsFails() {

        when(identService.getTestidenterByGruppeId(GRUPPE_ID))
                .thenReturn(Flux.error(new IllegalStateException("Database unavailable")));

        service.executeAsync(bestilling);

        verify(transactionHelperService).oppdaterBestillingFerdig(
                BESTILLING_ID, "Feil ved utføring av : Database unavailable");
        verify(service, never()).doFerdig(any());
        assertThat(bestilling.isFerdig()).isTrue();
        assertThat(bestilling.getFeil()).contains("Database unavailable");
    }

    private void stubGroup(String... identer) {

        when(identService.getTestidenterByGruppeId(GRUPPE_ID)).thenReturn(
                Flux.fromArray(identer).map(ident -> Testident.builder().ident(ident).gruppeId(GRUPPE_ID).build()));
        when(bestillingService.isStoppet(BESTILLING_ID)).thenReturn(Mono.just(false));
    }

    private BestillingProgress stubPerson(String ident) {

        var progress = progress(ident);
        stubPerson(progress, Mono.just(progress));
        return progress;
    }

    private void stubPerson(BestillingProgress progress, Mono<BestillingProgress> synchronization) {

        var person = DollyPerson.builder().ident(progress.getIdent()).master(PDLF).build();
        doReturn(Mono.just(progress)).when(service).opprettProgress(BESTILLING_ID, progress.getIdent());
        doReturn(Mono.just(progress)).when(service).sendOrdrePerson(progress);
        doReturn(Mono.just(person)).when(service).opprettDollyPersonForGjenopprett(progress);
        when(personServiceClient.syncPerson(person, progress)).thenReturn(synchronization);
    }

    private void stubClientRestoration() {

        when(clientRegister.gjenopprett(any(), any(), any())).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(2)));
    }

    private static BestillingProgress progress(String ident) {

        return BestillingProgress.builder().id(Long.valueOf(ident)).bestillingId(BESTILLING_ID)
                .ident(ident).master(PDLF).pdlSync(true).build();
    }

    private static GruppeBestillingIdent historicalOrder(Long id, String ident, String criteria) {

        return new GruppeBestillingIdent(ident, id, criteria, "q2");
    }
}
