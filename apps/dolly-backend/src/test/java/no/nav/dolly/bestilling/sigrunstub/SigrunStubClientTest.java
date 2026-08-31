package no.nav.dolly.bestilling.sigrunstub;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubPensjonsgivendeInntektRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.sigrunstub.RsPensjonsgivendeForFolketrygden;
import no.nav.dolly.service.TransactionHelperService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
    private MapperFacade mapperFacade;

    @Mock
    private TransactionHelperService transactionHelperService;

    @Captor
    ArgumentCaptor<String> statusCaptor;

    @InjectMocks
    private SigrunStubClient sigrunStubClient;

    @Test
    void gjenopprett_ingendata() {

        when(transactionHelperService.persister(any(), any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(sigrunStubClient.gjenopprett(new RsDollyBestillingRequest(), DollyPerson.builder().ident(IDENT).build(),
                new BestillingProgress(), false))
                .expectNextCount(0)
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
        when(transactionHelperService.persister(any(), any(), any())).thenReturn(Mono.just(progress));

        var request = new RsDollyBestillingRequest();
        request.setSigrunstubPensjonsgivende(List.of(new RsPensjonsgivendeForFolketrygden()));

        StepVerifier.create(sigrunStubClient.gjenopprett(request,
                                DollyPerson.builder().ident(IDENT).build(), progress, false))
                .assertNext(_ -> {
                    verify(transactionHelperService, times(1))
                            .persister(any(BestillingProgress.class), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getValue(), Matchers.is(equalTo("SIGRUN_PENSJONSGIVENDE:Feil= " +
                            "Inntektsår= 1978; feilmelding= En feil har oppstått")));
                })
                .verifyComplete();
    }

    @Test
    void gjenopprett_sigrunstubPensjonsgivendeInntekt_ok() {

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
                    assertThat(statusCaptor.getValue(), Matchers.is(equalTo("SIGRUN_PENSJONSGIVENDE:OK")));
                })
                .verifyComplete();

        verify(sigrunStubConsumer).updatePensjonsgivendeInntekt(anyList());
    }
}