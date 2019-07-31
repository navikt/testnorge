package no.nav.registre.inst.provider.rs;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

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

import no.nav.registre.inst.Institusjonsopphold;
import no.nav.registre.inst.service.IdentService;

@RunWith(MockitoJUnitRunner.class)
public class IdentControllerTest {

    @Mock
    private IdentService identService;

    @InjectMocks
    private IdentController identController;

    private String id = "test";
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
        identController.opprettInstitusjonsopphold(id, id, institusjonsopphold);

        verify(identService).sendTilInst2(institusjonsopphold, id, id);
    }

    @Test
    public void shouldHenteInstitusjonsopphold() {
        identController.hentInstitusjonsopphold(id, id, identer);

        verify(identService).hentOppholdTilIdenter(identer, id, id);
    }

    @Test
    public void shouldOppdatereInstitusjonsopphold() {
        identController.oppdaterInstitusjonsopphold(oppholdId, id, id, institusjonsopphold);

        verify(identService).oppdaterInstitusjonsopphold(id, id, oppholdId, institusjonsopphold);
    }

    @Test
    public void shouldSletteInstitusjonsopphold() {
        identController.slettInstitusjonsopphold(id, id, Collections.singletonList(oppholdId));

        verify(identService).slettOppholdMedId(anyMap(), eq(id), eq(id), eq(oppholdId));
    }

    @Test
    public void shouldOppretteFlereInstitusjonsopphold() {
        List<Institusjonsopphold> institusjonsoppholdene = new ArrayList<>(Collections.singletonList(institusjonsopphold));
        identController.opprettFlereInstitusjonsopphold(id, id, institusjonsoppholdene);

        verify(identService).opprettInstitusjonsopphold(institusjonsoppholdene, id, id);
    }

    @Test
    public void shouldSletteIdenter() {
        identController.slettIdenter(id, id, identer);

        verify(identService).slettInstitusjonsforholdTilIdenter(identer, id, id);
    }
}