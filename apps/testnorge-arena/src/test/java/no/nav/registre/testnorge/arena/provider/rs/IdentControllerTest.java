package no.nav.registre.testnorge.arena.provider.rs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import no.nav.registre.testnorge.arena.service.IdentService;

@RunWith(MockitoJUnitRunner.class)
public class IdentControllerTest {

    @Mock
    private IdentService identService;

    @InjectMocks
    private IdentController identController;

    private String miljoe = "q2";
    private String eier = "ORKESTRATOREN";
    private String fnr1 = "10101010101";
    private String fnr2 = "20202020202";
    private String fnr3 = "30303030303";
    private String fnr4 = "40404040404";
    private Arbeidsoeker arbeidsoeker;

    @Before
    public void setUp() {
        arbeidsoeker = Arbeidsoeker.builder()
                .personident(fnr1)
                .miljoe(miljoe)
                .eier(eier)
                .build();
    }

    @Test
    public void slettIdenterIArenaForvalter() {
        when(identService
                .slettBrukereIArenaForvalter(Arrays.asList(fnr1, fnr2, fnr3, fnr4), miljoe))
                .thenReturn(Arrays.asList(fnr1, fnr3, fnr4));

        ResponseEntity<List<String>> response = identController.slettBrukereIArenaForvalter(miljoe, Arrays.asList(fnr1, fnr2, fnr3, fnr4));
        assertThat(response.getBody(), is(Arrays.asList(fnr1, fnr3, fnr4)));
    }

    @Test
    public void hentBrukereFraArenaForvalter() {
        when(identService
                .hentArbeidsoekere(eier, miljoe, fnr1))
                .thenReturn(Collections.singletonList(arbeidsoeker));

        ResponseEntity<List<Arbeidsoeker>> response = identController.hentBrukereFraArenaForvalter(eier, miljoe, fnr1);
        assertThat(response.getBody().size(), is(1));
        assertThat(response.getBody().get(0).getPersonident(), is(fnr1));
    }

}
