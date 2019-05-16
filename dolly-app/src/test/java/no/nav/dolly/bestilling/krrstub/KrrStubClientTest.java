package no.nav.dolly.bestilling.krrstub;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdataRequest;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;

@RunWith(MockitoJUnitRunner.class)
public class KrrStubClientTest {

    private static final String IDENT = "111111111";
    private static final Long BESTILLING_ID = 1L;

    @Mock
    private KrrStubConsumer krrStubConsumer;

    @Mock
    private KrrStubResponseHandler krrStubResponseHandler;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private KrrStubClient krrStubClient;

    @Test
    public void gjenopprett_ingendata() {
        krrStubClient.gjenopprett(new RsDollyBestilling(), NorskIdent.builder().ident(IDENT).build(), new BestillingProgress());

        verify(krrStubConsumer, times(0)).createDigitalKontaktdata(anyLong(), any(DigitalKontaktdataRequest.class));
    }

    @Test
    public void gjenopprett_krrdata_ok() {

        when(mapperFacade.map(any(RsDigitalKontaktdata.class), eq(DigitalKontaktdataRequest.class)))
                .thenReturn(new DigitalKontaktdataRequest());
        when(krrStubConsumer.createDigitalKontaktdata(anyLong(), any(DigitalKontaktdataRequest.class))).thenReturn(ResponseEntity.ok(""));

        krrStubClient.gjenopprett(RsDollyBestilling.builder().krrstub(new RsDigitalKontaktdata()).build(),
                NorskIdent.builder().ident(IDENT).build(),
                BestillingProgress.builder().bestillingId(BESTILLING_ID).build());

        verify(krrStubConsumer).createDigitalKontaktdata(anyLong(), any(DigitalKontaktdataRequest.class));
        verify(krrStubResponseHandler).extractResponse(any(ResponseEntity.class));
    }

    @Test
    public void gjenopprett_krrdata_feil() {

        BestillingProgress progress = BestillingProgress.builder().bestillingId(BESTILLING_ID).build();
        when(mapperFacade.map(any(RsDigitalKontaktdata.class), eq(DigitalKontaktdataRequest.class)))
                .thenReturn(new DigitalKontaktdataRequest());
        when(krrStubConsumer.createDigitalKontaktdata(anyLong(), any(DigitalKontaktdataRequest.class))).thenThrow(HttpClientErrorException.class);

        krrStubClient.gjenopprett(RsDollyBestilling.builder()
                .krrstub(new RsDigitalKontaktdata())
                .build(), NorskIdent.builder().ident(IDENT).build(), progress);

        verify(krrStubConsumer).createDigitalKontaktdata(anyLong(), any(DigitalKontaktdataRequest.class));
        verify(krrStubResponseHandler, times(0)).extractResponse(any(ResponseEntity.class));

        assertThat(progress.getKrrstubStatus(), containsString("Feil:"));
    }
}