package no.nav.registre.orkestratoren.provider.rs;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserFrikortRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserMedlRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;
import no.nav.registre.orkestratoren.service.TestnorgeAaregService;
import no.nav.registre.orkestratoren.service.ArenaService;
import no.nav.registre.orkestratoren.service.TestnorgeFrikortService;
import no.nav.registre.orkestratoren.service.TestnorgeInntektService;
import no.nav.registre.orkestratoren.service.TestnorgeInstService;
import no.nav.registre.orkestratoren.service.TestnorgeMedlService;
import no.nav.registre.orkestratoren.service.TestnorgeSigrunService;
import no.nav.registre.orkestratoren.service.TestnorgeTpService;

@ExtendWith(MockitoExtension.class)
class SyntetiseringsControllerTest {

    @Mock
    private TestnorgeInntektService testnorgeInntektService;
    @Mock
    private TestnorgeSigrunService testnorgeSigrunService;

    @Mock
    private TestnorgeAaregService testnorgeAaregService;

    @Mock
    private TestnorgeInstService testnorgeInstService;

    @Mock
    private TestnorgeTpService testnorgeTpService;

    @Mock
    private ArenaService arenaService;

    @Mock
    private TestnorgeMedlService testnorgeMedlService;

    @Mock
    private TestnorgeFrikortService testnorgeFrikortService;

    @InjectMocks
    private SyntetiseringsController syntetiseringsController;

    private final Long avspillergruppeId = 100000445L;
    private final String miljoe = "t9";


    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å opprette inntektsmeldinger, skal metoden kalle på
     * {@link TestnorgeInntektService#genererInntektsmeldinger}
     */
    @Test
    void shouldProduceInntektsmeldinger() {
        var syntetiserInntektsmeldingRequest = new SyntetiserInntektsmeldingRequest(avspillergruppeId, miljoe);

        syntetiseringsController.opprettSyntetiskInntektsmeldingIInntektstub(syntetiserInntektsmeldingRequest);

        verify(testnorgeInntektService).genererInntektsmeldinger(syntetiserInntektsmeldingRequest);
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å generere skattegrunnlag i sigrun, skal metoden kalle på
     * {@link TestnorgeSigrunService#genererSkattegrunnlag}.
     */
    @Test
    void shouldProduceSkattegrunnlagISigrun() {
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
    void shouldProduceArbeidsforholdIAareg() {
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
    void shouldProduceInstitusjonsmeldingIInst() {
        var antallNyeIdenter = 20;

        var syntetiserInstRequest = new SyntetiserInstRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        syntetiseringsController.opprettInstitutjonsforholdIInst(syntetiserInstRequest);

        verify(testnorgeInstService).genererInstitusjonsforhold(syntetiserInstRequest);
    }

    /**
     * Scenario: HVIS syntetiseringskontrolleren får en request om å generere ytelser i tjpen skal metoden kalle på
     * {@link TestnorgeTpService#genererTp}
     */
    @Test
    void shouldProduceYtelserInTp() {
        var antallIdenter = 10;
        var request = new SyntetiserTpRequest(avspillergruppeId, miljoe, antallIdenter);
        syntetiseringsController.opprettYtelserITp(request);
        verify(testnorgeTpService).genererTp(request);
    }

    @Test
    void shouldOppretteArbeidssoekereMedOppfoelgingIArena() {
        var syntetiserArenaRequest = new SyntetiserArenaRequest(miljoe, 2);
        syntetiseringsController.opprettArbeidssoekereMedOppfoelgingIArena(syntetiserArenaRequest);

        verify(arenaService).opprettArbeidssoekereMedOppfoelgingIArena(syntetiserArenaRequest);
    }

    @Test
    void shouldProduceMedlemskapInMedl() {
        var syntetiserMedlRequest = new SyntetiserMedlRequest(avspillergruppeId, miljoe, 0.1);
        syntetiseringsController.opprettMedlemskapIMedl(syntetiserMedlRequest);

        verify(testnorgeMedlService).genererMedlemskap(syntetiserMedlRequest);
    }

    @Test
    void shouldProduceEgenandelInFrikort() {
        var syntetiserFrikortRequest = new SyntetiserFrikortRequest(avspillergruppeId, miljoe, 2);
        syntetiseringsController.opprettEgenandelerIFrikort(syntetiserFrikortRequest);

        verify(testnorgeFrikortService).genererFrikortEgenmeldinger(syntetiserFrikortRequest);
    }
}
