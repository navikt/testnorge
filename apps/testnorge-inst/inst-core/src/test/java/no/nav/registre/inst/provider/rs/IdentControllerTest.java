package no.nav.registre.inst.provider.rs;

import no.nav.registre.inst.Institusjonsopphold;
import no.nav.registre.inst.service.IdentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class IdentControllerTest {

    @Mock
    private IdentService identService;

    @InjectMocks
    private IdentController identController;

    private String id = "test";
    private String miljoe = "t1";
    private Long oppholdId = 123L;
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private List<String> identer;
    private Institusjonsopphold institusjonsopphold = new Institusjonsopphold();

    @Before
    public void setUp() {
        identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));
    }

    @Test
    public void shouldOppretteInstitusjonsopphold() {
        identController.opprettInstitusjonsopphold(id, id, miljoe, institusjonsopphold);

        verify(identService).sendTilInst2(id, id, miljoe, institusjonsopphold);
    }

    @Test
    public void shouldHenteInstitusjonsopphold() {
        identController.hentInstitusjonsopphold(id, id, miljoe, identer);

        verify(identService).hentOppholdTilIdenter(id, id, miljoe, identer);
    }

    @Test
    public void shouldOppdatereInstitusjonsopphold() {
        identController.oppdaterInstitusjonsopphold(oppholdId, id, id, miljoe, institusjonsopphold);

        verify(identService).oppdaterInstitusjonsopphold(id, id, miljoe, oppholdId, institusjonsopphold);
    }

    @Test
    public void shouldOppretteFlereInstitusjonsopphold() {
        List<Institusjonsopphold> institusjonsoppholdene = new ArrayList<>(Collections.singletonList(institusjonsopphold));
        identController.opprettFlereInstitusjonsopphold(id, id, miljoe, institusjonsoppholdene);

        verify(identService).opprettInstitusjonsopphold(id, id, miljoe, institusjonsoppholdene);
    }

    @Test
    public void shouldSletteIdenter() {
        identController.slettIdenter(id, id, miljoe, identer);

        verify(identService).slettInstitusjonsoppholdTilIdenter(id, id, miljoe, identer);
    }
}