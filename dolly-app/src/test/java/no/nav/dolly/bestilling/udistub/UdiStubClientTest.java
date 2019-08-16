package no.nav.dolly.bestilling.udistub;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.udistub.RsUdiForholdData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

@RunWith(MockitoJUnitRunner.class)
public class UdiStubClientTest {

	private static final String IDENT = "11111111";

	@Mock
	private UdiStubConsumer udiStubConsumer;

	@Mock
	private UdiStubResponseHandler udiStubResponseHandler;

	@Mock
	private MapperFacade mapperFacade;

	@InjectMocks
	private UdiStubClient udiStubClient;

	@Test
	public void gjenopprett_ingendata() {
		udiStubClient.gjenopprett(new RsDollyBestilling(), NorskIdent.builder().ident(IDENT).build(), new BestillingProgress());
	}

	@Test
	public void gjenopprett_udistub_feiler() {

		BestillingProgress progress = new BestillingProgress();
		when(udiStubConsumer.createUdiForhold(any(), any())).thenThrow(HttpClientErrorException.class);

		udiStubClient.gjenopprett(RsDollyBestilling.builder()
				.udistub(new RsUdiForholdData()).build(), NorskIdent.builder().ident(IDENT).build(), progress);

		assertThat(progress.getUdistubStatus(), containsString("Feil:"));
	}

	@Test
	public void gjenopprett_udistub_ok() {

		BestillingProgress progress = new BestillingProgress();

		when(udiStubConsumer.createUdiForhold(any(), any())).thenReturn(ResponseEntity.ok(""));
		when(udiStubResponseHandler.extractResponse(any())).thenReturn("OK");

		udiStubClient.gjenopprett(RsDollyBestilling.builder()
				.udistub(new RsUdiForholdData()).build(), NorskIdent.builder().ident(IDENT).build(), progress);

		verify(udiStubConsumer).createUdiForhold(any(), any());
		verify(udiStubResponseHandler).extractResponse(any());
		assertThat(progress.getUdistubStatus(), is("OK"));
	}
}