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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;

import no.nav.registre.skd.consumer.IdentPoolConsumer;
import no.nav.registre.skd.consumer.TpsfConsumer;

@RunWith(MockitoJUnitRunner.class)
public class IdentServiceTest {

    @Mock
    private TpsfConsumer tpsfConsumer;

    @Mock
    private IdentPoolConsumer identPoolConsumer;

    @InjectMocks
    private IdentService identService;

    @Test
    public void shouldDeleteIdents() {
        var avspillergruppeId = 123L;
        var miljoer = new ArrayList<>(Collections.singletonList("t1"));
        var identer = new ArrayList<>(Collections.singletonList("01010101010"));
        var meldingIder = new ArrayList<>(Collections.singletonList(1L));

        when(tpsfConsumer.getMeldingIderTilhoerendeIdenter(avspillergruppeId, identer)).thenReturn(meldingIder);
        when(tpsfConsumer.slettMeldingerFraTpsf(meldingIder)).thenReturn(HttpStatus.ACCEPTED);
        when(identPoolConsumer.frigjoerLedigeIdenter(identer)).thenReturn(new ArrayList<>(identer));

        var response = identService.slettIdenterFraAvspillergruppe(avspillergruppeId, miljoer, identer);

        verify(tpsfConsumer).getMeldingIderTilhoerendeIdenter(avspillergruppeId, identer);
        verify(tpsfConsumer).slettMeldingerFraTpsf(meldingIder);
        verify(tpsfConsumer).slettIdenterFraTps(miljoer, identer);
        verify(identPoolConsumer).frigjoerLedigeIdenter(identer);

        assertThat(response, IsIterableContainingInOrder.contains(meldingIder.toArray()));
    }
}