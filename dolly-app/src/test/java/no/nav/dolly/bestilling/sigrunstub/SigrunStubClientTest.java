package no.nav.dolly.bestilling.sigrunstub;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.sigrunstub.RsOpprettSkattegrunnlag;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

@RunWith(MockitoJUnitRunner.class)
public class SigrunStubClientTest {

    private static final String IDENT = "11111111";

    @Mock
    private SigrunStubConsumer sigrunStubConsumer;

    @Mock
    private SigrunStubResponseHandler sigrunStubResponseHandler;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @InjectMocks
    private SigrunStubClient sigrunStubClient;

    //TODO Ikke en veldig dekkende test uten asserts av noe slag
    @Test
    public void gjenopprett_ingendata() {

        sigrunStubClient.gjenopprett(new RsDollyBestilling(), NorskIdent.builder().ident(IDENT).build(), new BestillingProgress());
    }

    @Test
    public void gjenopprett_sigrunstub_feiler() {

        BestillingProgress progress = new BestillingProgress();
        when(sigrunStubConsumer.createSkattegrunnlag(anyList())).thenThrow(HttpClientErrorException.class);
        when(errorStatusDecoder.decodeRuntimeException(any(RuntimeException.class))).thenReturn("Feil:");

        sigrunStubClient.gjenopprett(RsDollyBestilling.builder()
                .sigrunstub(singletonList(new RsOpprettSkattegrunnlag())).build(), NorskIdent.builder().ident(IDENT).build(), progress);

        assertThat(progress.getSigrunstubStatus(), containsString("Feil:"));
    }

    @Test
    public void gjenopprett_sigrunstub_ok() {

        BestillingProgress progress = new BestillingProgress();

        when(sigrunStubConsumer.createSkattegrunnlag(anyList())).thenReturn(ResponseEntity.ok(""));
        when(sigrunStubResponseHandler.extractResponse(any())).thenReturn("OK");

        sigrunStubClient.gjenopprett(RsDollyBestilling.builder()
                .sigrunstub(singletonList(new RsOpprettSkattegrunnlag())).build(), NorskIdent.builder().ident(IDENT).build(), progress);

        verify(sigrunStubConsumer).createSkattegrunnlag(anyList());
        verify(sigrunStubResponseHandler).extractResponse(any());
        assertThat(progress.getSigrunstubStatus(), is("OK"));
    }
}