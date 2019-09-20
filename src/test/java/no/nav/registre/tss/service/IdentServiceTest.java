package no.nav.registre.tss.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.JMSException;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@RunWith(MockitoJUnitRunner.class)
public class IdentServiceTest {

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private JmsService jmsService;

    @InjectMocks
    private IdentService identService;

    private String koeNavn = "testKoe";
    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 2;
    private String fnr1 = "23026325811";
    private String navn1 = "GLOBUS IHERDIG STRAFFET";
    private String fnr2 = "08016325431";
    private String navn2 = "BORD OPPSTEMT ELEKTRONISK";

    @Test
    public void shouldHenteLegerIAvspillergruppeFraTss() throws JMSException {
        List<String> identer = Arrays.asList(fnr1, fnr2);

        when(jmsService.hentKoeNavnSamhandler(miljoe)).thenReturn(koeNavn);
        when(hodejegerenConsumer.getLevende(avspillergruppeId)).thenReturn(identer);
        identService.hentLegerIAvspillergruppeFraTss(avspillergruppeId, antallNyeIdenter, miljoe);

        verify(hodejegerenConsumer).getLevende(avspillergruppeId);
        verify(jmsService).sendOgMotta910RutineFraTss(identer, koeNavn);
    }
}