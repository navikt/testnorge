package no.nav.registre.orkestratoren.provider.rs;

import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserEiaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSkdmeldingerRequest;
import no.nav.registre.orkestratoren.service.ArenaInntektSyntPakkenService;
import no.nav.registre.orkestratoren.service.EiaSyntPakkenService;
import no.nav.registre.orkestratoren.service.TpsSyntPakkenService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringsControllerTest {

    @Mock
    private TpsSyntPakkenService tpsSyntPakkenService;

    @Mock
    private ArenaInntektSyntPakkenService arenaInntektSyntPakkenService;

    @Mock
    private EiaSyntPakkenService eiaSyntPakkenService;

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
     * Scenario: HVIS syntetiseringskontrolleren får et request om å opprette inntektsmeldinger, skal metoden kalle på
     * {@link ArenaInntektSyntPakkenService#genererInntektsmeldinger}
     */
    @Test
    public void shouldProduceInntektsmeldinger() {
        SyntetiserInntektsmeldingRequest syntetiserInntektsmeldingRequest = new SyntetiserInntektsmeldingRequest(avspillergruppeId);

        syntetiseringsController.opprettSyntetiskInntektsmeldingIInntektstub(syntetiserInntektsmeldingRequest);

        verify(arenaInntektSyntPakkenService).genererInntektsmeldinger(syntetiserInntektsmeldingRequest);
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
}
