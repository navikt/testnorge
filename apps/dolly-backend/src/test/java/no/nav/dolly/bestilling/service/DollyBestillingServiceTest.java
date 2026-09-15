package no.nav.dolly.bestilling.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static no.nav.dolly.domain.jpa.Testident.Master.PDL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

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
                List.<ClientRegister>of(),
                mapperFacade,
                jsonMapper,
                openSearchService,
                pdlDataConsumer,
                testgruppeRepository,
                transactionHelperService
        );
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
}
