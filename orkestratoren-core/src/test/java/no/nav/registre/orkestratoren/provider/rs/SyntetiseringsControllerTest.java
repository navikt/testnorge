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
import no.nav.registre.orkestratoren.service.AaregSyntPakkenService;
import no.nav.registre.orkestratoren.service.InntektSyntPakkenService;
import no.nav.registre.orkestratoren.service.ArenaSyntPakkenService;
import no.nav.registre.orkestratoren.service.BisysSyntPakkenService;
import no.nav.registre.orkestratoren.service.EiaSyntPakkenService;
import no.nav.registre.orkestratoren.service.InstSyntPakkenService;
import no.nav.registre.orkestratoren.service.MedlSyntPakkenService;
import no.nav.registre.orkestratoren.service.PoppSyntPakkenService;
import no.nav.registre.orkestratoren.service.TpSyntPakkenService;
import no.nav.registre.orkestratoren.service.TpsSyntPakkenService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringsControllerTest {

    @Mock
    private TpsSyntPakkenService tpsSyntPakkenService;

    @Mock
    private InntektSyntPakkenService inntektSyntPakkenService;

    @Mock
    private EiaSyntPakkenService eiaSyntPakkenService;

    @Mock
    private PoppSyntPakkenService poppSyntPakkenService;

    @Mock
    private AaregSyntPakkenService aaregSyntPakkenService;

    @Mock
    private InstSyntPakkenService instSyntPakkenService;

    @Mock
    private BisysSyntPakkenService bisysSyntPakkenService;

    @Mock
    private TpSyntPakkenService tpSyntPakkenService;

    @Mock
    private ArenaSyntPakkenService arenaSyntPakkenService;

    @Mock
    private MedlSyntPakkenService medlSyntPakkenService;

    @InjectMocks
    private SyntetiseringsController syntetiseringsController;

    private Long avspillergruppeId = 100000445L;
    private String miljoe = "t9";

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å opprette skd-meldinger, skal metoden kalle på
     * {@link TpsSyntPakkenService#genererSkdmeldinger}
     */
    @Test
    public void shouldProduceSkdmeldinger() {
        Map<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put("0110", 20);

        SyntetiserSkdmeldingerRequest syntetiserSkdmeldingerRequest = new SyntetiserSkdmeldingerRequest(avspillergruppeId,
                miljoe,
                antallMeldingerPerEndringskode);

        syntetiseringsController.opprettSkdmeldingerITPS(syntetiserSkdmeldingerRequest);

        verify(tpsSyntPakkenService).genererSkdmeldinger(avspillergruppeId, miljoe, antallMeldingerPerEndringskode);
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å opprette nav-endringsmeldinger, skal metoden kalle på
     * {@link TpsSyntPakkenService#genererNavmeldinger}
     */
    @Test
    public void shouldProduceNavmeldinger() {
        Map<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put("Z010", 20);

        SyntetiserNavmeldingerRequest syntetiserNavmeldingerRequest = new SyntetiserNavmeldingerRequest(avspillergruppeId,
                miljoe,
                antallMeldingerPerEndringskode);

        syntetiseringsController.opprettNavmeldingerITPS(syntetiserNavmeldingerRequest);

        verify(tpsSyntPakkenService).genererNavmeldinger(syntetiserNavmeldingerRequest);
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å opprette inntektsmeldinger, skal metoden kalle på
     * {@link InntektSyntPakkenService#genererInntektsmeldinger}
     */
    @Test
    public void shouldProduceInntektsmeldinger() {
        SyntetiserInntektsmeldingRequest syntetiserInntektsmeldingRequest = new SyntetiserInntektsmeldingRequest(avspillergruppeId);

        syntetiseringsController.opprettSyntetiskInntektsmeldingIInntektstub(syntetiserInntektsmeldingRequest);

        verify(inntektSyntPakkenService).genererInntektsmeldinger(syntetiserInntektsmeldingRequest);
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å generere sykemeldinger til EIA, skal metoden kalle på
     * {@link EiaSyntPakkenService#genererEiaSykemeldinger}.
     */
    @Test
    public void shouldTriggerGenereringAvSykemeldingerIEia() {
        int antallMeldinger = 20;

        SyntetiserEiaRequest syntetiserEiaRequest = new SyntetiserEiaRequest(avspillergruppeId, miljoe, antallMeldinger);

        syntetiseringsController.opprettSykemeldingerIEia(syntetiserEiaRequest);

        verify(eiaSyntPakkenService).genererEiaSykemeldinger(syntetiserEiaRequest);
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å generere skattegrunnlag i sigrun, skal metoden kalle på
     * {@link PoppSyntPakkenService#genererSkattegrunnlag}.
     */
    @Test
    public void shouldProduceSkattegrunnlagISigrun() {
        int antallNyeIdenter = 20;

        SyntetiserPoppRequest syntetiserPoppRequest = new SyntetiserPoppRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        syntetiseringsController.opprettSkattegrunnlagISigrun("test", syntetiserPoppRequest);

        verify(poppSyntPakkenService).genererSkattegrunnlag(syntetiserPoppRequest, "test");
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å generere arbeidsforhold i aareg, skal metoden kalle på
     * {@link AaregSyntPakkenService#genererArbeidsforholdsmeldinger}.
     */
    @Test
    public void shouldProduceArbeidsforholdIAareg() {
        int antallNyeIdenter = 20;

        SyntetiserAaregRequest syntetiserAaregRequest = new SyntetiserAaregRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        syntetiseringsController.opprettArbeidsforholdIAareg(false, syntetiserAaregRequest);

        verify(aaregSyntPakkenService).genererArbeidsforholdsmeldinger(syntetiserAaregRequest, false);
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å generere institusjonsforhold i inst, skal metoden kalle på
     * {@link InstSyntPakkenService#genererInstitusjonsforhold}.
     */
    @Test
    public void shouldProduceInstitusjonsmeldingIInst() {
        int antallNyeIdenter = 20;

        SyntetiserInstRequest syntetiserInstRequest = new SyntetiserInstRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        syntetiseringsController.opprettInstitutjonsforholdIInst(syntetiserInstRequest);

        verify(instSyntPakkenService).genererInstitusjonsforhold(syntetiserInstRequest);
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å generere bistandsforhold i bisys, skal metoden kalle på
     * {@link BisysSyntPakkenService#genererBistandsmeldinger}.
     */
    @Test
    public void shouldProduceBistandsmeldingerInBisys() {
        int antallNyeIdenter = 2;

        SyntetiserBisysRequest syntetiserBisysRequest = new SyntetiserBisysRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        syntetiseringsController.opprettBistandsmeldingerIBisys(syntetiserBisysRequest);

        verify(bisysSyntPakkenService).genererBistandsmeldinger(syntetiserBisysRequest);
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får en request om å generere ytelser i tjpen skal metoden kalle på
     * {@link TpSyntPakkenService#genererTp}
     */
    @Test
    public void shouldProduceYtelserInTp() {
        int antallIdenter = 10;
        SyntetiserTpRequest request = new SyntetiserTpRequest(avspillergruppeId, miljoe, antallIdenter);
        syntetiseringsController.opprettYtelserITp(request);
        verify(tpSyntPakkenService).genererTp(request);
    }

    @Test
    public void asd() {
        SyntetiserArenaRequest syntetiserArenaRequest = new SyntetiserArenaRequest(avspillergruppeId, miljoe, 2);
        syntetiseringsController.opprettArbeidssoekereIArena(syntetiserArenaRequest);

        verify(arenaSyntPakkenService).opprettArbeidssokereIArena(syntetiserArenaRequest);
    }

    @Test
    public void shouldProduceMedlemskapInMedl() {
        SyntetiserMedlRequest syntetiserMedlRequest = new SyntetiserMedlRequest(avspillergruppeId, miljoe, 0.1);
        syntetiseringsController.opprettMedlemskapIMedl(syntetiserMedlRequest);

        verify(medlSyntPakkenService).genererMedlemskap(syntetiserMedlRequest);
    }
}
