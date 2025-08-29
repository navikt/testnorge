package no.nav.dolly.bestilling.instdata;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.bestilling.instdata.domain.InstitusjonsoppholdRespons;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
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
import java.util.Map;

import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstdataClientTest {

    private static final String IDENT = "11111111111";
    private static final String ENVIRONMENT = "q2";

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @Mock
    private InstdataConsumer instdataConsumer;

    @Mock
    private TransactionHelperService transactionHelperService;

    @Captor
    ArgumentCaptor<String> statusCaptor;

    @InjectMocks
    private InstdataClient instdataClient;

    void setup() {
        statusCaptor = ArgumentCaptor.forClass(String.class);
    }

    @Test
    void gjenopprettUtenInstdata_TomRetur() {

        var dollyPerson = DollyPerson.builder().ident(IDENT).build();

        StepVerifier.create(instdataClient.gjenopprett(new RsDollyBestillingRequest(), dollyPerson,
                                new BestillingProgress(), false)
                        .map(ClientFuture::get))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void gjenopprettNaarInstdataIkkeFinnesFraFoer_SkalGiOk() {

        var progress = new BestillingProgress();
        var dollyPerson = DollyPerson.builder().ident(IDENT).build();

        when(instdataConsumer.getMiljoer()).thenReturn(Mono.just(List.of("q2")));
        when(mapperFacade.mapAsList(anyList(), eq(Instdata.class), any(MappingContext.class))).thenReturn(List.of(Instdata.builder()
                .norskident(IDENT)
                .build()));
        when(instdataConsumer.postInstdata(anyList(), anyString())).thenReturn(Flux.just(InstdataResponse.builder()
                .status(HttpStatus.OK)
                .build()));

        var request = new RsDollyBestillingRequest();
        request.setInstdata(List.of(RsInstdata.builder().build()));
        request.setEnvironments(singleton("q2"));

        StepVerifier.create(instdataClient.gjenopprett(request, dollyPerson, progress, true)
                        .map(ClientFuture::get))
                .assertNext(status -> {
                    verify(transactionHelperService)
                            .persister(any(BestillingProgress.class), any(), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getValue(), is(equalTo("q2:opphold=1$OK")));
                })
                .verifyComplete();
    }

    @Test
    void gjenopprettNaarInstdataFinnesFraFoer_SkalGiOk() {

        var progress = new BestillingProgress();
        var dollyPerson = DollyPerson.builder().ident(IDENT).build();

        when(instdataConsumer.getMiljoer()).thenReturn(Mono.just(List.of("q2")));
        when(mapperFacade.mapAsList(anyList(), eq(Instdata.class), any(MappingContext.class))).thenReturn(List.of(Instdata.builder()
                .norskident(IDENT)
                .build()));
        when(instdataConsumer.getInstdata(IDENT, ENVIRONMENT)).thenReturn(Mono.just(InstitusjonsoppholdRespons.builder()
                .institusjonsopphold(Map.of("q2", List.of(new Instdata())))
                .build()));
        when(instdataConsumer.postInstdata(anyList(), anyString())).thenReturn(Flux.just(InstdataResponse.builder()
                .status(HttpStatus.OK)
                .build()));

        var request = new RsDollyBestillingRequest();
        request.setInstdata(List.of(RsInstdata.builder().build()));
        request.setEnvironments(singleton("q2"));

        StepVerifier.create(instdataClient.gjenopprett(request, dollyPerson, progress, false)
                        .map(ClientFuture::get))
                .assertNext(status -> {
                    verify(transactionHelperService)
                            .persister(any(BestillingProgress.class), any(), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getValue(), is(equalTo("q2:opphold=1$OK")));
                })
                .verifyComplete();
    }
}