package no.nav.dolly.bestilling.arenaforvalter;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.properties.ProvidersProps;

@RunWith(MockitoJUnitRunner.class)
public class ArenaForvalterConsumerTest {

    private static final String IDENT = "12423353";
    private static final String ENV = "u2";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ProvidersProps providersProps;

    @InjectMocks
    private ArenaForvalterConsumer arenaForvalterConsumer;

    @Before
    public void setup() {
        when(providersProps.getArenaForvalter()).thenReturn(ProvidersProps.ArenaForvalter.builder().url("baseUrl").build());
    }

    @Test
    public void deleteIdent() {

        arenaForvalterConsumer.deleteIdent(IDENT, ENV);

        verify(providersProps).getArenaForvalter();
        verify(restTemplate).exchange(any(RequestEntity.class), eq(JsonNode.class));
    }

    @Test
    public void postArenadata() {

        arenaForvalterConsumer.postArenadata(ArenaNyeBrukere.builder()
                .nyeBrukere(singletonList(ArenaNyBruker.builder().personident(IDENT).build()))
                .build());

        verify(providersProps).getArenaForvalter();
        verify(restTemplate).exchange(any(RequestEntity.class), eq(ArenaArbeidssokerBruker.class));
    }

    @Test
    public void getIdent_OK() {

        arenaForvalterConsumer.getIdent(IDENT);

        verify(providersProps).getArenaForvalter();
        verify(restTemplate).exchange(any(RequestEntity.class), eq(ArenaArbeidssokerBruker.class));
    }

    @Test
    public void getEnvironments() {

        arenaForvalterConsumer.getEnvironments();

        verify(providersProps).getArenaForvalter();
        verify(restTemplate).exchange(any(RequestEntity.class), eq(List.class));
    }
}