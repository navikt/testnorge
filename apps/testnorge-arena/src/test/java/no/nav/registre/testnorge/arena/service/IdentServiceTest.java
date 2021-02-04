package no.nav.registre.testnorge.arena.service;

import no.nav.registre.testnorge.arena.consumer.rs.BrukereArenaForvalterConsumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class IdentServiceTest {

    @Mock
    private BrukereArenaForvalterConsumer brukereArenaForvalterConsumer;

    @InjectMocks
    private IdentService identService;

    private String miljoe = "q2";
    private String fnr1 = "10101010101";
    private String fnr2 = "20202020202";
    private String fnr3 = "30303030303";

    @Before
    public void setUp() {

    }

    @Test
    public void slettBrukereTest() {
        doReturn(true).when(brukereArenaForvalterConsumer).slettBruker(eq(fnr2), eq(miljoe));
        doReturn(true).when(brukereArenaForvalterConsumer).slettBruker(eq(fnr3), eq(miljoe));

        List<String> slettedeIdenter = identService.slettBrukereIArenaForvalter(Arrays.asList(fnr1, fnr2, fnr3), miljoe);

        assertThat(slettedeIdenter.contains(fnr2), is(true));
        assertThat(slettedeIdenter.contains(fnr1), is(false));
        assertThat(slettedeIdenter.size(), is(2));
    }
}
