package no.nav.dolly.bestilling.sigrunstub;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SigrunStubClientTest {

    private static final String IDENT = "11111111";

    @Mock
    private SigrunStubConsumer sigrunStubConsumer;

    @Mock
    private SigrunStubResponseHandler sigrunStubResponseHandler;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private SigrunStubClient sigrunStubClient;

    @Test
    public void gjenopprett_ingendata() {
        BestillingProgress progress = new BestillingProgress();
        sigrunStubClient.gjenopprett(new RsDollyBestillingRequest(), DollyPerson.builder().hovedperson(IDENT).build(),
                new BestillingProgress(), false);

        assertThat(progress.getSigrunstubStatus(), is(nullValue()));
    }

    @Test
    public void gjenopprett_sigrunstub_feiler() {

        BestillingProgress progress = new BestillingProgress();
        when(sigrunStubConsumer.deleteSkattegrunnlag(anyList())).thenReturn(Mono.just(emptyList()));
        when(sigrunStubConsumer.createSkattegrunnlag(anyList())).thenThrow(HttpClientErrorException.class);
        when(errorStatusDecoder.decodeRuntimeException(any(RuntimeException.class))).thenReturn("Feil:");

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setSigrunstub(singletonList(new OpprettSkattegrunnlag()));

        sigrunStubClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT).build(), progress, false);

        assertThat(progress.getSigrunstubStatus(), containsString("Feil:"));
    }

    @Test
    public void gjenopprett_sigrunstub_ok() {

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setSigrunstub(singletonList(new OpprettSkattegrunnlag()));

        when(mapperFacade.mapAsList(any(List.class), eq(OpprettSkattegrunnlag.class))).thenReturn(request.getSigrunstub());
        BestillingProgress progress = new BestillingProgress();

        when(sigrunStubConsumer.createSkattegrunnlag(anyList())).thenReturn(ResponseEntity.ok(new SigrunResponse()));
        when(sigrunStubResponseHandler.extractResponse(any())).thenReturn("OK");

        when(sigrunStubConsumer.deleteSkattegrunnlag(anyList())).thenReturn(Mono.just(emptyList()));

        sigrunStubClient.gjenopprett(request, DollyPerson.builder().hovedperson(IDENT).build(), progress, false);

        verify(sigrunStubConsumer).createSkattegrunnlag(anyList());
        verify(sigrunStubResponseHandler).extractResponse(any());
        assertThat(progress.getSigrunstubStatus(), is("OK"));
    }
}