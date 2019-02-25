package no.nav.registre.hodejegeren.provider.rs;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import no.nav.registre.hodejegeren.service.EksisterendeIdenterService;
import no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService;
import no.nav.registre.hodejegeren.service.Endringskoder;

@RunWith(MockitoJUnitRunner.class)
public class HodejegerenControllerTest {

    @Mock
    private EksisterendeIdenterService eksisterendeIdenterService;

    @Mock
    private EndringskodeTilFeltnavnMapperService endringskodeTilFeltnavnMapperService;

    @InjectMocks
    private HodejegerenController hodejegerenController;

    private String fnr;
    private Long avspillergruppeId;
    private String miljoe;
    private Endringskoder endringskode;
    private int antallIdenter;
    private int minimumAlder;
    private List<String> myndigeIdenter;

    @Before
    public void setUp() {
        fnr = "01010101010";
        avspillergruppeId = 100000445L;
        miljoe = "t9";
        endringskode = Endringskoder.FOEDSELSMELDING;
        antallIdenter = 10;
        minimumAlder = 18;
        myndigeIdenter = new ArrayList<>();
        myndigeIdenter.add(fnr);
    }

    /**
     * Scenario: HVIS hodejeger-controlleren får et request om å hente levende identer i gruppe, skal metoden kalle på
     * {@link EksisterendeIdenterService#finnLevendeIdenter}
     */
    @Test
    public void shouldHenteLevendeIdenterIGruppe() {
        hodejegerenController.hentLevendeIdenterIGruppe(avspillergruppeId);
        verify(eksisterendeIdenterService).finnLevendeIdenter(avspillergruppeId);
    }

    /**
     * Scenario: HVIS hodejeger-controlleren får et request om å hente døde og utvandrede identer i gruppe, skal metoden kalle på
     * {@link EksisterendeIdenterService#finnDoedeOgUtvandredeIdenter}
     */
    @Test
    public void shouldHenteDoedeOgUtvandredeIdenterIGruppe() {
        hodejegerenController.hentDoedeOgUtvandredeIdenterIGruppe(avspillergruppeId);
        verify(eksisterendeIdenterService).finnDoedeOgUtvandredeIdenter(avspillergruppeId);
    }

    /**
     * Scenario: HVIS hodejeger-controlleren får et request om å hente gifte identer i gruppe, skal metoden kalle på
     * {@link EksisterendeIdenterService#finnGifteIdenter}
     */
    @Test
    public void shouldHenteGifteIdenterIGruppe() {
        hodejegerenController.hentGifteIdenterIGruppe(avspillergruppeId);
        verify(eksisterendeIdenterService).finnGifteIdenter(avspillergruppeId);
    }

    /**
     * Scenario: HVIS hodejeger-controlleren får et request om å hente levende identer i gruppe i angitt miljø, skal metoden kalle på
     * {@link EksisterendeIdenterService#hentLevendeIdenterIGruppeOgSjekkStatusQuo}
     */
    @Test
    public void shouldHenteLevendeIdenterIGruppeAndCheckStatusQuo() {
        hodejegerenController.hentLevendeIdenter(avspillergruppeId, miljoe, antallIdenter, minimumAlder);
        verify(eksisterendeIdenterService).hentLevendeIdenterIGruppeOgSjekkStatusQuo(avspillergruppeId, miljoe, antallIdenter, minimumAlder);
    }

    /**
     * Scenario: HVIS hodejeger-controlleren får et request om å hente myndige identer i gruppe med tilknyttet nav-enhet i angitt miljø, skal metoden kalle på
     * {@link EksisterendeIdenterService#hentLevendeIdenterIGruppeOgSjekkStatusQuo} for å finne myndige identer, og deretter {@link EksisterendeIdenterService#hentFnrMedNavKontor}
     * for å finne tilknyttet nav-enhet
     */
    @Test
    public void shouldHenteMyndigeIdenterMedNavEnhetIGruppeAndCheckStatusQuo() {
        when(eksisterendeIdenterService.hentLevendeIdenterIGruppeOgSjekkStatusQuo(avspillergruppeId, miljoe, antallIdenter, minimumAlder)).thenReturn(myndigeIdenter);
        hodejegerenController.hentEksisterendeMyndigeIdenterMedNavKontor(avspillergruppeId, miljoe, antallIdenter);
        verify(eksisterendeIdenterService).hentLevendeIdenterIGruppeOgSjekkStatusQuo(avspillergruppeId, miljoe, antallIdenter, minimumAlder);
        verify(eksisterendeIdenterService).hentFnrMedNavKontor(miljoe, myndigeIdenter);
    }

    /**
     * Scenario: HVIS hodejeger-controlleren får et request om å hente hente status quo på et fnr i et miljø med en viss endringskode, skal metoden kalle på
     * {@link EndringskodeTilFeltnavnMapperService#getStatusQuoFraAarsakskode}
     */
    @Test
    public void shouldHenteStatusQuoFraEndringskode() throws IOException {
        hodejegerenController.hentStatusQuoFraEndringskode(endringskode, miljoe, fnr);
        verify(endringskodeTilFeltnavnMapperService).getStatusQuoFraAarsakskode(endringskode, miljoe, fnr);
    }

    @Test
    public void hentAlleLevendeOverAlderTest() {
        MockHttpServletResponse resp = new MockHttpServletResponse();
        hodejegerenController.hentAlleLevendeIdenterOverAlder(avspillergruppeId, 13, resp);
        verify(eksisterendeIdenterService).finnAlleIdenterOverAlder(avspillergruppeId, 13);
    }

    @Test
    public void shouldHenteIdenterMedStatusQuo() {
        hodejegerenController.hentEksisterendeIdenterMedStatusQuo(avspillergruppeId, miljoe, 1);
        verify(eksisterendeIdenterService).hentGittAntallIdenterMedStatusQuo(avspillergruppeId, miljoe, 1);
    }
}
