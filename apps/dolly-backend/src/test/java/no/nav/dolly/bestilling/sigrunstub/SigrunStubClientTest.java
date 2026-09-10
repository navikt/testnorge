package no.nav.dolly.bestilling.sigrunstub;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubPensjonsgivendeInntektRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubSummertskattegrunnlagRequest;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.sigrunstub.RsPensjonsgivendeForFolketrygden;
import no.nav.dolly.domain.resultset.sigrunstub.RsSummertSkattegrunnlag;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransactionHelperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SigrunStubClientTest {

    private static final String IDENT = "11111111";
    private static final String TESTNORGE_IDENT = "11811111111";

    @Mock
    private SigrunStubConsumer sigrunStubConsumer;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @Mock
    private TransactionHelperService transactionHelperService;

    @Captor
    ArgumentCaptor<String> statusCaptor;

    @InjectMocks
    private SigrunStubClient sigrunStubClient;

    @Test
    void shouldCompleteWithoutResultWhenNoDataExists() {

        when(transactionHelperService.persister(any(), any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(sigrunStubClient.gjenopprett(new RsDollyBestillingRequest(), DollyPerson.builder().ident(IDENT).build(),
                new BestillingProgress(), false))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void shouldPersistErrorWhenPensjonsgivendeInntektFails() {

        var progress = new BestillingProgress();
        when(sigrunStubConsumer.updatePensjonsgivendeInntekt(anyList()))
                .thenReturn(Mono.just(SigrunstubResponse.builder()
                        .opprettelseTilbakemeldingsListe(List.of(SigrunstubResponse.OpprettelseTilbakemelding.builder()
                                .inntektsaar("1978")
                                .message("En feil har oppstått")
                                .status(400)
                                .build()))
                        .build()));

        when(mapperFacade.mapAsList(anyList(), eq(SigrunstubPensjonsgivendeInntektRequest.class), any(MappingContext.class)))
                .thenReturn(List.of(new SigrunstubPensjonsgivendeInntektRequest()));
        when(transactionHelperService.persister(any(), any(), any())).thenReturn(Mono.just(progress));

        var request = new RsDollyBestillingRequest();
        request.setSigrunstubPensjonsgivende(List.of(new RsPensjonsgivendeForFolketrygden()));

        StepVerifier.create(sigrunStubClient.gjenopprett(request,
                                DollyPerson.builder().ident(IDENT).build(), progress, false))
                .assertNext(_ -> {
                    verify(transactionHelperService, times(1))
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getValue()).isEqualTo("SIGRUN_PENSJONSGIVENDE:Feil= " +
                            "Inntektsår= 1978; feilmelding= En feil har oppstått");
                })
                .verifyComplete();
    }

    @Test
    void shouldUpdatePensjonsgivendeInntekt() {

        var request = new RsDollyBestillingRequest();
        var progress = new BestillingProgress();
        request.setSigrunstubPensjonsgivende(singletonList(new RsPensjonsgivendeForFolketrygden()));

        when(mapperFacade.mapAsList(anyList(), eq(SigrunstubPensjonsgivendeInntektRequest.class), any(MappingContext.class)))
                .thenReturn(List.of(new SigrunstubPensjonsgivendeInntektRequest()));

        when(sigrunStubConsumer.updatePensjonsgivendeInntekt(anyList())).thenReturn(Mono.just(SigrunstubResponse.builder()
                .opprettelseTilbakemeldingsListe(List.of(SigrunstubResponse.OpprettelseTilbakemelding.builder()
                        .status(200)
                        .build()))
                .build()));
        when(transactionHelperService.persister(any(), any(), any())).thenReturn(Mono.just(progress));

        StepVerifier.create(sigrunStubClient.gjenopprett(request, DollyPerson.builder().ident(IDENT).build(),
                                new BestillingProgress(), true))
                .assertNext(_ -> {
                    verify(transactionHelperService, times(1))
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getValue()).isEqualTo("SIGRUN_PENSJONSGIVENDE:OK");
                })
                .verifyComplete();

        verify(sigrunStubConsumer).updatePensjonsgivendeInntekt(anyList());
    }

    @Test
    void shouldCreateSummertSkattegrunnlag() {

        var request = new RsDollyBestillingRequest();
        var progress = new BestillingProgress();
        request.setSigrunstubSummertSkattegrunnlag(List.of(RsSummertSkattegrunnlag.builder()
                .inntektsaar("2025")
                .build()));

        when(mapperFacade.mapAsList(anyList(),
                eq(SigrunstubSummertskattegrunnlagRequest.Summertskattegrunnlag.class),
                any(MappingContext.class)))
                .thenReturn(List.of(SigrunstubSummertskattegrunnlagRequest.Summertskattegrunnlag.builder()
                        .personidentifikator(IDENT)
                        .build()));
        when(sigrunStubConsumer.createSigrunstubSummertSkattegrunnlag(any()))
                .thenReturn(Mono.just(okResponse(IDENT)));
        when(transactionHelperService.persister(any(), any(), any()))
                .thenReturn(Mono.just(progress));

        StepVerifier.create(sigrunStubClient.gjenopprett(
                        request, DollyPerson.builder().ident(IDENT).build(), progress, true))
                .assertNext(_ -> {
                    verify(transactionHelperService).persister(any(), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getValue()).isEqualTo("SIGRUN_SUMMERT:OK");
                })
                .verifyComplete();

        verify(sigrunStubConsumer).createSigrunstubSummertSkattegrunnlag(any());
    }

    @Test
    void shouldImportBothIncomeTypesForTestnorgeIdent() {

        var progress = new BestillingProgress();
        when(sigrunStubConsumer.importCheckPensjonsgivendeInntektForFolketrygden(TESTNORGE_IDENT))
                .thenReturn(Mono.just(okResponse(TESTNORGE_IDENT)));
        when(sigrunStubConsumer.importPensjonsgivendeInntektForFolketrygden(TESTNORGE_IDENT))
                .thenReturn(Mono.just(okResponse(TESTNORGE_IDENT)));
        when(sigrunStubConsumer.importCheckSummertSkattegrunnlag(TESTNORGE_IDENT))
                .thenReturn(Mono.just(okResponse(TESTNORGE_IDENT)));
        when(sigrunStubConsumer.importSummertSkattegrunnlag(TESTNORGE_IDENT))
                .thenReturn(Mono.just(okResponse(TESTNORGE_IDENT)));
        when(transactionHelperService.persister(any(), any(), any()))
                .thenReturn(Mono.just(progress));

        StepVerifier.create(sigrunStubClient.gjenopprett(
                        new RsDollyBestillingRequest(),
                        DollyPerson.builder().ident(TESTNORGE_IDENT).build(),
                        progress,
                        true))
                .assertNext(_ -> {
                    verify(transactionHelperService).persister(any(), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getValue())
                            .contains("SIGRUN_SUMMERT:OK")
                            .contains("SIGRUN_PENSJONSGIVENDE:OK");
                })
                .verifyComplete();

        verify(sigrunStubConsumer).importPensjonsgivendeInntektForFolketrygden(TESTNORGE_IDENT);
        verify(sigrunStubConsumer).importSummertSkattegrunnlag(TESTNORGE_IDENT);
    }

    @Test
    void shouldNotImportWhenImportChecksFail() {

        var progress = new BestillingProgress();
        when(sigrunStubConsumer.importCheckPensjonsgivendeInntektForFolketrygden(TESTNORGE_IDENT))
                .thenReturn(Mono.just(SigrunstubResponse.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .build()));
        when(sigrunStubConsumer.importCheckSummertSkattegrunnlag(TESTNORGE_IDENT))
                .thenReturn(Mono.just(SigrunstubResponse.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .build()));
        when(transactionHelperService.persister(any(), any(), any()))
                .thenReturn(Mono.just(progress));

        StepVerifier.create(sigrunStubClient.gjenopprett(
                        new RsDollyBestillingRequest(),
                        DollyPerson.builder().ident(TESTNORGE_IDENT).build(),
                        progress,
                        true))
                .expectNext(progress)
                .verifyComplete();

        verify(sigrunStubConsumer, never())
                .importPensjonsgivendeInntektForFolketrygden(TESTNORGE_IDENT);
        verify(sigrunStubConsumer, never())
                .importSummertSkattegrunnlag(TESTNORGE_IDENT);
    }

    @Test
    void shouldDeleteBothIncomeTypesOnRelease() {

        var identer = List.of(IDENT);
        when(sigrunStubConsumer.deletePensjonsgivendeInntekt(identer))
                .thenReturn(Flux.just(okResponse(IDENT)));
        when(sigrunStubConsumer.deleteSummertSkattegrunnlag(identer))
                .thenReturn(Flux.just(okResponse(IDENT)));

        sigrunStubClient.release(identer);

        verify(sigrunStubConsumer).deletePensjonsgivendeInntekt(identer);
        verify(sigrunStubConsumer).deleteSummertSkattegrunnlag(identer);
    }

    private static SigrunstubResponse okResponse(String ident) {

        return SigrunstubResponse.builder()
                .ident(ident)
                .status(HttpStatus.OK)
                .build();
    }
}