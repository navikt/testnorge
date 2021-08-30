package no.nav.registre.skd.provider.rs;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.registre.skd.service.IdentService;

@RunWith(MockitoJUnitRunner.class)
public class IdentControllerTest {

    @Mock
    private IdentService identService;

    @InjectMocks
    private IdentController identController;

    private final Long avspillergruppeId = 123L;
    private final List<String> miljoer = new ArrayList<>(Collections.singletonList("t1"));

    @Test
    public void shouldSletteIdenterFraAvspillergruppe() {
        var identer = new ArrayList<>(Collections.singletonList("01010101010"));
        identController.slettIdenterFraAvspillergruppe(avspillergruppeId, miljoer, identer);
        verify(identService).slettIdenterFraAvspillergruppe(avspillergruppeId, miljoer, identer);
    }
}