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
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSkdmeldingerRequest;
import no.nav.registre.orkestratoren.service.EiaSyntPakkenService;
import no.nav.registre.orkestratoren.service.TpsSyntPakkenService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringsControllerTest {

    @InjectMocks
    private SyntetiseringsController syntetiseringsController;

    @Mock
    private TpsSyntPakkenService tpsSyntPakkenService;

    @Mock
    private EiaSyntPakkenService eiaSyntPakkenService;

    private Long skdMeldingGruppeId = 100000445L;
    private String miljoe = "t9";

    /**
     * Scenario: HVIS syntetiseringskontrolleren får et request om å oppretteSkdMeldinger, skal metoden kalle på
     * {@link TpsSyntPakkenService#produserOgSendSkdmeldingerTilTpsIMiljoer}
     */
    @Test
    public void shouldProduceAndSendSkdmeldingerToTpsIMiljoer() {
        Map<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put("0110", 20);

        SyntetiserSkdmeldingerRequest syntetiserSkdmeldingerRequest = new SyntetiserSkdmeldingerRequest(skdMeldingGruppeId,
                miljoe,
                antallMeldingerPerEndringskode);

        syntetiseringsController.opprettSkdMeldingerOgSendTilTps(syntetiserSkdmeldingerRequest);

        verify(tpsSyntPakkenService).produserOgSendSkdmeldingerTilTpsIMiljoer(skdMeldingGruppeId, miljoe, antallMeldingerPerEndringskode);
    }

    @Test
    public void shouldTriggerGenereringAvSykemeldingerIEia() {
        int antallMeldinger = 20;

        SyntetiserEiaRequest syntetiserEiaRequest = new SyntetiserEiaRequest(skdMeldingGruppeId, miljoe, antallMeldinger);

        syntetiseringsController.genererSykemeldingerIEia(syntetiserEiaRequest);

        verify(eiaSyntPakkenService).genererEiaSykemeldinger(syntetiserEiaRequest);
    }
}
