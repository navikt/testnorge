package no.nav.registre.orkestratoren.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import no.nav.registre.orkestratoren.exceptions.NestedHttpStatusCodeException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.orkestratoren.consumer.rs.HodejegerenConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TpsfConsumer;

@RunWith(MockitoJUnitRunner.class)
public class TpsSyntPakkenServiceTest {

    @Mock
    private TpsfConsumer tpsfConsumer;
    @Mock
    private HodejegerenConsumer hodejegerenConsumer;
    @InjectMocks
    private TpsSyntPakkenService tpsSyntPakkenService;
    @Mock
    private NestedHttpStatusCodeException httpStatusCodeException;

    /**
     * Testscenario: Dersom hodejegeren kaster feilmelding og varsler at Ids for lagrede meldinger i TPSF er tom,
     * så skal ikke hodejegeren kalle på endepunktet sendToTps hos TPSF.
     * <p>
     * Flere scenarier for denne metoden dekkes av komponenttesten {@link no.nav.registre.orkestratoren.StartSyntetiseringTpsCompTest}
     */
    @Test(expected = NestedHttpStatusCodeException.class)
    public void shouldNotCallTpsfIfIdsIsEmpty() {
        when(httpStatusCodeException.getResponseBodyAsString()).thenReturn("{\"ids\":[]}");
        when(hodejegerenConsumer.startSyntetisering(any())).thenThrow(httpStatusCodeException);
        try {
            tpsSyntPakkenService.produserOgSendSkdmeldingerTilTpsIMiljoer(123L, "u6", new HashMap<>());
        } finally {
            verifyZeroInteractions(tpsfConsumer);
        }
    }
}