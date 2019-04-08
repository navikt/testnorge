package no.nav.dolly.bestilling.arena;

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

import no.nav.dolly.domain.resultset.arenastub.RsArenadata;
import no.nav.dolly.properties.ProvidersProps;

@RunWith(MockitoJUnitRunner.class)
public class ArenaStubConsumerTest {

    private static final String IDENT = "12423353";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ProvidersProps providersProps;

    @InjectMocks
    private ArenaStubConsumer arenaStubConsumer;

    @Before
    public void setup() {
        when(providersProps.getArenaStub()).thenReturn(ProvidersProps.ArenaStub.builder().url("baseUrl").build());
    }

    @Test
    public void deleteIdent() {

        arenaStubConsumer.deleteIdent(IDENT);

        verify(providersProps).getArenaStub();
        verify(restTemplate).exchange(any(RequestEntity.class), eq(JsonNode.class));
    }

    @Test
    public void postArenadata() {

        arenaStubConsumer.postArenadata(RsArenadata.builder().personident(IDENT).build());

        verify(providersProps).getArenaStub();
        verify(restTemplate).exchange(any(RequestEntity.class), eq(JsonNode.class));
    }
}