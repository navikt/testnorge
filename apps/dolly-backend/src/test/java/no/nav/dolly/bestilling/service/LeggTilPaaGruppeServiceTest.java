package no.nav.dolly.bestilling.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.function.BiConsumer;

import static no.nav.dolly.domain.jpa.Testident.Master.PDLF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
class LeggTilPaaGruppeServiceTest {

    private static final Long BESTILLING_ID = 1L;
    private static final Long GRUPPE_ID = 2L;
    private static final String IDENT_1 = "12345678901";
    private static final String IDENT_2 = "12345678902";

    @Mock
    private ApplicationConfig applicationConfig;

    @Mock
    private BestillingProgressRepository bestillingProgressRepository;

    @Mock
    private BestillingRepository bestillingRepository;

    @Mock
    private BestillingService bestillingService;

    @Mock
    private CounterCustomRegistry counterCustomRegistry;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @Mock
    private IdentService identService;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private JsonMapper jsonMapper;

    @Mock
    private OpenSearchService openSearchService;

    @Mock
    private no.nav.dolly.bestilling.pdldata.PdlDataConsumer pdlDataConsumer;

    @Mock
    private PersonServiceClient personServiceClient;

    @Mock
    private TestgruppeRepository testgruppeRepository;

    @Mock
    private TransactionHelperService transactionHelperService;

    @Mock
    private CacheManager cacheManager;

    private LeggTilPaaGruppeService service;
    private Bestilling bestilling;
    private RsDollyBestillingRequest bestKriterier;

