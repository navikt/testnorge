package no.nav.registre.skd.service;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.registre.skd.consumer.TpsfConsumer;

@RunWith(MockitoJUnitRunner.class)
public class IdentServiceTest {

    @Mock
    private TpsfConsumer tpsfConsumer;

    @InjectMocks
    private IdentService identService;

    @Test
    public void shouldDeleteIdents() {
        Long avspilergruppeId = 123L;
        List<String> miljoer = new ArrayList<>(Collections.singletonList("t1"));
        List<String> identer = new ArrayList<>(Collections.singletonList("01010101010"));
        List<Long> meldingIder = new ArrayList<>(Collections.singletonList(1L));

        when(tpsfConsumer.getMeldingIderTilhoerendeIdenter(avspilergruppeId, identer)).thenReturn(meldingIder);
        when(tpsfConsumer.slettMeldingerFraTpsf(meldingIder)).thenReturn(ResponseEntity.ok().build());

        List<Long> response = identService.slettIdenterFraAvspillergruppe(avspilergruppeId, miljoer, identer);

        verify(tpsfConsumer).getMeldingIderTilhoerendeIdenter(avspilergruppeId, identer);
        verify(tpsfConsumer).slettMeldingerFraTpsf(meldingIder);
        verify(tpsfConsumer).slettIdenterFraTps(miljoer, identer);

        assertThat(response, IsIterableContainingInOrder.contains(meldingIder.toArray()));
    }
}