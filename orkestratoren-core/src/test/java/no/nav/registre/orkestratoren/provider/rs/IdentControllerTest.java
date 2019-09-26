package no.nav.registre.orkestratoren.provider.rs;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.service.IdentService;

@RunWith(MockitoJUnitRunner.class)
public class IdentControllerTest {

    @Mock
    private IdentService identService;

    @InjectMocks
    private IdentController identController;

    private Long avspillergruppeId = 100000445L;
    private String miljoe = "t9";
    private String testdataEier = "orkestratoren";
    private List<String> identer;
    private Map<Long, String> avspillergruppeIdMedMiljoe;

    @Before
    public void setUp() {
        identer = new ArrayList<>(Arrays.asList("01010101010", "02020202020"));

        avspillergruppeIdMedMiljoe = new HashMap<>();
        avspillergruppeIdMedMiljoe.put(avspillergruppeId, miljoe);
        ReflectionTestUtils.setField(identController, "avspillergruppeIdMedMiljoe", avspillergruppeIdMedMiljoe);
    }

    @Test
    public void shouldSletteIdenterFraAdaptere() {
        identController.slettIdenterFraAdaptere(avspillergruppeId, miljoe, testdataEier, identer);

        verify(identService).slettIdenterFraAdaptere(avspillergruppeId, miljoe, testdataEier, identer);
    }

    @Test
    public void shouldSynkronisereMedTps() {
        identController.synkroniserMedTps();

        verify(identService).synkroniserMedTps(avspillergruppeId, miljoe);
    }

    @Test
    public void shouldRenseAvspillergruppe() {
        identController.rensAvspillergruppe();

        verify(identService).fjernKolliderendeIdenter(avspillergruppeId, miljoe);
    }
}