package no.nav.registre.orkestratoren.provider.rs;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.service.IdentService;

@ExtendWith(MockitoExtension.class)
class IdentControllerTest {

    @Mock
    private IdentService identService;

    @InjectMocks
    private IdentController identController;

    private final Long avspillergruppeId = 100000445L;
    private final String miljoe = "t9";
    private List<String> identer;

    @BeforeEach
    public void setUp() {
        identer = new ArrayList<>(Arrays.asList("01010101010", "02020202020"));
        ReflectionTestUtils.setField(identController, "avspillergruppeId", avspillergruppeId);
        ReflectionTestUtils.setField(identController, "miljoe", miljoe);
    }

    @Test
    void shouldSletteIdenterFraAdaptere() {
        var testdataEier = "orkestratoren";
        identController.slettIdenterFraAdaptere(avspillergruppeId, miljoe, testdataEier, identer);

        verify(identService).slettIdenterFraAdaptere(avspillergruppeId, miljoe, testdataEier, identer);
    }

    @Test
    void shouldSynkronisereMedTps() {
        identController.synkroniserMedTps();

        verify(identService).synkroniserMedTps(avspillergruppeId, miljoe);
    }

    @Test
    void shouldRenseAvspillergruppe() {
        identController.rensAvspillergruppe();

        verify(identService).fjernKolliderendeIdenter(avspillergruppeId, miljoe);
    }
}