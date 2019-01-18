package no.nav.registre.hodejegeren.provider.rs;

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
import java.util.Arrays;
import java.util.List;

import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
import no.nav.registre.hodejegeren.consumer.requests.SendToTpsRequest;
import no.nav.registre.hodejegeren.provider.rs.requests.LagreITpsfRequest;
import no.nav.registre.hodejegeren.service.EksisterendeIdenterService;
import no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService;
import no.nav.registre.hodejegeren.service.Endringskoder;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;

@RunWith(MockitoJUnitRunner.class)
public class HodejegerenControllerTest {

    @Mock
    private EksisterendeIdenterService eksisterendeIdenterService;

    @Mock
    private EndringskodeTilFeltnavnMapperService endringskodeTilFeltnavnMapperService;

    @Mock
    private TpsfConsumer tpsfConsumer;

    @InjectMocks
    private HodejegerenController hodejegerenController;

    private String fnr;
    private Long avspillergruppeId;
    private String miljoe;
    private Endringskoder endringskode;
    private int antallIdenter;
    private int minimumAlder;
    private LagreITpsfRequest lagreITpsfRequest;
    private SendToTpsRequest sendToTpsRequest;
    private List<String> myndigeIdenter;

    @Before
    public void setUp() {
        fnr = "01010101010";
        avspillergruppeId = 100000445L;
        miljoe = "t9";
        endringskode = Endringskoder.FOEDSELSMELDING;
        antallIdenter = 10;
        minimumAlder = 18;
        List<Long> ids = new ArrayList<>(Arrays.asList(123L, 234L));
        List<RsMeldingstype> meldinger = new ArrayList<>();
        meldinger.add(RsMeldingstype1Felter.builder().build());
        lagreITpsfRequest = new LagreITpsfRequest(avspillergruppeId, meldinger);
        sendToTpsRequest = new SendToTpsRequest(miljoe, ids);
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

    /**
     * Scenario: HVIS hodejeger-controlleren får et request om å lagre en liste med meldinger i en viss avspillergruppe i TPSF, skal metoden kalle på
     * {@link TpsfConsumer#saveSkdEndringsmeldingerInTPSF}
     */
    @Test
    public void shouldLagreSkdEndringsmeldignerITpsf() {
        hodejegerenController.lagreSkdEndringsmeldingerITpsf(lagreITpsfRequest);
        verify(tpsfConsumer).saveSkdEndringsmeldingerInTPSF(avspillergruppeId, lagreITpsfRequest.getSkdMeldinger());
    }

    /**
     * Scenario: HVIS hodejeger-controlleren får et request om å sende en liste med id-er fra en viss avspillergruppe til TPS, skal metoden kalle på
     * {@link TpsfConsumer#sendSkdmeldingerToTps}
     */
    @Test
    public void shouldSendeSkdEndringsmeldignerTilTpsf() {
        hodejegerenController.sendSkdEndringsmeldingerTilTps(avspillergruppeId, sendToTpsRequest);
        verify(tpsfConsumer).sendSkdmeldingerToTps(avspillergruppeId, sendToTpsRequest);
    }
}
