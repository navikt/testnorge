package no.nav.registre.hodejegeren.provider.rs;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.registre.hodejegeren.provider.rs.requests.HistorikkRequest;
import no.nav.registre.hodejegeren.service.HistorikkService;

@RunWith(MockitoJUnitRunner.class)
public class HistorikkControllerTest {

    @Mock
    private HistorikkService historikkService;

    @InjectMocks
    private HistorikkController historikkController;

    private String id = "01010101010";

    @Test
    public void shouldHenteAllHistorikk() {
        historikkController.hentAllHistorikk();
        verify(historikkService).hentAllHistorikk();
    }

    @Test
    public void shouldHenteHistorikkMedId() {
        historikkController.hentHistorikkMedId(id);
        verify(historikkService).hentHistorikkMedId(id);
    }

    @Test
    public void shouldLeggeTilHistorikk() {
        List<HistorikkRequest> historikkRequests = new ArrayList<>(Collections.singletonList(HistorikkRequest.builder().build()));
        historikkController.leggTilHistorikk(historikkRequests);
        verify(historikkService).leggTilHistorikkPaaIdent(historikkRequests);
    }

    @Test
    public void shouldSletteHistorikk() {
        historikkController.slettHistorikk(id);
        verify(historikkService).slettHistorikk(id);
    }

    @Test
    public void shouldSletteKilde() {
        String navnPaaKilde = "kilde";
        historikkController.slettKilde(id, navnPaaKilde);
        verify(historikkService).slettKilde(id, navnPaaKilde);
    }
}