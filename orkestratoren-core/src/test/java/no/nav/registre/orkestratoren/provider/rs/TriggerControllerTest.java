package no.nav.registre.orkestratoren.provider.rs;

import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.orkestratoren.service.TpsSyntPakkenConsumer;

@RunWith(MockitoJUnitRunner.class)
public class TriggerControllerTest {

    @InjectMocks
    private TriggerController triggerController;

    @Mock
    private TpsSyntPakkenConsumer tpsSyntPakkenConsumer;

    @Test
    public void opprettSkdMeldinger() {
        int antallSkdMeldinger = 20;

        List<String> miljoer = new ArrayList<>();
        miljoer.add("u2");
        miljoer.add("u6");

        List<String> aarsakskoder = new ArrayList<>();
        aarsakskoder.add("01");
        aarsakskoder.add("02");

        TriggerRequest triggerRequest = new TriggerRequest(antallSkdMeldinger, miljoer, aarsakskoder);

        triggerController.opprettSkdMeldinger(triggerRequest);

        verify(tpsSyntPakkenConsumer).produserOgSendSkdmeldingerTilTpsIMiljoer(antallSkdMeldinger, miljoer, aarsakskoder);
    }
}
