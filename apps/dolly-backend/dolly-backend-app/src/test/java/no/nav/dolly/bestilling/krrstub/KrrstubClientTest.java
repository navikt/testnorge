package no.nav.dolly.bestilling.krrstub;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KrrstubClientTest {

    private static final String IDENT = "111111111";
    private static final Long BESTILLING_ID = 1L;

    @Mock
    private KrrstubConsumer krrstubConsumer;

    @Mock
    private KrrstubResponseHandler krrStubResponseHandler;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @InjectMocks
    private KrrstubClient krrstubClient;

    @Test
    public void gjenopprett_ingendata() {
        krrstubClient.gjenopprett(new RsDollyBestillingRequest(), DollyPerson.builder().hovedperson(IDENT).build(), new BestillingProgress(), false);

        verify(krrstubConsumer, times(0)).createDigitalKontaktdata(any(DigitalKontaktdata.class));
    }

    @Test
    public void gjenopprett_krrdata_ok() {

        when(mapperFacade.map(any(RsDigitalKontaktdata.class), eq(DigitalKontaktdata.class)))
                .thenReturn(new DigitalKontaktdata());

        when(krrstubConsumer.getDigitalKontaktdata(IDENT)).thenReturn(null);
        when(krrstubConsumer.createDigitalKontaktdata(any(DigitalKontaktdata.class))).thenReturn(ResponseEntity.ok(""));

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setKrrstub(RsDigitalKontaktdata.builder().build());
        krrstubClient.gjenopprett(request,
                DollyPerson.builder().hovedperson(IDENT).build(),
                BestillingProgress.builder()
                        .bestilling(Bestilling.builder().id(BESTILLING_ID).build())
                        .build(), false);

        verify(krrstubConsumer).createDigitalKontaktdata(any(DigitalKontaktdata.class));
        verify(krrStubResponseHandler).extractResponse(any());
    }

    @Test
    public void gjenopprett_krrdata_feil() {

        BestillingProgress progress = BestillingProgress.builder()
                .bestilling(Bestilling.builder().id(BESTILLING_ID).build())
                .build();
        when(krrstubConsumer.getDigitalKontaktdata(IDENT)).thenReturn(null);
        when(mapperFacade.map(any(RsDigitalKontaktdata.class), eq(DigitalKontaktdata.class)))
                .thenReturn(new DigitalKontaktdata());
        when(krrstubConsumer.createDigitalKontaktdata(any(DigitalKontaktdata.class))).thenThrow(HttpClientErrorException.class);
        when(errorStatusDecoder.decodeRuntimeException(any(RuntimeException.class))).thenReturn("Feil:");

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setKrrstub(RsDigitalKontaktdata.builder().build());
        krrstubClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT).build(), progress, false);

        verify(krrstubConsumer).createDigitalKontaktdata(any(DigitalKontaktdata.class));
        verify(krrStubResponseHandler, times(0)).extractResponse(any());

        assertThat(progress.getKrrstubStatus(), containsString("Feil:"));
    }
}