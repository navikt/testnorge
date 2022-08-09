package no.nav.testnav.apps.instservice.controller;

import no.nav.testnav.apps.instservice.domain.InstitusjonsoppholdV2;
import no.nav.testnav.apps.instservice.provider.IdentController;
import no.nav.testnav.apps.instservice.service.IdentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class IdentControllerTest {

    @MockBean
    public JwtDecoder jwtDecoder;

    @Mock
    private IdentService identService;

    @InjectMocks
    private IdentController identController;

    private final String miljoe = "t1";
    private List<String> identer;
    private final InstitusjonsoppholdV2 institusjonsopphold = new InstitusjonsoppholdV2();

    @Before
    public void setUp() {
        String fnr1 = "01010101010";
        String fnr2 = "02020202020";
        identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));
    }

    @Test
    public void shouldOppretteInstitusjonsopphold() {
        identController.opprettInstitusjonsopphold(miljoe, institusjonsopphold);

        verify(identService).sendTilInst2(miljoe, institusjonsopphold);
    }

    @Test
    public void shouldHenteInstitusjonsopphold() {
        identController.hentInstitusjonsopphold(miljoe, identer);

        verify(identService).hentOppholdTilIdenter(miljoe, identer);
    }

    @Test
    public void shouldOppretteFlereInstitusjonsopphold() {
        List<InstitusjonsoppholdV2> institusjonsoppholdene = new ArrayList<>(Collections.singletonList(institusjonsopphold));
        identController.opprettFlereInstitusjonsopphold(miljoe, institusjonsoppholdene);

        verify(identService).opprettInstitusjonsopphold(miljoe, institusjonsoppholdene);
    }

    @Test
    public void shouldSletteIdenter() {
        identController.slettIdenter(miljoe, identer);

        verify(identService).slettInstitusjonsoppholdTilIdenter(miljoe, identer);
    }
}