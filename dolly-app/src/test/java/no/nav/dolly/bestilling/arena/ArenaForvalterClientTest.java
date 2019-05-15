package no.nav.dolly.bestilling.arena;

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
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaBrukerMedServicebehov;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaBrukerUtenServicebehov;

@RunWith(MockitoJUnitRunner.class)
public class ArenaForvalterClientTest {

    private static final String IDENT = "12423353";
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

    @Test
    public void gjenopprett_Ok() {

        BestillingProgress progress = new BestillingProgress();
        when(arenaForvalterConsumer.postArenadata(any(Arenadata.class))).thenReturn(responseEntity);

        arenaForvalterClient.gjenopprett(RsDollyBestilling.builder()
                .arenaForvalter(RsArenaBrukerMedServicebehov.builder().build())
                .environments(singletonList(ARENA_ENV))
                .build(), IDENT, progress);

        assertThat(progress.getArenastubStatus(), is(equalTo("arenaDeleteBruker&status: OK$arenaOpprettBruker&status: OK")));
        verify(arenaForvalterConsumer).deleteIdent(IDENT);
        verify(arenaForvalterConsumer).postArenadata(any(Arenadata.class));
    }

    @Test
    public void gjenopprett_Feil() {

        BestillingProgress progress = new BestillingProgress();
        when(arenaForvalterConsumer.postArenadata(any(Arenadata.class))).thenThrow(httpClientErrorException);
        when(httpClientErrorException.getMessage()).thenReturn(format("%s %s", HttpStatus.BAD_REQUEST, ERROR_CAUSE));
        when(httpClientErrorException.getResponseBodyAsString()).thenReturn(ERROR_MSG);
        arenaForvalterClient.gjenopprett(RsDollyBestilling.builder()
                .arenaForvalter(RsArenaBrukerUtenServicebehov.builder().build())
                .environments(singletonList(ARENA_ENV))
                .build(), IDENT, progress);

        assertThat(progress.getArenastubStatus(), is(equalTo("arenaDeleteBruker&status: OK"
                + "$arenaOpprettBruker&status: FEIL: 400 Bad request (An error has occured)")));
        verify(arenaForvalterConsumer).deleteIdent(IDENT);
        verify(arenaForvalterConsumer).postArenadata(any(Arenadata.class));
    }

    @Test
    public void gjenopprett_EnvironmentForArenaNotSelected() {

        BestillingProgress progress = new BestillingProgress();
        arenaForvalterClient.gjenopprett(RsDollyBestilling.builder().arenaForvalter(RsArenaBrukerUtenServicebehov.builder().build()).build(), IDENT, progress);

        assertThat(progress.getArenastubStatus(), is(equalTo("Info: Brukere ikke opprettet i ArenaForvalter da miljø 'q2' ikke er valgt")));
    }

    @Test
    public void gjenopprett_ArenaForvalterNotIncluded() {

        BestillingProgress progress = new BestillingProgress();
        arenaForvalterClient.gjenopprett(RsDollyBestilling.builder().environments(singletonList(ARENA_ENV)).build(), IDENT, progress);

        verifyZeroInteractions(arenaForvalterConsumer);
        assertThat(progress.getArenastubStatus(), is(nullValue()));
    }
}