package no.nav.dolly.bestilling.arena;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.arenastub.RsArenadata;

@RunWith(MockitoJUnitRunner.class)
public class ArenaStubClientTest {

    private static final String IDENT = "12423353";
    private static final String ERROR_MSG = "An error has occured";

    @Mock
    private ArenaStubConsumer arenaStubConsumer;

    @InjectMocks
    private ArenaStubClient arenaStubClient;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private HttpClientErrorException httpClientErrorException;

    @Mock
    private ResponseEntity responseEntity;

    @Test
    public void gjenopprett_Ok() {

        BestillingProgress progress = new BestillingProgress();
        when(arenaStubConsumer.postArenadata(any(RsArenadata.class))).thenReturn(responseEntity);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);

        arenaStubClient.gjenopprett(RsDollyBestilling.builder().arenastub(new RsArenadata()).build(),
                NorskIdent.builder().ident(IDENT).build(), progress);

        assertThat(progress.getArenastubStatus(), is(equalTo(HttpStatus.OK.name())));
        verify(arenaStubConsumer).deleteIdent(IDENT);
        verify(arenaStubConsumer).postArenadata(any(RsArenadata.class));
    }

    @Test
    public void gjenopprett_Feil() {

        BestillingProgress progress = new BestillingProgress();
        when(arenaStubConsumer.postArenadata(any(RsArenadata.class))).thenThrow(httpClientErrorException);
        when(httpClientErrorException.getMessage()).thenReturn("An error has occured");
        arenaStubClient.gjenopprett(RsDollyBestilling.builder().arenastub(new RsArenadata()).build(),
                NorskIdent.builder().ident(IDENT).build(), progress);

        assertThat(progress.getArenastubStatus(), is(equalTo("Feil: " + ERROR_MSG)));
        verify(arenaStubConsumer).deleteIdent(IDENT);
        verify(arenaStubConsumer).postArenadata(any(RsArenadata.class));
    }
}