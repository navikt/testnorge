package no.nav.registre.endringsmeldinger.provider.rs;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.transform.TransformerException;

import no.nav.registre.endringsmeldinger.provider.rs.requests.GenererKontonummerRequest;
import no.nav.registre.endringsmeldinger.service.TestdataService;

@RunWith(MockitoJUnitRunner.class)
public class TestdataControllerTest {

    @Mock
    private TestdataService testdataService;

    @InjectMocks
    private TestdataController testdataController;

    private String koeNavn = "testkoe";
    private GenererKontonummerRequest genererKontonummerRequest;

    @Before
    public void setUp() {
        genererKontonummerRequest = GenererKontonummerRequest.builder().build();
    }

    @Test
    public void shouldGenerereKontonummerPaaIdenter() throws TransformerException {
        testdataController.genererKontonummerPaaIdenter(koeNavn, genererKontonummerRequest);
        verify(testdataService).genererKontonummerOgSendTilTps(koeNavn, genererKontonummerRequest);
    }
}