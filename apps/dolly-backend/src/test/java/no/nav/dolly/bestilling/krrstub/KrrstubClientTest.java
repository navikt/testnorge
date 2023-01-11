package no.nav.dolly.bestilling.krrstub;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.krrstub.dto.DigitalKontaktdataResponse;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KrrstubClientTest {

    private static final String IDENT = "111111111";
    private static final Long BESTILLING_ID = 1L;

    @Mock
    private KrrstubConsumer krrstubConsumer;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @Mock
    private TransactionHelperService transactionHelperService;

    @InjectMocks
    private KrrstubClient krrstubClient;

    @Test
    void gjenopprett_ingendata() {

        krrstubClient.gjenopprett(new RsDollyBestillingRequest(),
                        DollyPerson.builder().hovedperson(IDENT).build(), new BestillingProgress(), false)
                .subscribe(resultat ->
                        verify(krrstubConsumer,
                                times(0)).createDigitalKontaktdata(any(DigitalKontaktdata.class)));
    }

    @Test
    void gjenopprett_krrdata_ok() {

        when(mapperFacade.map(any(RsDigitalKontaktdata.class), eq(DigitalKontaktdata.class), any(MappingContext.class)))
                .thenReturn(new DigitalKontaktdata());

        when(krrstubConsumer.createDigitalKontaktdata(any(DigitalKontaktdata.class))).thenReturn(Mono.just(new DigitalKontaktdataResponse()));
        when(krrstubConsumer.deleteKontaktdataPerson(anyString())).thenReturn(Mono.just(new DigitalKontaktdataResponse()));

        var request = new RsDollyBestillingRequest();
        request.setKrrstub(RsDigitalKontaktdata.builder().build());
        krrstubClient.gjenopprett(request,
                        DollyPerson.builder().hovedperson(IDENT).build(),
                        BestillingProgress.builder()
                                .bestilling(Bestilling.builder().id(BESTILLING_ID).build())
                                .build(), false)
                .subscribe(resultat ->
                        verify(krrstubConsumer).createDigitalKontaktdata(any(DigitalKontaktdata.class)));
    }

    @Test
    void gjenopprett_krrdata_feil() {

        when(mapperFacade.map(any(RsDigitalKontaktdata.class), eq(DigitalKontaktdata.class), any(MappingContext.class)))
                .thenReturn(new DigitalKontaktdata());
        when(krrstubConsumer.createDigitalKontaktdata(any(DigitalKontaktdata.class)))
                .thenReturn(Mono.just(DigitalKontaktdataResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .melding("En feil har oppstått")
                        .build()));
        when(errorStatusDecoder.getErrorText(eq(HttpStatus.BAD_REQUEST), anyString())).thenReturn("Feil:");
        when(krrstubConsumer.deleteKontaktdataPerson(anyString())).thenReturn(Mono.just(new DigitalKontaktdataResponse()));

        var request = new RsDollyBestillingRequest();
        request.setKrrstub(RsDigitalKontaktdata.builder().build());

        StepVerifier.create(krrstubClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT).build(),
                                new BestillingProgress(), false)
                        .map(ClientFuture::get))
                .expectNext(BestillingProgress.builder()
                        .krrstubStatus("Feil:")
                        .build())
                .verifyComplete();

        verify(krrstubConsumer).createDigitalKontaktdata(any(DigitalKontaktdata.class));
    }
}
