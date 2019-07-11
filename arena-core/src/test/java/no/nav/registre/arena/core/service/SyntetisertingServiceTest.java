package no.nav.registre.arena.core.service;


import no.nav.registre.arena.core.consumer.rs.ArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.HodejegerenConsumer;
import no.nav.registre.arena.core.consumer.rs.responses.Arbeidsoker;
import no.nav.registre.arena.core.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.arena.domain.NyBruker;
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
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SyntetisertingServiceTest {
    @Mock
    private HodejegerenConsumer hodejegerenConsumer;
    @Mock
    private ArenaForvalterConsumer arenaForvalterConsumer;
    @Mock
    private Random random;

    @InjectMocks
    private SyntetiseringService syntetiseringService;

    private SyntetiserArenaRequest arenaRequest;
    private Long avspillergruppeId = 10L;
    private String miljoe = "q2";
    private String fnr1 = "10101010101";
    private String fnr2 = "20202020202";
    private String fnr3 = "30303030303";

    private List<String> identerOverAlder = new ArrayList<>(Arrays.asList(fnr1, fnr2));
    private List<String> eksisterendeIdenter = new ArrayList<>(Arrays.asList(fnr1, fnr3));
    private List<NyBruker> nyeBrukere = Collections.singletonList(new NyBruker(
            fnr2,
            miljoe,
            "IKVAL",
            null,
            true,
            null,
            null));
    private List<Arbeidsoker> nyeArbeisokere = Collections.singletonList(new Arbeidsoker(
            fnr2,
            miljoe,
            "OK",
            "ORKESTRATOREN",
            true,
            true
    ));

    @Before
    public void setUp() {
        arenaRequest = new SyntetiserArenaRequest(avspillergruppeId, miljoe, 1);
    }

    // TODO: sjekk noen farlige muterbare operasjoner med removeAll osv. kan hende man ikke bare muterer en kopi av listene..
    @Test
    public void hentGyldigeIdenterTest() {
        when(hodejegerenConsumer
                .finnLevendeIdenterOverAlder(avspillergruppeId))
                .thenReturn(identerOverAlder);
        when(arenaForvalterConsumer
                .hentEksisterendeIdenter())
                .thenReturn(eksisterendeIdenter);
        when(arenaForvalterConsumer
                .sendTilArenaForvalter(nyeBrukere))
                .thenReturn(nyeArbeisokere);

        List<Arbeidsoker> nyeIdenter = syntetiseringService.sendBrukereTilArenaForvalterConsumer(arenaRequest);
        assertThat(nyeIdenter.size(), is(1));
    }

}
