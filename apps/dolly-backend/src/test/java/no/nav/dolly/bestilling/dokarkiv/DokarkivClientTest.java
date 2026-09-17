package no.nav.dolly.bestilling.dokarkiv;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.consumer.dokumentarkiv.SafConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DokumentService;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.dolly.service.TransaksjonMappingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DokarkivClientTest {

    @Mock
    private DokarkivConsumer dokarkivConsumer;
    @Mock
    private DokumentService dokumentService;
    @Mock
    private ErrorStatusDecoder errorStatusDecoder;
    @Mock
    private MapperFacade mapperFacade;
    @Mock
    private JsonMapper jsonMapper;
    @Mock
    private PersonServiceConsumer personServiceConsumer;
    @Mock
    private SafConsumer safConsumer;
    @Mock
    private TransactionHelperService transactionHelperService;
    @Mock
    private TransaksjonMappingService transaksjonMappingService;
    @InjectMocks
    private DokarkivClient dokarkivClient;

    private final RsDollyUtvidetBestilling bestilling = new RsDollyUtvidetBestilling();
    private final DollyPerson person = DollyPerson.builder().ident("syntetisk-person").build();
    private final BestillingProgress progress = new BestillingProgress();
    private final DokarkivRequest request = new DokarkivRequest();

    @BeforeEach
    void setUp() {
        bestilling.setId(1L);
        bestilling.setEnvironments(Set.of("q2"));
        bestilling.setDokarkiv(List.of(new RsDokarkiv()));

        when(dokarkivConsumer.getEnvironments()).thenReturn(Mono.just(List.of("q2")));
        when(personServiceConsumer.getPdlPersoner(any())).thenReturn(Flux.just(PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(PdlPersonBolk.PersonBolk.builder()
                                .person(new PdlPerson.Person())
                                .build()))
                        .build())
                .build()));
        when(dokumentService.getDokumenterByBestilling(1L)).thenReturn(Flux.empty());
        when(mapperFacade.map(any(), eq(DokarkivRequest.class), any())).thenReturn(request);
        when(transactionHelperService.persister(any(), any(), any(), anyString())).thenAnswer(invocation -> {
            BiConsumer<BestillingProgress, String> setter = invocation.getArgument(2);
            setter.accept(progress, invocation.getArgument(3));
            return Mono.just(progress);
        });
    }

    @Test
    void shouldUploadFortyMiBDocumentAndWaitBeyondThirtySeconds() {
        var content = Base64.getEncoder().encodeToString(new byte[40 * 1024 * 1024]);
        var variant = DokarkivRequest.DokumentVariant.builder().fysiskDokument(content).build();
        request.setDokumenter(List.of(DokarkivRequest.Dokument.builder()
                .dokumentvarianter(List.of(variant))
                .build()));

        when(dokarkivConsumer.initProxyUpload()).thenReturn(Mono.just("upload-reference"));
        when(dokarkivConsumer.appendProxyChunk(eq("upload-reference"), anyString()))
                .thenAnswer(_ -> Mono.delay(Duration.ofSeconds(1)).then());
        when(dokarkivConsumer.postDokarkiv("q2", request)).thenAnswer(_ ->
                Mono.delay(Duration.ofSeconds(35)).thenReturn(DokarkivResponse.builder()
                        .miljoe("q2")
                        .journalpostId("journalpost")
                        .dokumenter(List.of(DokarkivResponse.DokumentInfo.builder()
                                .dokumentInfoId("dokument")
                                .build()))
                        .build()));
        when(jsonMapper.writeValueAsString(any())).thenReturn("[]");
        when(transaksjonMappingService.save(any())).thenReturn(Mono.empty());

        StepVerifier.withVirtualTime(() -> dokarkivClient.gjenopprett(bestilling, person, progress, true))
                .thenAwait(Duration.ofSeconds(148))
                .assertNext(result -> assertThat(result.getDokarkivStatus()).isEqualTo("q2:OK"))
                .verifyComplete();

        var chunks = ArgumentCaptor.forClass(String.class);
        verify(dokarkivConsumer, times(112))
                .appendProxyChunk(eq("upload-reference"), chunks.capture());
        assertThat(chunks.getAllValues()).allSatisfy(chunk -> assertThat(chunk.length()).isLessThanOrEqualTo(500_000));
        assertThat(String.join("", chunks.getAllValues()).equals(content)).isTrue();
        assertThat(variant.getFysiskDokument()).isNull();
        assertThat(variant.getUploadReferanse()).isEqualTo("upload-reference");
        verify(dokarkivConsumer).postDokarkiv("q2", request);
    }

    @Test
    void shouldStopWhenDokarkivOperationExceedsItsTimeBudget() {
        when(dokarkivConsumer.postDokarkiv("q2", request)).thenReturn(Mono.never());

        StepVerifier.withVirtualTime(() -> dokarkivClient.gjenopprett(bestilling, person, progress, true))
                .thenAwait(Duration.ofSeconds(599))
                .expectNoEvent(Duration.ofMillis(999))
                .thenAwait(Duration.ofMillis(1))
                .assertNext(result -> assertThat(result.getDokarkivStatus())
                        .isEqualTo("q2:Mottaker svarer ikke; eller har for lang svartid."))
                .verifyComplete();

        verify(transaksjonMappingService, never()).save(any());
    }
}
