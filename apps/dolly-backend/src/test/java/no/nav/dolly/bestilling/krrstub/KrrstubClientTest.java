package no.nav.dolly.bestilling.krrstub;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.ResponseHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
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
    private ResponseHandler krrStubResponseHandler;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder = new ErrorStatusDecoder(new ObjectMapper());

    @InjectMocks
    private KrrstubClient krrstubClient;

    @Test
    void gjenopprett_ingendata() {
        krrstubClient.gjenopprett(null, new RsDollyBestillingRequest(), DollyPerson.builder().hovedperson(IDENT).build(), new BestillingProgress(), false);

        verify(krrstubConsumer, times(0)).createDigitalKontaktdata(any(DigitalKontaktdata.class));
    }

    @Test
    void gjenopprett_krrdata_ok() {

        when(mapperFacade.map(any(RsDigitalKontaktdata.class), eq(DigitalKontaktdata.class)))
                .thenReturn(new DigitalKontaktdata());

        when(krrstubConsumer.createDigitalKontaktdata(any(DigitalKontaktdata.class))).thenReturn(ResponseEntity.ok(""));
        when(krrstubConsumer.deleteKontaktdataPerson(anyList())).thenReturn(Mono.just(List.of()));

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setKrrstub(RsDigitalKontaktdata.builder().build());
        krrstubClient.gjenopprett(
                null,
                request,
                DollyPerson
                        .builder()
                        .hovedperson(IDENT)
                        .build(),
                BestillingProgress
                        .builder()
                        .bestilling(
                                Bestilling
                                        .builder()
                                        .id(BESTILLING_ID)
                                        .build())
                        .build(),
                false);

        verify(krrstubConsumer).createDigitalKontaktdata(any(DigitalKontaktdata.class));
        verify(krrStubResponseHandler).extractResponse(any());
    }

    @Test
    void gjenopprett_krrdata_feil() {

        BestillingProgress progress = BestillingProgress.builder()
                .bestilling(Bestilling.builder().id(BESTILLING_ID).build())
                .build();
        when(mapperFacade.map(any(RsDigitalKontaktdata.class), eq(DigitalKontaktdata.class)))
                .thenReturn(new DigitalKontaktdata());
        when(krrstubConsumer.createDigitalKontaktdata(any(DigitalKontaktdata.class))).thenThrow(HttpClientErrorException.class);
        when(errorStatusDecoder.decodeThrowable(any(RuntimeException.class))).thenReturn("Feil:");
        when(krrstubConsumer.deleteKontaktdataPerson(anyList())).thenReturn(Mono.just(List.of()));

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setKrrstub(RsDigitalKontaktdata.builder().build());
        krrstubClient.gjenopprett(
                null,
                request,
                DollyPerson
                        .builder()
                        .hovedperson(IDENT)
                        .build(),
                progress,
                false);

        verify(krrstubConsumer).createDigitalKontaktdata(any(DigitalKontaktdata.class));
        verify(krrStubResponseHandler, times(0)).extractResponse(any());

        assertThat(progress.getKrrstubStatus(), containsString("Feil:"));
    }

    @Test
    void gjenopprett_krrdata_feil_med_status_kode_ved_4xx() {

        BestillingProgress progress = BestillingProgress.builder()
                .bestilling(Bestilling.builder().id(BESTILLING_ID).build())
                .build();
        when(mapperFacade.map(any(RsDigitalKontaktdata.class), eq(DigitalKontaktdata.class)))
                .thenReturn(new DigitalKontaktdata());
        when(krrstubConsumer.createDigitalKontaktdata(any(DigitalKontaktdata.class))).thenThrow(WebClientResponseException.create(HttpStatus.CONFLICT.value(), "Conflict", null, null, null));
        when(errorStatusDecoder.decodeThrowable(any(RuntimeException.class))).thenCallRealMethod();
        when(krrstubConsumer.deleteKontaktdataPerson(anyList())).thenReturn(Mono.just(List.of()));

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setKrrstub(RsDigitalKontaktdata.builder().build());
        krrstubClient.gjenopprett(
                null,
                request,
                DollyPerson
                        .builder()
                        .hovedperson(IDENT)
                        .build(),
                progress,
                false);

        verify(krrstubConsumer).createDigitalKontaktdata(any(DigitalKontaktdata.class));
        verify(krrStubResponseHandler, times(0)).extractResponse(any());

        assertThat(progress.getKrrstubStatus(), containsString("Feil="));
        assertThat(progress.getKrrstubStatus(), containsString(HttpStatus.CONFLICT.toString()));
    }
}
