package no.nav.registre.inst.provider.rs;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.inst.service.IdentService;

@RunWith(MockitoJUnitRunner.class)
public class IdentControllerTest {

    @Mock
    private IdentService identService;

    @InjectMocks
    private IdentController identController;

    @Test
    public void shouldDeleteIdents() {
        String id = "test";
        List<String> identer = new ArrayList<>(Arrays.asList("01010101010", "02020202020"));
        identController.slettIdenterFraInst2(id, id, identer);

        verify(identService).slettInstitusjonsforholdTilIdenter(identer, id, id);
    }
}