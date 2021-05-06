package no.nav.registre.testnorge.arena.service;

import no.nav.registre.testnorge.arena.consumer.rs.BrukereArenaForvalterConsumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class IdentServiceTest {

    @Mock
    private BrukereArenaForvalterConsumer brukereArenaForvalterConsumer;

    @InjectMocks
    private IdentService identService;


    @Test
    public void slettBrukereTest() {
        String miljoe = "q2";
        String fnr2 = "20202020202";
        String fnr3 = "30303030303";
        String fnr1 = "10101010101";

        doReturn(true).when(brukereArenaForvalterConsumer).slettBruker(fnr2, miljoe);
        doReturn(true).when(brukereArenaForvalterConsumer).slettBruker(fnr3, miljoe);

        List<String> slettedeIdenter = identService.slettBrukereIArenaForvalter(Arrays.asList(fnr1, fnr2, fnr3), miljoe);

        assertThat(slettedeIdenter).hasSize(2).contains(fnr2).doesNotContain(fnr1);
    }
}
