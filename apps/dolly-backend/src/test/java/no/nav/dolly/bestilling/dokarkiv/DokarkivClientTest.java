package no.nav.dolly.bestilling.dokarkiv;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
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
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
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
        when(transactionHelperService.persister(any(), any(), any(), anyString())).thenAnswer(invocation -> {
            BiConsumer<BestillingProgress, String> setter = invocation.getArgument(2);
            setter.accept(progress, invocation.getArgument(3));
            return Mono.just(progress);
        });
    }

    @Test
    void shouldUploadFortyMiBDocumentAndWaitBeyondOneMinute() {
        when(mapperFacade.map(any(), eq(DokarkivRequest.class), any())).thenReturn(request);
        var content = Base64.getEncoder().encodeToString(new byte[40 * 1024 * 1024]);
        var variant = DokarkivRequest.DokumentVariant.builder().fysiskDokument(content).build();
        request.setDokumenter(List.of(DokarkivRequest.Dokument.builder()
                .dokumentvarianter(List.of(variant))
                .build()));

        when(dokarkivConsumer.initProxyUpload()).thenReturn(Mono.just("upload-reference"));
        when(dokarkivConsumer.appendProxyChunk(eq("upload-reference"), anyString()))
                .thenAnswer(_ -> Mono.delay(Duration.ofMillis(100)).then());
        when(dokarkivConsumer.postDokarkiv("q2", request)).thenAnswer(_ ->
                Mono.delay(Duration.ofSeconds(65)).thenReturn(DokarkivResponse.builder()
                        .miljoe("q2")
                        .journalpostId("journalpost")
                        .dokumenter(List.of(DokarkivResponse.DokumentInfo.builder()
                                .dokumentInfoId("dokument")
                                .build()))
                        .build()));
        when(jsonMapper.writeValueAsString(any())).thenReturn("[]");
        when(transaksjonMappingService.save(any())).thenReturn(Mono.empty());

        StepVerifier.withVirtualTime(() -> dokarkivClient.gjenopprett(bestilling, person, progress, true))
                .thenAwait(Duration.ofSeconds(77))
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
    void shouldUploadEightyMiBDocumentToBothEnvironmentsWithSequentialChunks() {
        var encodedLength = 4 * Math.ceilDiv(80 * 1024 * 1024, 3);
        var content = "A".repeat(encodedLength - 1) + "=";
        var receivedLengths = new HashMap<String, Integer>();
        var chunkCounts = new HashMap<String, Integer>();
        var activeUploads = new HashSet<String>();
        var submittedEnvironments = new HashSet<String>();
        bestilling.setEnvironments(Set.of("q1", "q2"));
        when(dokarkivConsumer.getEnvironments()).thenReturn(Mono.just(List.of("q1", "q2")));
        when(mapperFacade.map(any(), eq(DokarkivRequest.class), any())).thenAnswer(_ -> {
            var environmentRequest = new DokarkivRequest();
            environmentRequest.setDokumenter(List.of(DokarkivRequest.Dokument.builder()
                    .dokumentvarianter(List.of(DokarkivRequest.DokumentVariant.builder()
                            .fysiskDokument(content)
                            .build()))
                    .build()));
            return environmentRequest;
        });
        when(dokarkivConsumer.initProxyUpload())
                .thenReturn(Mono.just("upload-1"))
                .thenReturn(Mono.just("upload-2"));
        when(dokarkivConsumer.appendProxyChunk(anyString(), anyString())).thenAnswer(invocation -> {
            String uploadId = invocation.getArgument(0);
            String chunk = invocation.getArgument(1);
            var receivedLength = receivedLengths.getOrDefault(uploadId, 0);
            assertThat(activeUploads.add(uploadId)).isTrue();
            assertThat(chunk.length()).isEqualTo(Math.min(500_000, encodedLength - receivedLength));
            assertThat(content.regionMatches(receivedLength, chunk, 0, chunk.length())).isTrue();
            receivedLengths.put(uploadId, receivedLength + chunk.length());
            chunkCounts.merge(uploadId, 1, Integer::sum);
            clearInvocations(dokarkivConsumer);
            return Mono.delay(Duration.ofMillis(1))
                    .doOnNext(_ -> activeUploads.remove(uploadId))
                    .then();
        });
        when(dokarkivConsumer.postDokarkiv(anyString(), any(DokarkivRequest.class))).thenAnswer(invocation -> {
            String environment = invocation.getArgument(0);
            DokarkivRequest environmentRequest = invocation.getArgument(1);
            var variant = environmentRequest.getDokumenter().getFirst().getDokumentvarianter().getFirst();
            assertThat(variant.getFysiskDokument()).isNull();
            assertThat(receivedLengths.get(variant.getUploadReferanse())).isEqualTo(encodedLength);
            assertThat(activeUploads).doesNotContain(variant.getUploadReferanse());
            submittedEnvironments.add(environment);
            return Mono.just(DokarkivResponse.builder()
                    .miljoe(environment)
                    .journalpostId("journalpost-" + environment)
                    .dokumenter(List.of(DokarkivResponse.DokumentInfo.builder()
                            .dokumentInfoId("dokument")
                            .build()))
                    .build());
        });
        when(jsonMapper.writeValueAsString(any())).thenReturn("[]");
        when(transaksjonMappingService.save(any())).thenReturn(Mono.empty());

        StepVerifier.withVirtualTime(() -> dokarkivClient.gjenopprett(bestilling, person, progress, true))
                .thenAwait(Duration.ofSeconds(1))
                .assertNext(result -> assertThat(result.getDokarkivStatus()).contains("q1:OK", "q2:OK"))
                .verifyComplete();

        assertThat(receivedLengths).hasSize(2).allSatisfy((_, length) -> assertThat(length).isEqualTo(encodedLength));
        assertThat(chunkCounts).hasSize(2).allSatisfy((_, count) -> assertThat(count).isEqualTo(224));
        assertThat(submittedEnvironments).containsExactlyInAnyOrder("q1", "q2");
        assertThat(activeUploads).isEmpty();
    }

    @Test
    void shouldStopAppendingWhenUploadTimesOut() {
        when(mapperFacade.map(any(), eq(DokarkivRequest.class), any())).thenReturn(request);
        var content = "A".repeat(1_000_004);
        var variant = DokarkivRequest.DokumentVariant.builder().fysiskDokument(content).build();
        request.setDokumenter(List.of(DokarkivRequest.Dokument.builder()
                .dokumentvarianter(List.of(variant))
                .build()));
        when(dokarkivConsumer.initProxyUpload()).thenReturn(Mono.just("upload-reference"));
        when(dokarkivConsumer.appendProxyChunk(eq("upload-reference"), anyString())).thenReturn(Mono.never());

        StepVerifier.withVirtualTime(() -> dokarkivClient.gjenopprett(bestilling, person, progress, true))
                .thenAwait(Duration.ofMinutes(2))
                .assertNext(result -> assertThat(result.getDokarkivStatus())
                        .isEqualTo("q2:Mottaker svarer ikke; eller har for lang svartid."))
                .verifyComplete();

        verify(dokarkivConsumer).appendProxyChunk("upload-reference", content.substring(0, 500_000));
        verify(dokarkivConsumer, never()).postDokarkiv(anyString(), any(DokarkivRequest.class));
        assertThat(variant.getUploadReferanse()).isNull();
        assertThat(variant.getFysiskDokument()).isSameAs(content);
    }

    @Test
    void shouldLogUnderlyingCauseTypesWithoutLoggingDocumentContent() {
        when(mapperFacade.map(any(), eq(DokarkivRequest.class), any())).thenReturn(request);
        var error = new IllegalStateException("sensitive-document-content", new OutOfMemoryError("Java heap space"));
        when(dokarkivConsumer.postDokarkiv("q2", request)).thenReturn(Mono.error(error));
        var logger = (Logger) LoggerFactory.getLogger(DokarkivClient.class);
        var originalLevel = logger.getLevel();
        var appender = new ListAppender<ILoggingEvent>();
        appender.start();
        logger.addAppender(appender);
        logger.setLevel(Level.ERROR);

        try {
            StepVerifier.create(dokarkivClient.gjenopprett(bestilling, person, progress, true))
                    .expectNext(progress)
                    .verifyComplete();

            assertThat(appender.list).singleElement().satisfies(event -> {
                assertThat(event.getFormattedMessage())
                        .contains("årsakstyper=[IllegalStateException, OutOfMemoryError]")
                        .doesNotContain("sensitive-document-content", "Java heap space");
                assertThat(event.getThrowableProxy()).isNull();
            });
        } finally {
            logger.detachAppender(appender);
            logger.setLevel(originalLevel);
            appender.stop();
        }
    }

    @Test
    void shouldStopWhenDokarkivOperationExceedsItsTimeBudget() {
        when(mapperFacade.map(any(), eq(DokarkivRequest.class), any())).thenReturn(request);
        when(dokarkivConsumer.postDokarkiv("q2", request)).thenReturn(Mono.never());

        StepVerifier.withVirtualTime(() -> dokarkivClient.gjenopprett(bestilling, person, progress, true))
                .thenAwait(Duration.ofSeconds(119))
                .expectNoEvent(Duration.ofMillis(999))
                .thenAwait(Duration.ofMillis(1))
                .assertNext(result -> assertThat(result.getDokarkivStatus())
                        .isEqualTo("q2:Mottaker svarer ikke; eller har for lang svartid."))
                .verifyComplete();

        verify(transaksjonMappingService, never()).save(any());
    }
}
