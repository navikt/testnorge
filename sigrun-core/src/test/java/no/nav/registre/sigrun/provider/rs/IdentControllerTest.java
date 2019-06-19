package no.nav.registre.sigrun.provider.rs;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.registre.sigrun.service.SigrunService;

@RunWith(MockitoJUnitRunner.class)
public class IdentControllerTest {

    @Mock
    private SigrunService sigrunService;

    @InjectMocks
    private IdentController identController;

    private String testdataEier = "test";
    private String miljoe = "t1";

    @Test
    public void shouldDeleteIdentsFromSigrun() {
        List<String> identer = new ArrayList<>(Collections.singletonList("01010101010"));
        identController.slettIdenterFraSigrun(testdataEier, miljoe, identer);

        verify(sigrunService).slettSkattegrunnlagTilIdenter(identer, testdataEier, miljoe);
    }
}