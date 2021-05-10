package no.nav.registre.hodejegeren.provider.rs;

import static no.nav.registre.hodejegeren.provider.rs.HodejegerenController.MAX_ALDER;
import static no.nav.registre.hodejegeren.provider.rs.HodejegerenController.MIN_ALDER;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.registre.hodejegeren.service.CacheService;
import no.nav.registre.hodejegeren.service.EksisterendeIdenterService;
import no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService;
import no.nav.registre.hodejegeren.service.Endringskoder;

@RunWith(MockitoJUnitRunner.class)
public class HodejegerenControllerTest {

    @Mock
    private EksisterendeIdenterService eksisterendeIdenterService;

    @Mock
    private CacheService cacheService;

    @Mock
    private EndringskodeTilFeltnavnMapperService endringskodeTilFeltnavnMapperService;

    @InjectMocks
    private HodejegerenController hodejegerenController;

    private String fnr;
    private Long avspillergruppeId;
    private String miljoe;
    private String endringskode;
    private int antallIdenter;
    private int minimumAlder;
    private int maksimumAlder;
    private List<String> myndigeIdenter;

    @Before
    public void setUp() {
        fnr = "01010101010";
        avspillergruppeId = 100000445L;
        miljoe = "t9";
        endringskode = Endringskoder.FOEDSELSMELDING.getEndringskode();
        antallIdenter = 10;
        minimumAlder = 18;
        maksimumAlder = 67;
        myndigeIdenter = new ArrayList<>();
        myndigeIdenter.add(fnr);
    }

    /**
     * Scenario: HVIS hodejeger-controlleren får et request om å hente identer i gruppe, skal metoden kalle på
     * {@link CacheService#hentAlleIdenterCache(Long)}
     */
    @Test
    public void shouldHenteAlleIdenterIGruppe() {
        hodejegerenController.hentAlleIdenterIGruppe(avspillergruppeId);
        verify(cacheService).hentAlleIdenterCache(avspillergruppeId);
    }

    /**
     * Scenario: HVIS hodejeger-controlleren får et request om å hente levende identer i gruppe, skal metoden kalle på
     * {@link CacheService#hentLevendeIdenterCache(Long)}
     */
    @Test
    public void shouldHenteLevendeIdenterIGruppe() {
        hodejegerenController.hentLevendeIdenterIGruppe(avspillergruppeId);
        verify(cacheService).hentLevendeIdenterCache(avspillergruppeId);
    }

    /**
     * Scenario: HVIS hodejeger-controlleren får et request om å hente døde og utvandrede identer i gruppe, skal metoden kalle på
     * {@link CacheService#hentDoedeOgUtvandredeIdenterCache(Long)}
     */
    @Test
    public void shouldHenteDoedeOgUtvandredeIdenterIGruppe() {
        hodejegerenController.hentDoedeOgUtvandredeIdenterIGruppe(avspillergruppeId);
        verify(cacheService).hentDoedeOgUtvandredeIdenterCache(avspillergruppeId);
    }

    /**
     * Scenario: HVIS hodejeger-controlleren får et request om å hente gifte identer i gruppe, skal metoden kalle på
     * {@link CacheService#hentGifteIdenterCache(Long)}
     */
    @Test
    public void shouldHenteGifteIdenterIGruppe() {
        hodejegerenController.hentGifteIdenterIGruppe(avspillergruppeId);
        verify(cacheService).hentGifteIdenterCache(avspillergruppeId);
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
        hodejegerenController.hentAlleLevendeIdenterOverAlder(avspillergruppeId, 13);
        verify(eksisterendeIdenterService).finnAlleIdenterOverAlder(avspillergruppeId, 13);
    }

    @Test
    public void shouldHenteAlleIdenterIAldersgruppe() {
        hodejegerenController.hentAlleIdenterIAldersgruppe(avspillergruppeId, minimumAlder, maksimumAlder);
        verify(eksisterendeIdenterService).finnLevendeIdenterIAldersgruppe(avspillergruppeId, minimumAlder, maksimumAlder);
    }

    @Test
    public void shouldHenteIdenterMedStatusQuo() {
        hodejegerenController.hentEksisterendeIdenterMedStatusQuo(avspillergruppeId, miljoe, 1, MIN_ALDER, MAX_ALDER);
        verify(eksisterendeIdenterService).hentGittAntallIdenterMedStatusQuo(avspillergruppeId, miljoe, 1, MIN_ALDER, MAX_ALDER);
    }

    @Test
    public void shouldHenteAdresserPaaIdenter() {
        List<String> identer = new ArrayList<>(Collections.singleton(fnr));
        hodejegerenController.hentAdressePaaIdenter(miljoe, identer);
        verify(eksisterendeIdenterService).hentAdressePaaIdenter(miljoe, identer);
    }

    @Test
    public void shouldHenteAlleFoedteIdenter() {
        hodejegerenController.hentFoedteIdenter(avspillergruppeId, minimumAlder, maksimumAlder);
        verify(eksisterendeIdenterService).finnFoedteIdenter(avspillergruppeId, minimumAlder, maksimumAlder);
    }

    @Test
    public void shouldHentePersondataTilIdent() {
        String ident = "01010101010";
        hodejegerenController.hentPersondataTilIdent(ident, miljoe);
        verify(eksisterendeIdenterService).hentPersondata(ident, miljoe);
    }

    @Test
    public void shouldHenteIdenterMedKontoinformasjon() {
        hodejegerenController.hentPersondataMedKontoinformasjon(avspillergruppeId, miljoe, antallIdenter, minimumAlder, maksimumAlder);
        verify(eksisterendeIdenterService).hentGittAntallIdenterMedKononummerinfo(avspillergruppeId, miljoe, antallIdenter, minimumAlder, maksimumAlder);
    }

    @Test
    public void shouldHenteRelasjonerTilIdent() {
        String ident = "01010101010";
        hodejegerenController.hentRelasjonerTilIdent(ident, miljoe);
        verify(eksisterendeIdenterService).hentRelasjoner(ident, miljoe);
    }

    @Test
    public void shouldHenteIdenterSomIkkeErITps() {
        hodejegerenController.hentIdenterSomIkkeErITps(avspillergruppeId, miljoe);
        verify(eksisterendeIdenterService).hentIdenterSomIkkeErITps(avspillergruppeId, miljoe);
    }

    @Test
    public void shouldHenteIdenterSomKolliderer() {
        hodejegerenController.hentIdenterSomKolliderer(avspillergruppeId);
        verify(eksisterendeIdenterService).hentIdenterSomKolliderer(avspillergruppeId);
    }
}
