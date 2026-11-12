package no.nav.dolly.bestilling.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pdldata.dto.PdlResponse;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.opensearch.service.OpenSearchService;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BestillingRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.IdentRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.function.BiConsumer;

import static no.nav.dolly.domain.jpa.Testident.Master.PDL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DollyBestillingServiceTest {

    private static final Long BESTILLING_ID = 1L;
    private static final Long GRUPPE_ID = 2L;
    private static final String IDENT = "12345678901";
    private static final String BESKRIVELSE = "beskrivelse";

    @Mock
    private BestillingProgressRepository bestillingProgressRepository;

    @Mock
    private BestillingRepository bestillingRepository;

    @Mock
    private BestillingService bestillingService;

    @Mock
    private org.springframework.cache.CacheManager cacheManager;

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
    private PdlDataConsumer pdlDataConsumer;

    @Mock
    private TestgruppeRepository testgruppeRepository;

    @Mock
    private TransactionHelperService transactionHelperService;

    private DollyBestillingService dollyBestillingService;

    @BeforeEach
    void setUp() {
        dollyBestillingService = new DollyBestillingService(
                bestillingProgressRepository,
                bestillingRepository,
                bestillingService,
                cacheManager,
                counterCustomRegistry,
                errorStatusDecoder,
                identService,
                jsonMapper,
                List.<ClientRegister>of(),
                mapperFacade,
                openSearchService,
                pdlDataConsumer,
                testgruppeRepository,
                transactionHelperService
        );
    }

    @Test
    void shouldMarkDollyPersonAsLeggTilAndPreservePersonDetails() {

        var progress = BestillingProgress.builder()
                .bestillingId(BESTILLING_ID).ident(IDENT).master(PDL).build();
        given(bestillingRepository.findById(BESTILLING_ID))
                .willReturn(Mono.just(Bestilling.builder().gruppeId(GRUPPE_ID).build()));
        given(testgruppeRepository.findById(GRUPPE_ID))
                .willReturn(Mono.just(Testgruppe.builder().tags("SALESFORCE").build()));

        StepVerifier.create(dollyBestillingService.opprettDollyPersonLeggTil(progress, new RsDollyUtvidetBestilling()))
                .assertNext(person -> {
                    assertThat(person.isLeggTil()).isTrue();
                    assertThat(person.isOpprett()).isFalse();
                    assertThat(person.getIdent()).isEqualTo(IDENT);
                    assertThat(person.getMaster()).isEqualTo(PDL);
                    assertThat(person.getTags()).contains(Tags.SALESFORCE, Tags.DOLLY);
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnOriginalProgressAfterLeggIdentTilGruppe() {

        var progress = BestillingProgress.builder()
                .bestillingId(BESTILLING_ID)
                .ident(IDENT)
                .master(PDL)
                .build();
        var bestilling = Bestilling.builder()
                .id(BESTILLING_ID)
                .gruppeId(GRUPPE_ID)
                .build();

        given(bestillingRepository.findById(BESTILLING_ID)).willReturn(Mono.just(bestilling));
        given(identService.saveIdentTilGruppe(IDENT, GRUPPE_ID, PDL, BESKRIVELSE))
                .willReturn(Mono.just(Testident.builder().ident(IDENT).gruppeId(GRUPPE_ID).master(PDL).build()));

        StepVerifier.create(dollyBestillingService.leggIdentTilGruppe(progress, BESKRIVELSE))
                .expectNext(progress)
                .verifyComplete();
    }

    @Test
    void shouldUseIdentFromProgressWhenSavingTilGruppe() {

        var progress = BestillingProgress.builder()
                .bestillingId(BESTILLING_ID)
                .ident(IDENT)
                .master(PDL)
                .build();
        var bestilling = Bestilling.builder()
                .id(BESTILLING_ID)
                .gruppeId(GRUPPE_ID)
                .build();

        given(bestillingRepository.findById(BESTILLING_ID)).willReturn(Mono.just(bestilling));
        given(identService.saveIdentTilGruppe(anyString(), any(), any(), anyString()))
                .willReturn(Mono.just(Testident.builder().ident(IDENT).build()));

        StepVerifier.create(dollyBestillingService.leggIdentTilGruppe(progress, BESKRIVELSE))
                .expectNextCount(1)
                .verifyComplete();

        verify(identService).saveIdentTilGruppe(IDENT, GRUPPE_ID, PDL, BESKRIVELSE);
    }

    @Test
    void shouldStillReturnProgressWhenBestillingNotFound() {

        var progress = BestillingProgress.builder()
                .bestillingId(BESTILLING_ID)
                .ident(IDENT)
                .master(PDL)
                .build();

        given(bestillingRepository.findById(BESTILLING_ID)).willReturn(Mono.empty());

        // Mono#thenReturn emits the given value regardless of whether the upstream
        // (and therefore the flatMap/identService call) produced anything.
        StepVerifier.create(dollyBestillingService.leggIdentTilGruppe(progress, BESKRIVELSE))
                .expectNext(progress)
                .verifyComplete();

        verifyNoInteractions(identService);
    }

    @Test
    void shouldPassMissingIdentThroughToIdentServiceWhenIdentIsMissing() {

        var progress = BestillingProgress.builder()
                .bestillingId(BESTILLING_ID)
                .ident(null)
                .master(PDL)
                .build();
        var bestilling = Bestilling.builder()
                .id(BESTILLING_ID)
                .gruppeId(GRUPPE_ID)
                .build();

        given(bestillingRepository.findById(BESTILLING_ID)).willReturn(Mono.just(bestilling));
        given(identService.saveIdentTilGruppe(null, GRUPPE_ID, PDL, BESKRIVELSE))
                .willReturn(Mono.just(Testident.builder().ident(null).gruppeId(GRUPPE_ID).master(PDL).build()));

        // leggIdentTilGruppe has no guard for a missing ident on progress; it is passed
        // through to identService as-is (null in this case).
        StepVerifier.create(dollyBestillingService.leggIdentTilGruppe(progress, BESKRIVELSE))
                .expectNext(progress)
                .verifyComplete();

        verify(identService).saveIdentTilGruppe(null, GRUPPE_ID, PDL, BESKRIVELSE);
    }

    @Test
    void shouldRememberIdentityBeforeUpdatingPersonInPdlForvalter() {

        var person = PersonDTO.builder().ident(IDENT).build();
        var request = new RsDollyUtvidetBestilling();
        given(mapperFacade.map(any(), eq(BestillingRequestDTO.class), any()))
                .willReturn(BestillingRequestDTO.builder().person(person).build());
        var progress = BestillingProgress.builder().ident(IDENT).build();
        given(pdlDataConsumer.oppdaterPdl(eq(IDENT), any(PersonUpdateRequestDTO.class)))
                .willReturn(Mono.just(PdlResponse.builder().ident(IDENT).status(HttpStatus.OK).build()));
        stubPersister();

        StepVerifier.create(dollyBestillingService.oppdaterPerson(request, progress, IDENT))
                .assertNext(updatedProgress -> {
                    assertThat(updatedProgress).isSameAs(progress);
                    assertThat(updatedProgress.getOppdatertIdent()).isEqualTo(IDENT);
                    assertThat(updatedProgress.getPdlForvalterStatus()).isEqualTo("OK");
                })
                .verifyComplete();

        var requestCaptor = ArgumentCaptor.forClass(PersonUpdateRequestDTO.class);
        verify(pdlDataConsumer).oppdaterPdl(eq(IDENT), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getPerson()).isSameAs(person);
    }

    @Test
    void shouldLeaveProgressUnchangedWhenPdlUpdateHasNoPerson() {

        var progress = BestillingProgress.builder().ident(IDENT).build();
        var request = new RsDollyUtvidetBestilling();
        given(mapperFacade.map(any(), eq(BestillingRequestDTO.class), any()))
                .willReturn(BestillingRequestDTO.builder().build());

        StepVerifier.create(dollyBestillingService.oppdaterPerson(request, progress, IDENT))
                .assertNext(updatedProgress -> assertThat(updatedProgress).isSameAs(progress))
                .verifyComplete();

        assertThat(progress.getOppdatertIdent()).isNull();
        verifyNoInteractions(pdlDataConsumer, transactionHelperService);
    }

    @Test
    void shouldPrepareMissingPdlDataWithoutRequestingNewPersonForExistingIdent() {

        var progress = BestillingProgress.builder().ident(IDENT).build();
        var request = new RsDollyUtvidetBestilling();
        given(mapperFacade.map(any(), eq(BestillingRequestDTO.class), any()))
                .willReturn(BestillingRequestDTO.builder().build());

        StepVerifier.create(dollyBestillingService.oppdaterPerson(request, progress, IDENT))
                .assertNext(updatedProgress -> assertThat(updatedProgress).isSameAs(progress))
                .verifyComplete();

        assertThat(progress.getOppdatertIdent()).isNull();
        assertThat(request.getPdldata()).isNotNull();
        assertThat(request.getPdldata().getOpprettNyPerson()).isNull();
        verifyNoInteractions(pdlDataConsumer, transactionHelperService);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"OK", "", " "})
    void shouldSendPdlOrderWhenStatusAllowsItAndPreserveIdentityBeingUpdated(String status) {

        var progress = BestillingProgress.builder()
                .ident(IDENT)
                .oppdatertIdent("98765432109")
                .pdlForvalterStatus(status)
                .build();
        given(pdlDataConsumer.sendOrdre(IDENT, false))
                .willReturn(Mono.just(PdlResponse.builder()
                        .status(HttpStatus.OK).jsonNode("{\"status\":\"OK\"}").build()));
        stubPersister();

        StepVerifier.create(dollyBestillingService.sendOrdrePerson(progress))
                .assertNext(updatedProgress -> {
                    assertThat(updatedProgress.getPdlOrdreStatus()).isEqualTo("{\"status\":\"OK\"}");
                    assertThat(updatedProgress.getOppdatertIdent()).isEqualTo("98765432109");
                })
                .verifyComplete();

        verify(pdlDataConsumer).sendOrdre(IDENT, false);
    }

    @Test
    void shouldSavePdlOrderErrorStatusWithoutClearingCurrentIdentity() {

        var progress = BestillingProgress.builder()
                .ident(IDENT)
                .oppdatertIdent("98765432109")
                .pdlForvalterStatus("OK")
                .build();
        given(pdlDataConsumer.sendOrdre(IDENT, false))
                .willReturn(Mono.just(PdlResponse.builder()
                        .status(HttpStatus.BAD_REQUEST).feilmelding("Invalid identity").build()));
        given(errorStatusDecoder.getErrorText(HttpStatus.BAD_REQUEST, "Invalid identity"))
                .willReturn("Feil= Invalid identity");
        stubPersister();

        StepVerifier.create(dollyBestillingService.sendOrdrePerson(progress))
                .assertNext(updatedProgress -> {
                    assertThat(updatedProgress.getPdlOrdreStatus()).isEqualTo("Feil= Invalid identity");
                    assertThat(updatedProgress.getIdent()).isEqualTo(IDENT);
                    assertThat(updatedProgress.getOppdatertIdent()).isEqualTo("98765432109");
                })
                .verifyComplete();
    }

    @Test
    void shouldNotSendPdlOrderWhenPersonUpdateFailed() {

        var progress = BestillingProgress.builder()
                .pdlForvalterStatus("Feil= Update failed")
                .build();

        StepVerifier.create(dollyBestillingService.sendOrdrePerson(progress))
                .assertNext(updatedProgress -> assertThat(updatedProgress).isSameAs(progress))
                .verifyComplete();

        verifyNoInteractions(pdlDataConsumer, transactionHelperService);
    }

    @Test
    void shouldPersistUpdatedIdentityAndSwapRelatedRecords() {

        var previousIdent = "98765432109";
        var newIdent = IDENT;
        var progress = BestillingProgress.builder()
                .id(3L)
                .bestillingId(BESTILLING_ID)
                .ident(IDENT)
                .oppdatertIdent(previousIdent)
                .pdlForvalterStatus("OK")
                .build();
        stubPersonGroup();
        given(identService.swapIdent(previousIdent, newIdent)).willReturn(Mono.empty());
        given(bestillingProgressRepository.swapIdent(previousIdent, newIdent)).willReturn(Mono.empty());
        given(bestillingService.swapIdent(previousIdent, newIdent)).willReturn(Mono.empty());

        StepVerifier.create(dollyBestillingService.opprettDollyPersonLeggTil(progress, new RsDollyUtvidetBestilling()))
                .assertNext(person -> assertThat(person.getIdent()).isEqualTo(newIdent))
                .verifyComplete();

        var order = inOrder(identService, bestillingProgressRepository, bestillingService);
        order.verify(identService).swapIdent(previousIdent, newIdent);
        order.verify(bestillingProgressRepository).swapIdent(previousIdent, newIdent);
        order.verify(bestillingService).swapIdent(previousIdent, newIdent);
        verifyNoInteractions(transactionHelperService);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void shouldReturnNewIdentityWithoutSwappingWhenNoPreviousIdentityIsRecorded(String previousIdent) {

        var progress = BestillingProgress.builder()
                .id(3L)
                .bestillingId(BESTILLING_ID)
                .ident(IDENT)
                .oppdatertIdent(previousIdent)
                .build();
        stubPersonGroup();

        StepVerifier.create(dollyBestillingService.opprettDollyPersonLeggTil(progress, new RsDollyUtvidetBestilling()))
                .assertNext(person -> assertThat(person.getIdent()).isEqualTo(IDENT))
                .verifyComplete();

        verifyNoInteractions(transactionHelperService, identService, bestillingProgressRepository, bestillingService);
    }

    @Test
    void shouldSkipIdentitySwapWhenPreviousAndNewIdentityMatch() {

        var progress = BestillingProgress.builder()
                .bestillingId(BESTILLING_ID)
                .ident(IDENT)
                .oppdatertIdent(IDENT)
                .build();
        stubPersonGroup();

        StepVerifier.create(dollyBestillingService.opprettDollyPersonLeggTil(progress, new RsDollyUtvidetBestilling()))
                .assertNext(person -> assertThat(person.getIdent()).isEqualTo(IDENT))
                .verifyComplete();

        verifyNoInteractions(transactionHelperService, identService, bestillingProgressRepository, bestillingService);
    }

    @Test
    void shouldMoveHistoryBeforeDeletingTemporaryIdentWhenUsingExistingIdent() {

        var previousIdent = "98765432109";
        var progress = BestillingProgress.builder()
                .bestillingId(BESTILLING_ID).ident(IDENT).oppdatertIdent(previousIdent)
                .pdlForvalterStatus("OK").build();
        var request = RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder().nyident(List.of(
                                IdentRequestDTO.builder().eksisterendeIdent(IDENT).build())).build())
                        .build())
                .build();
        stubPersonGroup();
        given(bestillingProgressRepository.swapIdent(previousIdent, IDENT)).willReturn(Mono.empty());
        given(bestillingService.swapIdent(previousIdent, IDENT)).willReturn(Mono.empty());
        given(identService.deleteIdent(previousIdent)).willReturn(Mono.empty());

        StepVerifier.create(dollyBestillingService.opprettDollyPersonLeggTil(progress, request))
                .assertNext(person -> assertThat(person.getIdent()).isEqualTo(IDENT))
                .verifyComplete();

        var order = inOrder(bestillingProgressRepository, bestillingService, identService);
        order.verify(bestillingProgressRepository).swapIdent(previousIdent, IDENT);
        order.verify(bestillingService).swapIdent(previousIdent, IDENT);
        order.verify(identService).deleteIdent(previousIdent);
        order.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "Feil= Update failed"})
    void shouldNotSwapIdentityWhenPdlUpdateIsNotSuccessful(String status) {

        var progress = BestillingProgress.builder()
                .bestillingId(BESTILLING_ID).ident(IDENT).oppdatertIdent("98765432109")
                .pdlForvalterStatus(status).build();
        var request = RsDollyUtvidetBestilling.builder()
                .pdldata(PdlPersondata.builder()
                        .person(PersonDTO.builder().nyident(List.of(
                                IdentRequestDTO.builder().eksisterendeIdent(IDENT).build())).build())
                        .build())
                .build();
        stubPersonGroup();

        StepVerifier.create(dollyBestillingService.opprettDollyPersonLeggTil(progress, request))
                .assertNext(person -> assertThat(person.getIdent()).isEqualTo(IDENT))
                .verifyComplete();

        StepVerifier.create(dollyBestillingService.opprettDollyPersonLeggTil(progress, new RsDollyUtvidetBestilling()))
                .assertNext(person -> assertThat(person.getIdent()).isEqualTo(IDENT))
                .verifyComplete();

        verifyNoInteractions(identService, bestillingProgressRepository, bestillingService);
    }

    @Test
    void shouldCreatePersonForGjenopprettingWithoutSettingCreationFlags() {

        var progress = BestillingProgress.builder()
                .bestillingId(BESTILLING_ID).ident(IDENT).master(PDL).build();
        stubPersonGroup();

        StepVerifier.create(dollyBestillingService.opprettDollyPersonForGjenopprett(progress))
                .assertNext(person -> {
                    assertThat(person.getIdent()).isEqualTo(IDENT);
                    assertThat(person.getMaster()).isEqualTo(PDL);
                    assertThat(person.isLeggTil()).isFalse();
                    assertThat(person.isOpprett()).isFalse();
                    assertThat(person.getTags()).contains(Tags.DOLLY);
                })
                .verifyComplete();

        verifyNoInteractions(identService, bestillingProgressRepository, bestillingService);
    }

    @Test
    void shouldMarkNewDollyPersonAsOpprettWithoutMarkingLeggTil() {

        var progress = BestillingProgress.builder()
                .bestillingId(BESTILLING_ID).ident(IDENT).master(PDL).build();
        stubPersonGroup();

        StepVerifier.create(dollyBestillingService.opprettDollyPersonNyoppretting(progress, new RsDollyUtvidetBestilling()))
                .assertNext(person -> {
                    assertThat(person.getIdent()).isEqualTo(IDENT);
                    assertThat(person.getMaster()).isEqualTo(PDL);
                    assertThat(person.isOpprett()).isTrue();
                    assertThat(person.isLeggTil()).isFalse();
                })
                .verifyComplete();
    }

    private void stubPersonGroup() {

        given(bestillingRepository.findById(BESTILLING_ID))
                .willReturn(Mono.just(Bestilling.builder().gruppeId(GRUPPE_ID).build()));
        given(testgruppeRepository.findById(GRUPPE_ID))
                .willReturn(Mono.just(Testgruppe.builder().build()));
    }

    private void stubPersister() {
        when(transactionHelperService.persister(any(BestillingProgress.class), any(), anyString()))
                .thenAnswer(invocation -> {
                    BestillingProgress progress = invocation.getArgument(0);
                    BiConsumer<BestillingProgress, String> setter = invocation.getArgument(1);
                    setter.accept(progress, invocation.getArgument(2));
                    return Mono.just(progress);
                });
    }
}
