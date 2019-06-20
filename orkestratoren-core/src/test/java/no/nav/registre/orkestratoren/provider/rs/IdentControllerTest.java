package no.nav.registre.orkestratoren.provider.rs;

import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.orkestratoren.service.IdentService;

@RunWith(MockitoJUnitRunner.class)
public class IdentControllerTest {

    @Mock
    private IdentService identService;

    @InjectMocks
    private IdentController identController;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private String testdataEier = "test";
    private List<String> identer;

    @Before
    public void setUp() {
        identer = new ArrayList<>(Arrays.asList("01010101010", "02020202020"));
    }

    @Test
    public void shouldSletteIdenterFraAdaptere() {
        identController.slettIdenterFraAdaptere(avspillergruppeId, miljoe, testdataEier, identer);

        verify(identService).slettIdenterFraAdaptere(avspillergruppeId, miljoe, testdataEier, identer);
    }
}