    @BeforeEach
    void setUp() {

        service = spy(new LeggTilPaaGruppeService(
                applicationConfig,
                bestillingProgressRepository,
                bestillingRepository,
                bestillingService,
                counterCustomRegistry,
                errorStatusDecoder,
                identService,
                List.of(),
                mapperFacade,
                jsonMapper,
                openSearchService,
                pdlDataConsumer,
                personServiceClient,
                testgruppeRepository,
                transactionHelperService,
                cacheManager));

        bestilling = Bestilling.builder().id(BESTILLING_ID).gruppeId(GRUPPE_ID).build();
        bestKriterier = new RsDollyBestillingRequest();

        lenient().when(applicationConfig.getOpprettPersonTimeout()).thenReturn(60L);
        lenient().doReturn(bestKriterier).when(service).getDollyBestillingRequest(bestilling);
        lenient().when(bestillingService.isStoppet(BESTILLING_ID)).thenReturn(Mono.just(false));
        lenient().doReturn(Mono.empty()).when(service).saveBestillingToOpenSearchServer(any(), any());
        lenient().doReturn(Mono.just(bestilling)).when(service).doFerdig(bestilling);
        lenient().doAnswer(invocation -> Mono.just(progress(invocation.getArgument(2))))
                .when(service).opprettProgress(eq(bestilling), eq(PDLF), anyString());
        lenient().doAnswer(invocation -> Mono.just(invocation.getArgument(1)))
                .when(service).oppdaterPerson(any(), any());
        lenient().doAnswer(invocation -> Mono.just(invocation.getArgument(0)))
                .when(service).sendOrdrePerson(any());
        lenient().doAnswer(invocation -> Mono.just(DollyPerson.builder()
                        .ident(((BestillingProgress) invocation.getArgument(0)).getIdent()).build()))
                .when(service).opprettDollyPersonLeggTil(any(), any());
        lenient().doAnswer(invocation -> Mono.just(invocation.getArgument(2)))
                .when(service).gjenopprettKlienterStart(any(), any(), any(), anyBoolean());
        lenient().doAnswer(invocation -> Mono.just(invocation.getArgument(2)))
                .when(service).gjenopprettKlienterFerdigstill(any(), any(), any(), anyBoolean());
        lenient().when(personServiceClient.syncPerson(any(), any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(1)));
        lenient().when(transactionHelperService.persister(any(BestillingProgress.class),
                        any(BiConsumer.class), anyString()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        lenient().when(bestillingProgressRepository.save(any(BestillingProgress.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
    }

    @Test
    void shouldFinishBestillingWhenAllIdenterAreHandled() {

        when(identService.getTestidenterByGruppeId(GRUPPE_ID))
                .thenReturn(Flux.just(testident(IDENT_1), testident(IDENT_2)));

        service.executeAsync(bestilling);

        verify(personServiceClient, times(2)).syncPerson(any(), any());
        verify(service, times(2)).gjenopprettKlienterFerdigstill(any(), any(), any(), anyBoolean());
        verify(service).saveBestillingToOpenSearchServer(any(), eq(bestilling));
        verify(service).doFerdig(bestilling);
    }

    @Test
    void shouldHandleRemainingIdenterWhenOneIdentFails() {

        when(identService.getTestidenterByGruppeId(GRUPPE_ID))
                .thenReturn(Flux.just(testident(IDENT_1), testident(IDENT_2)));
        doAnswer(invocation -> {
            BestillingProgress progress = invocation.getArgument(0);
            return IDENT_1.equals(progress.getIdent()) ?
                    Mono.error(new IllegalStateException("Feil i første ident")) :
                    Mono.just(DollyPerson.builder().ident(progress.getIdent()).build());
        }).when(service).opprettDollyPersonLeggTil(any(), any());

        service.executeAsync(bestilling);

        verify(personServiceClient, times(1)).syncPerson(any(), any());
        verify(service, times(1)).gjenopprettKlienterFerdigstill(any(), any(), any(), anyBoolean());
        verify(service).doFerdig(bestilling);
    }

    @Test
    void shouldStoreErrorOnProgressOfFailingIdent() {

        when(identService.getTestidenterByGruppeId(GRUPPE_ID))
                .thenReturn(Flux.just(testident(IDENT_1)));
        doReturn(Mono.error(new IllegalStateException("Feil i første ident")))
                .when(service).opprettDollyPersonLeggTil(any(), any());

        service.executeAsync(bestilling);

        verify(transactionHelperService).persister(
                org.mockito.ArgumentMatchers.argThat(progress -> IDENT_1.equals(progress.getIdent())),
                any(BiConsumer.class), eq("Feil: Feil i første ident"));
        verify(service).doFerdig(bestilling);
    }

    @Test
    void shouldStoreErrorWhenProgressCouldNotBeCreated() {

        when(identService.getTestidenterByGruppeId(GRUPPE_ID))
                .thenReturn(Flux.just(testident(IDENT_1)));
        doReturn(Mono.error(new IllegalStateException("Kunne ikke lagre progress")))
                .when(service).opprettProgress(bestilling, PDLF, IDENT_1);

        service.executeAsync(bestilling);

        verify(bestillingProgressRepository).save(org.mockito.ArgumentMatchers.argThat(
                (BestillingProgress progress) -> IDENT_1.equals(progress.getIdent()) &&
                        progress.getFeil().contains("Kunne ikke lagre progress")));
        verify(service).doFerdig(bestilling);
    }

    @Test
    void shouldNotRecordErrorsWhenBestillingIsStopped() {

        when(identService.getTestidenterByGruppeId(GRUPPE_ID))
                .thenReturn(Flux.just(testident(IDENT_1), testident(IDENT_2)));
        when(bestillingService.isStoppet(BESTILLING_ID)).thenReturn(Mono.just(true));

        service.executeAsync(bestilling);

        verify(service, never()).opprettProgress(any(), any(), anyString());
        verifyNoInteractions(personServiceClient);
        verify(bestillingProgressRepository, never()).save(any(BestillingProgress.class));
        verify(service, never()).saveBestillingToOpenSearchServer(any(), any());
        verify(service).doFerdig(bestilling);
    }

    @Test
    void shouldUseExceptionNameWhenErrorHasNoMessage() {

        when(identService.getTestidenterByGruppeId(GRUPPE_ID)).thenReturn(Flux.just(testident(IDENT_1)));
        doReturn(Mono.error(new IllegalStateException()))
                .when(service).opprettDollyPersonLeggTil(any(), any());

        service.executeAsync(bestilling);

        verify(transactionHelperService).persister(any(BestillingProgress.class), any(BiConsumer.class),
                eq("Feil: IllegalStateException"));
        verify(service).doFerdig(bestilling);
    }

    @Test
    void shouldFinishBestillingWhenProgressCannotBeUpdatedWithError() {

        when(identService.getTestidenterByGruppeId(GRUPPE_ID))
                .thenReturn(Flux.just(testident(IDENT_1), testident(IDENT_2)));
        doReturn(Mono.error(new IllegalStateException("feil")))
                .when(service).opprettDollyPersonLeggTil(any(), any());
        when(transactionHelperService.persister(any(BestillingProgress.class), any(BiConsumer.class), anyString()))
                .thenReturn(Mono.empty());

        service.executeAsync(bestilling);

        verify(transactionHelperService, times(2))
                .persister(any(BestillingProgress.class), any(BiConsumer.class), anyString());
        verify(service).doFerdig(bestilling);
    }

    @Test
    void shouldNotFailWhenSettingBestillingFerdigFailsAfterError() {

        when(identService.getTestidenterByGruppeId(GRUPPE_ID))
                .thenReturn(Flux.error(new IllegalStateException()));
        when(transactionHelperService.oppdaterBestillingFerdig(eq(BESTILLING_ID), contains("IllegalStateException")))
                .thenReturn(Mono.error(new IllegalStateException("db nede")));

        service.executeAsync(bestilling);

        verify(transactionHelperService).oppdaterBestillingFerdig(eq(BESTILLING_ID), contains("IllegalStateException"));
    }

    @Test
    void shouldNotFailWhenDoFerdigFailsAfterCompletion() {

        when(identService.getTestidenterByGruppeId(GRUPPE_ID)).thenReturn(Flux.just(testident(IDENT_1)));
        doReturn(Mono.error(new IllegalStateException("db nede"))).when(service).doFerdig(bestilling);

        service.executeAsync(bestilling);

        verify(service).doFerdig(bestilling);
    }

    @Test
    void shouldUseProgressReturnedFromSendOrdrePersonInLaterSteps() {

        var updatedProgress = BestillingProgress.builder().id(99L).bestillingId(BESTILLING_ID)
                .ident(IDENT_1).master(PDLF).pdlSync(true).build();
        when(identService.getTestidenterByGruppeId(GRUPPE_ID)).thenReturn(Flux.just(testident(IDENT_1)));
        doReturn(Mono.just(updatedProgress)).when(service).sendOrdrePerson(any());

        service.executeAsync(bestilling);

        verify(service).opprettDollyPersonLeggTil(updatedProgress, bestilling.getBruker());
        verify(personServiceClient).syncPerson(any(), eq(updatedProgress));
        verify(service).doFerdig(bestilling);
    }

    @Test
    void shouldRecordErrorWhenIdentIsInvalid() {

        when(identService.getTestidenterByGruppeId(GRUPPE_ID))
                .thenReturn(Flux.just(testident(IDENT_1)));
        doReturn(Mono.just(BestillingProgress.builder().id(10L).bestillingId(BESTILLING_ID)
                .ident("ugyldig").master(PDLF).build()))
                .when(service).opprettProgress(bestilling, PDLF, IDENT_1);

        service.executeAsync(bestilling);

        verifyNoInteractions(personServiceClient);
        verify(transactionHelperService).persister(any(BestillingProgress.class), any(BiConsumer.class),
                eq("Feil: Oppretting ikke fullført; annen feil"));
        verify(service).doFerdig(bestilling);
    }

    @Test
    void shouldRecordErrorAndSkipFerdigstillWhenPdlSyncFails() {

        when(identService.getTestidenterByGruppeId(GRUPPE_ID))
                .thenReturn(Flux.just(testident(IDENT_1)));
        doAnswer(invocation -> {
            BestillingProgress progress = invocation.getArgument(1);
            progress.setPdlSync(false);
            return Mono.just(progress);
        }).when(personServiceClient).syncPerson(any(), any());

        service.executeAsync(bestilling);

        verify(service, never()).gjenopprettKlienterFerdigstill(any(), any(), any(), anyBoolean());
        verify(transactionHelperService).persister(any(BestillingProgress.class), any(BiConsumer.class),
                eq("Feil: Oppretting ikke fullført; annen feil"));
        verify(service).doFerdig(bestilling);
    }

    @Test
    void shouldRecordErrorWhenIdentTakesTooLong() {

        when(applicationConfig.getOpprettPersonTimeout()).thenReturn(1L);
        when(identService.getTestidenterByGruppeId(GRUPPE_ID))
                .thenReturn(Flux.just(testident(IDENT_1)));
        doReturn(Mono.never()).when(personServiceClient).syncPerson(any(), any());

        service.executeAsync(bestilling);

        verify(transactionHelperService, org.mockito.Mockito.timeout(5000))
                .persister(any(BestillingProgress.class), any(BiConsumer.class), anyString());
        verify(service, org.mockito.Mockito.timeout(5000)).doFerdig(bestilling);
    }

    @Test
    void shouldFinishBestillingWithErrorWhenFetchingIdenterFails() {

        when(identService.getTestidenterByGruppeId(GRUPPE_ID))
                .thenReturn(Flux.error(new IllegalStateException("Database nede")));
        when(transactionHelperService.oppdaterBestillingFerdig(eq(BESTILLING_ID), anyString()))
                .thenReturn(Mono.just(bestilling));

        service.executeAsync(bestilling);

        verify(transactionHelperService).oppdaterBestillingFerdig(eq(BESTILLING_ID), contains("Database nede"));
        verify(service, never()).doFerdig(any());
        verify(service, never()).saveBestillingToOpenSearchServer(any(), any());
    }

    @Test
    void shouldFinishBestillingWhenOpenSearchFails() {

        when(identService.getTestidenterByGruppeId(GRUPPE_ID)).thenReturn(Flux.just(testident(IDENT_1)));
        doReturn(Mono.error(new IllegalStateException("OpenSearch nede")))
                .when(service).saveBestillingToOpenSearchServer(any(), eq(bestilling));

        service.executeAsync(bestilling);

        verify(service).doFerdig(bestilling);
    }

    @Test
    void shouldFinishWithoutHandlingIdenterWhenBestillingCriteriaHaveError() {

        bestKriterier.setFeil("Feil: Kunne ikke mappe bestillingskriterier, se logg!");

        service.executeAsync(bestilling);

        verifyNoInteractions(identService, personServiceClient);
        verify(service).doFerdig(bestilling);
    }

    @Test
    void shouldNotCallCounterWhenIdentFailsBeforeUpdate() {

        when(identService.getTestidenterByGruppeId(GRUPPE_ID)).thenReturn(Flux.just(testident(IDENT_1)));
        doReturn(Mono.error(new IllegalStateException("feil")))
                .when(service).opprettDollyPersonLeggTil(any(), any());

        service.executeAsync(bestilling);

        verifyNoInteractions(counterCustomRegistry);
        assertThat(bestKriterier.getFeil()).isNull();
    }

    private static BestillingProgress progress(String ident) {

        return BestillingProgress.builder()
                .id(ident.hashCode() + 0L)
                .bestillingId(BESTILLING_ID)
                .ident(ident)
                .master(PDLF)
                .pdlSync(true)
                .build();
    }

    private static Testident testident(String ident) {

        return Testident.builder().ident(ident).master(PDLF).build();
    }
}
