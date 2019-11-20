package no.nav.registre.orkestratoren.provider.rs;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserBisysRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserEiaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserMedlRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserNavmeldingerRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSkdmeldingerRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;
import no.nav.registre.orkestratoren.service.TesnorgeArenaService;
import no.nav.registre.orkestratoren.service.TestnorgeAaregService;
import no.nav.registre.orkestratoren.service.TestnorgeBisysService;
import no.nav.registre.orkestratoren.service.TestnorgeEiaService;
import no.nav.registre.orkestratoren.service.TestnorgeInntektService;
import no.nav.registre.orkestratoren.service.TestnorgeInstService;
import no.nav.registre.orkestratoren.service.TestnorgeMedlService;
import no.nav.registre.orkestratoren.service.TestnorgeSigrunService;
import no.nav.registre.orkestratoren.service.TestnorgeSkdService;
import no.nav.registre.orkestratoren.service.TestnorgeTpService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringsControllerTest {

    @Mock
    private TestnorgeSkdService testnorgeSkdService;

    @Mock
    private TestnorgeInntektService testnorgeInntektService;

    @Mock
    private TestnorgeEiaService testnorgeEiaService;

    @Mock
    private TestnorgeSigrunService testnorgeSigrunService;

    @Mock
    private TestnorgeAaregService testnorgeAaregService;

    @Mock
    private TestnorgeInstService testnorgeInstService;

    @Mock
    private TestnorgeBisysService testnorgeBisysService;

    @Mock
    private TestnorgeTpService testnorgeTpService;

    @Mock
    private TesnorgeArenaService tesnorgeArenaService;

    @Mock
    private TestnorgeMedlService testnorgeMedlService;

    @InjectMocks
    private SyntetiseringsController syntetiseringsController;

    private Long avspillergruppeId = 100000445L;
    private String miljoe = "t9";

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å opprette skd-meldinger, skal metoden kalle på
     * {@link TestnorgeSkdService#genererSkdmeldinger}
     */
    @Test
    public void shouldProduceSkdmeldinger() {
        Map<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put("0110", 20);

        var syntetiserSkdmeldingerRequest = new SyntetiserSkdmeldingerRequest(avspillergruppeId,
                miljoe,
                antallMeldingerPerEndringskode);

        syntetiseringsController.opprettSkdmeldingerITPS(syntetiserSkdmeldingerRequest);

        verify(testnorgeSkdService).genererSkdmeldinger(avspillergruppeId, miljoe, antallMeldingerPerEndringskode);
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å opprette nav-endringsmeldinger, skal metoden kalle på
     * {@link TestnorgeSkdService#genererNavmeldinger}
     */
    @Test
    public void shouldProduceNavmeldinger() {
        Map<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put("Z010", 20);

        var syntetiserNavmeldingerRequest = new SyntetiserNavmeldingerRequest(avspillergruppeId,
                miljoe,
                antallMeldingerPerEndringskode);

        syntetiseringsController.opprettNavmeldingerITPS(syntetiserNavmeldingerRequest);

        verify(testnorgeSkdService).genererNavmeldinger(syntetiserNavmeldingerRequest);
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å opprette inntektsmeldinger, skal metoden kalle på
     * {@link TestnorgeInntektService#genererInntektsmeldinger}
     */
    @Test
    public void shouldProduceInntektsmeldinger() {
        var syntetiserInntektsmeldingRequest = new SyntetiserInntektsmeldingRequest(avspillergruppeId);

        syntetiseringsController.opprettSyntetiskInntektsmeldingIInntektstub(syntetiserInntektsmeldingRequest);

        verify(testnorgeInntektService).genererInntektsmeldinger(syntetiserInntektsmeldingRequest);
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å generere sykemeldinger til EIA, skal metoden kalle på
     * {@link TestnorgeEiaService#genererEiaSykemeldinger}.
     */
    @Test
    public void shouldTriggerGenereringAvSykemeldingerIEia() {
        var antallMeldinger = 20;

        var syntetiserEiaRequest = new SyntetiserEiaRequest(avspillergruppeId, miljoe, antallMeldinger);

        syntetiseringsController.opprettSykemeldingerIEia(syntetiserEiaRequest);

        verify(testnorgeEiaService).genererEiaSykemeldinger(syntetiserEiaRequest);
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å generere skattegrunnlag i sigrun, skal metoden kalle på
     * {@link TestnorgeSigrunService#genererSkattegrunnlag}.
     */
    @Test
    public void shouldProduceSkattegrunnlagISigrun() {
        var antallNyeIdenter = 20;

        var syntetiserPoppRequest = new SyntetiserPoppRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        syntetiseringsController.opprettSkattegrunnlagISigrun("test", syntetiserPoppRequest);

        verify(testnorgeSigrunService).genererSkattegrunnlag(syntetiserPoppRequest, "test");
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å generere arbeidsforhold i aareg, skal metoden kalle på
     * {@link TestnorgeAaregService#genererArbeidsforholdsmeldinger}.
     */
    @Test
    public void shouldProduceArbeidsforholdIAareg() {
        var antallNyeIdenter = 20;

        var syntetiserAaregRequest = new SyntetiserAaregRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        syntetiseringsController.opprettArbeidsforholdIAareg(false, syntetiserAaregRequest);

        verify(testnorgeAaregService).genererArbeidsforholdsmeldinger(syntetiserAaregRequest, false);
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å generere institusjonsforhold i inst, skal metoden kalle på
     * {@link TestnorgeInstService#genererInstitusjonsforhold}.
     */
    @Test
    public void shouldProduceInstitusjonsmeldingIInst() {
        var antallNyeIdenter = 20;

        var syntetiserInstRequest = new SyntetiserInstRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        syntetiseringsController.opprettInstitutjonsforholdIInst(syntetiserInstRequest);

        verify(testnorgeInstService).genererInstitusjonsforhold(syntetiserInstRequest);
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å generere bistandsforhold i bisys, skal metoden kalle på
     * {@link TestnorgeBisysService#genererBistandsmeldinger}.
     */
    @Test
    public void shouldProduceBistandsmeldingerInBisys() {
        var antallNyeIdenter = 2;

        var syntetiserBisysRequest = new SyntetiserBisysRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        syntetiseringsController.opprettBistandsmeldingerIBisys(syntetiserBisysRequest);

        verify(testnorgeBisysService).genererBistandsmeldinger(syntetiserBisysRequest);
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får en request om å generere ytelser i tjpen skal metoden kalle på
     * {@link TestnorgeTpService#genererTp}
     */
    @Test
    public void shouldProduceYtelserInTp() {
        var antallIdenter = 10;
        var request = new SyntetiserTpRequest(avspillergruppeId, miljoe, antallIdenter);
        syntetiseringsController.opprettYtelserITp(request);
        verify(testnorgeTpService).genererTp(request);
    }

    @Test
    public void shouldOppretteArbeidssoekereIArena() {
        var syntetiserArenaRequest = new SyntetiserArenaRequest(avspillergruppeId, miljoe, 2);
        syntetiseringsController.opprettArbeidssoekereIArena(syntetiserArenaRequest);

        verify(tesnorgeArenaService).opprettArbeidssokereIArena(syntetiserArenaRequest);
    }

    @Test
    public void shouldProduceMedlemskapInMedl() {
        var syntetiserMedlRequest = new SyntetiserMedlRequest(avspillergruppeId, miljoe, 0.1);
        syntetiseringsController.opprettMedlemskapIMedl(syntetiserMedlRequest);

        verify(testnorgeMedlService).genererMedlemskap(syntetiserMedlRequest);
    }
}
