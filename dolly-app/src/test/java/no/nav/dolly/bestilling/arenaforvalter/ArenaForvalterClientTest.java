package no.nav.dolly.bestilling.arenaforvalter;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.Before;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaServicedata;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;

@RunWith(MockitoJUnitRunner.class)
public class ArenaForvalterClientTest {

    private static final String IDENT = "12423353112";
    private static final String ARENA_ENV = "q2";
    private static final String ERROR_CAUSE = "Bad request";
    private static final String ERROR_MSG = "An error has occured";

    @Mock
    private ArenaForvalterConsumer arenaForvalterConsumer;

    @InjectMocks
    private ArenaForvalterClient arenaForvalterClient;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private HttpClientErrorException httpClientErrorException;

    @Mock
    private ResponseEntity responseEntity;

    @Mock
    private MapperFacade mapperFacade;

    @Before
    public void setup() throws Exception {
        when(arenaForvalterConsumer.getIdent(IDENT)).thenReturn(responseEntity);
        when(arenaForvalterConsumer.deleteIdent(IDENT)).thenReturn(responseEntity);
        when(responseEntity.hasBody()).thenReturn(true);
        when(responseEntity.getBody()).thenReturn(buildJsonResult());
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
    }

    @Test
    public void gjenopprett_Ok() {

        BestillingProgress progress = new BestillingProgress();
        when(arenaForvalterConsumer.postArenadata(any(ArenaServicedata.class))).thenReturn(responseEntity);

        arenaForvalterClient.gjenopprett(RsDollyBestilling.builder()
                .arenaforvalter(Arenadata.builder().build())
                .environments(singletonList(ARENA_ENV))
                .build(), NorskIdent.builder().ident(IDENT).build(), progress);

        assertThat(progress.getArenaforvalterStatus(), is(equalTo("HentBruker&status: OK$InaktiverBruker&status: OK$OpprettNyBruker&status: OK")));
        verify(arenaForvalterConsumer).deleteIdent(IDENT);
        verify(arenaForvalterConsumer).postArenadata(any(ArenaServicedata.class));
    }

    @Test
    public void gjenopprett_Feil() {

        BestillingProgress progress = new BestillingProgress();
        when(arenaForvalterConsumer.postArenadata(any(ArenaServicedata.class))).thenThrow(httpClientErrorException);
        when(httpClientErrorException.getMessage()).thenReturn(format("%s %s", HttpStatus.BAD_REQUEST, ERROR_CAUSE));
        when(httpClientErrorException.getResponseBodyAsString()).thenReturn(ERROR_MSG);

        arenaForvalterClient.gjenopprett(RsDollyBestilling.builder()
                .arenaforvalter(Arenadata.builder().build())
                .environments(singletonList(ARENA_ENV))
                .build(), NorskIdent.builder().ident(IDENT).build(), progress);

        assertThat(progress.getArenaforvalterStatus(), is(equalTo(
                "HentBruker&status: OK$InaktiverBruker&status: OK$OpprettNyBruker&status: FEIL: 400 Bad request (An error has occured)")));
        verify(arenaForvalterConsumer).getIdent(IDENT);
        verify(arenaForvalterConsumer).postArenadata(any(ArenaServicedata.class));
    }

    @Test
    public void gjenopprett_EnvironmentForArenaNotSelected() {

        BestillingProgress progress = new BestillingProgress();
        arenaForvalterClient.gjenopprett(RsDollyBestilling.builder()
                        .arenaforvalter(Arenadata.builder().build()).build(),
                NorskIdent.builder().ident(IDENT).build(), progress);

        assertThat(progress.getArenaforvalterStatus(), is(equalTo("ArenaForvalter&Status: Feil: Brukere ikke opprettet i ArenaForvalter da miljø 'q2' ikke er valgt")));
    }

    @Test
    public void gjenopprett_ArenaForvalterNotIncluded() {

        BestillingProgress progress = new BestillingProgress();
        arenaForvalterClient.gjenopprett(RsDollyBestilling.builder().environments(singletonList(ARENA_ENV)).build(),
                NorskIdent.builder().ident(IDENT).build(), progress);

        verifyZeroInteractions(arenaForvalterConsumer);
        assertThat(progress.getArenaforvalterStatus(), is(nullValue()));
    }

    private JsonNode buildJsonResult() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree("{\"arbeidsokerList\": [{\"status\":\"OK\"}]}");
    }
}