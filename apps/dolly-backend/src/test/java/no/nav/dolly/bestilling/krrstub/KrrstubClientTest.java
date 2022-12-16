package no.nav.dolly.bestilling.krrstub;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.krrstub.dto.DigitalKontaktdataResponse;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KrrstubClientTest {

    private static final String IDENT = "111111111";
    private static final Long BESTILLING_ID = 1L;

    @Mock
    private KrrstubConsumer krrstubConsumer;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder = new ErrorStatusDecoder(new ObjectMapper());

    @InjectMocks
    private KrrstubClient krrstubClient;

    @Test
    public void gjenopprett_ingendata() {

        krrstubClient.gjenopprett(new RsDollyBestillingRequest(), DollyPerson.builder().hovedperson(IDENT).build(), new BestillingProgress(), false);

        verify(krrstubConsumer, times(0)).createDigitalKontaktdata(any(DigitalKontaktdata.class));
    }

    @Test
    public void gjenopprett_krrdata_ok() {

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
                        .build(), false);

        verify(krrstubConsumer).createDigitalKontaktdata(any(DigitalKontaktdata.class));
    }

//    @Test
//    public void gjenopprett_krrdata_feil() {
//
//        BestillingProgress progress = BestillingProgress.builder()
//                .bestilling(Bestilling.builder().id(BESTILLING_ID).build())
//                .build();
//        when(mapperFacade.map(any(RsDigitalKontaktdata.class), eq(DigitalKontaktdata.class), any(MappingContext.class)))
//                .thenReturn(new DigitalKontaktdata());
//        when(krrstubConsumer.createDigitalKontaktdata(any(DigitalKontaktdata.class))).thenThrow(HttpClientErrorException.class);
//        when(errorStatusDecoder.decodeThrowable(any(RuntimeException.class))).thenReturn("Feil:");
//        when(krrstubConsumer.deleteKontaktdataPerson(anyString())).thenReturn(Mono.just(new DigitalKontaktdataResponse()));
//
//        var request = new RsDollyBestillingRequest();
//        request.setKrrstub(RsDigitalKontaktdata.builder().build());
//        var test = krrstubClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT).build(), progress, false);
//
//        verify(krrstubConsumer).createDigitalKontaktdata(any(DigitalKontaktdata.class));
//
//        assertThat(progress.getKrrstubStatus(), containsString("Feil:"));
//    }
//
//    @Test
//    void gjenopprett_krrdata_feil_med_status_kode_ved_4xx() {
//
//        BestillingProgress progress = BestillingProgress.builder()
//                .bestilling(Bestilling.builder().id(BESTILLING_ID).build())
//                .build();
//        when(mapperFacade.map(any(RsDigitalKontaktdata.class), eq(DigitalKontaktdata.class)))
//                .thenReturn(new DigitalKontaktdata());
//        when(krrstubConsumer.createDigitalKontaktdata(any(DigitalKontaktdata.class))).thenThrow(WebClientResponseException.create(HttpStatus.CONFLICT.value(), "Conflict", null, null, null));
//        when(errorStatusDecoder.decodeThrowable(any(RuntimeException.class))).thenCallRealMethod();
//        when(krrstubConsumer.deleteKontaktdataPerson(anyList())).thenReturn(Mono.just(List.of()));
//
//        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
//        request.setKrrstub(RsDigitalKontaktdata.builder().build());
//        krrstubClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT).build(), progress, false);
//
//        verify(krrstubConsumer).createDigitalKontaktdata(any(DigitalKontaktdata.class));
//        verify(krrStubResponseHandler, times(0)).extractResponse(any());
//
//        assertThat(progress.getKrrstubStatus(), containsString("Feil="));
//        assertThat(progress.getKrrstubStatus(), containsString(HttpStatus.CONFLICT.toString()));
//    }
}
