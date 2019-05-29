package no.nav.dolly.bestilling.arenaforvalter;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukerUtenServicebehov;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBrukereUtenServicebehov;
import no.nav.dolly.properties.ProvidersProps;

@RunWith(MockitoJUnitRunner.class)
public class ArenaForvalterConsumerTest {

    private static final String IDENT = "12423353";

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

        arenaForvalterConsumer.deleteIdent(IDENT);

        verify(providersProps).getArenaForvalter();
        verify(restTemplate).exchange(any(RequestEntity.class), eq(JsonNode.class));
    }

    @Test
    public void postArenadata() {

        arenaForvalterConsumer.postArenadata(ArenaBrukereUtenServicebehov.builder()
                .nyeBrukereUtenServiceBehov(singletonList(ArenaBrukerUtenServicebehov.builder().personident(IDENT).build()))
                .build());

        verify(providersProps).getArenaForvalter();
        verify(restTemplate).exchange(any(RequestEntity.class), eq(JsonNode.class));
    }
}