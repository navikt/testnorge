package no.nav.registre.orkestratoren.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSkdConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TpsfConsumer;
import no.nav.registre.orkestratoren.exceptions.HttpStatusCodeExceptionContainer;

@RunWith(MockitoJUnitRunner.class)
public class TpsSyntPakkenServiceTest {

    @Mock
    private TpsfConsumer tpsfConsumer;
    @Mock
    private TestnorgeSkdConsumer testnorgeSkdConsumer;
    @InjectMocks
    private TpsSyntPakkenService tpsSyntPakkenService;
    @Mock
    private HttpStatusCodeExceptionContainer httpStatusCodeException;

    /**
     * Testscenario: Dersom Testnorge-Skd kaster feilmelding og varsler at Ids for lagrede meldinger i TPSF er tom, så skal ikke
     * testnorge-skd kalle på endepunktet sendToTps hos TPSF.
     * <p>
     * Flere scenarier for denne metoden dekkes av komponenttesten
     * {@link no.nav.registre.orkestratoren.StartSyntetiseringTpsCompTest}
     */
    @Test(expected = HttpStatusCodeExceptionContainer.class)
    public void shouldNotCallTpsfIfIdsIsEmpty() {
        // when(httpStatusCodeException.getResponseBodyAsString()).thenReturn("{\"ids\":[]}");
        when(testnorgeSkdConsumer.startSyntetisering(any())).thenThrow(httpStatusCodeException);
        try {
            tpsSyntPakkenService.produserOgSendSkdmeldingerTilTpsIMiljoer(123L, "u6", new HashMap<>());
        } finally {
            verifyZeroInteractions(tpsfConsumer);
        }
    }

    @Test(expected = HttpStatusCodeExceptionContainer.class)
    public void serverErrorFromTpsfTest() {
        when(testnorgeSkdConsumer.startSyntetisering(any())).thenReturn(new ArrayList<Long>() {
            {
                add(1L);
                add(2L);
                add(3L);
                add(4L);
            }
        });

        when(tpsfConsumer.sendSkdmeldingerTilTps(any(), any())).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        tpsSyntPakkenService.produserOgSendSkdmeldingerTilTpsIMiljoer(123L, "u6", new HashMap<>());
    }
}