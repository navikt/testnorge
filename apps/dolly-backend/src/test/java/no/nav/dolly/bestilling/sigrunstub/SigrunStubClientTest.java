package no.nav.dolly.bestilling.sigrunstub;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubLignetInntektRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubPensjonsgivendeInntektRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.sigrunstub.RsLignetInntekt;
import no.nav.dolly.domain.resultset.sigrunstub.RsPensjonsgivendeForFolketrygden;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SigrunStubClientTest {

    private static final String IDENT = "11111111";

    @Mock
    private SigrunStubConsumer sigrunStubConsumer;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private TransactionHelperService transactionHelperService;

    @Captor
    ArgumentCaptor<String> statusCaptor;

    @InjectMocks
    private SigrunStubClient sigrunStubClient;

    void setup() {
        statusCaptor = ArgumentCaptor.forClass(String.class);
    }

    @Test
    void gjenopprett_ingendata() {

        BestillingProgress progress = new BestillingProgress();
        sigrunStubClient.gjenopprett(new RsDollyBestillingRequest(), DollyPerson.builder().ident(IDENT).build(),
                new BestillingProgress(), false);

        assertThat(progress.getSigrunstubStatus(), is(nullValue()));
    }

    @Test
    void gjenopprett_sigrunstubLignetInntekt_feiler() {

        var progress = new BestillingProgress();
        when(sigrunStubConsumer.updateLignetInntekt(anyList())).thenReturn(Mono.just(SigrunstubResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .melding("Feil ...")
                .build()));
        when(errorStatusDecoder.getErrorText(eq(HttpStatus.BAD_REQUEST), anyString())).thenReturn("Feil:");

        var request = new RsDollyBestillingRequest();
        request.setSigrunstub(singletonList(new RsLignetInntekt()));

        StepVerifier.create(sigrunStubClient.gjenopprett(request,
                                DollyPerson.builder().ident(IDENT).build(), progress, false))
                .assertNext(status -> {
                    verify(transactionHelperService, times(1))
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getValue(), Matchers.is(equalTo("SIGRUN_LIGNET:Feil=")));
                })
                .verifyComplete();
    }

    @Test
    void gjenopprett_sigrunstubPensjonsgivendeInntekt_feiler() {

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

        var request = new RsDollyBestillingRequest();
        request.setSigrunstubPensjonsgivende(List.of(new RsPensjonsgivendeForFolketrygden()));

        StepVerifier.create(sigrunStubClient.gjenopprett(request,
                                DollyPerson.builder().ident(IDENT).build(), progress, false))
                .assertNext(status -> {
                    verify(transactionHelperService, times(1))
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getValue(), Matchers.is(equalTo("SIGRUN_PENSJONSGIVENDE:Feil= " +
                            "Inntektsår= 1978; feilmelding= En feil har oppstått")));
                })
                .verifyComplete();
    }

    @Test
    void gjenopprett_sigrunstubLignetInntekt_ok() {

        var request = new RsDollyBestillingRequest();
        request.setSigrunstub(singletonList(new RsLignetInntekt()));

        when(mapperFacade.mapAsList(anyList(), eq(SigrunstubLignetInntektRequest.class), any(MappingContext.class)))
                .thenReturn(List.of(new SigrunstubLignetInntektRequest()));

        when(sigrunStubConsumer.updateLignetInntekt(anyList())).thenReturn(Mono.just(SigrunstubResponse.builder()
                .opprettelseTilbakemeldingsListe(List.of(SigrunstubResponse.OpprettelseTilbakemelding.builder()
                        .status(200)
                        .build()))
                .build()));

        StepVerifier.create(sigrunStubClient.gjenopprett(request, DollyPerson.builder().ident(IDENT).build(),
                                new BestillingProgress(), true))
                .assertNext(status -> {
                    verify(transactionHelperService, times(1))
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getValue(), Matchers.is(equalTo("SIGRUN_LIGNET:OK")));
                })
                .verifyComplete();

        verify(sigrunStubConsumer).updateLignetInntekt(anyList());
    }

    @Test
    void gjenopprett_sigrunstubPensjonsgivendeInntekt_ok() {

        var request = new RsDollyBestillingRequest();
        request.setSigrunstubPensjonsgivende(singletonList(new RsPensjonsgivendeForFolketrygden()));

        when(mapperFacade.mapAsList(anyList(), eq(SigrunstubPensjonsgivendeInntektRequest.class), any(MappingContext.class)))
                .thenReturn(List.of(new SigrunstubPensjonsgivendeInntektRequest()));

        when(sigrunStubConsumer.updatePensjonsgivendeInntekt(anyList())).thenReturn(Mono.just(SigrunstubResponse.builder()
                .opprettelseTilbakemeldingsListe(List.of(SigrunstubResponse.OpprettelseTilbakemelding.builder()
                        .status(200)
                        .build()))
                .build()));

        StepVerifier.create(sigrunStubClient.gjenopprett(request, DollyPerson.builder().ident(IDENT).build(),
                                new BestillingProgress(), true))
                .assertNext(status -> {
                    verify(transactionHelperService, times(1))
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getValue(), Matchers.is(equalTo("SIGRUN_PENSJONSGIVENDE:OK")));
                })
                .verifyComplete();

        verify(sigrunStubConsumer).updatePensjonsgivendeInntekt(anyList());
    }
}