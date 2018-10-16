package no.nav.registre.orkestratoren.provider.rs;

import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.orkestratoren.service.TpsSyntPakkenConsumer;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringsControllerTest {

    @InjectMocks
    private SyntetiseringsController syntetiseringsController;

    @Mock
    private TpsSyntPakkenConsumer tpsSyntPakkenConsumer;

    @Test
    public void opprettSkdMeldinger() {
        long skdMeldingGruppeId = 100000445L;

        String miljoe = "t9";

        Map<String, Integer> antallMeldingerPerAarsakskode = new HashMap<>();
        antallMeldingerPerAarsakskode.put("01", 20);

        SyntetiserSkdmeldingerRequest syntetiserSkdmeldingerRequest = new SyntetiserSkdmeldingerRequest(skdMeldingGruppeId,
                miljoe,
                antallMeldingerPerAarsakskode);

        syntetiseringsController.opprettSkdMeldinger(syntetiserSkdmeldingerRequest);

        verify(tpsSyntPakkenConsumer).produserOgSendSkdmeldingerTilTpsIMiljoer(skdMeldingGruppeId, miljoe, antallMeldingerPerAarsakskode);
    }
}
