package no.nav.registre.tss.provider.rs;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.JMSException;

import no.nav.registre.tss.service.IdentService;

@RunWith(MockitoJUnitRunner.class)
public class IdentControllerTest {

    @Mock
    private IdentService identService;

    @InjectMocks
    private IdentController identController;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 2;
    private String fnr1 = "29016644901";

    @Test
    public void shouldHenteLegerFraTss() throws JMSException {
        identController.hentLegerFraTss(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(identService).hentLegerIAvspillergruppeFraTss(avspillergruppeId, antallNyeIdenter, miljoe);
    }

    @Test
    public void shouldHenteLegeFraTss() throws JMSException {
        identController.hentLegeFraTss(fnr1, miljoe);
        verify(identService).hentSamhandlerFraTss(fnr1, miljoe);
    }

}