package no.nav.registre.arena.core.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.registre.arena.core.service.util.ArbeidssoekerUtils;
import no.nav.registre.arena.core.service.util.IdenterUtils;
import no.nav.registre.arena.core.service.util.ServiceUtils;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import no.nav.registre.arena.core.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.TilleggSyntConsumer;

@RunWith(MockitoJUnitRunner.class)
public class RettighetTilleggServiceTest {

    @Mock
    private TilleggSyntConsumer tilleggSyntConsumer;

    @Mock
    private RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;

    @Mock
    private RettighetTiltakService rettighetTiltakService;

    @Mock
    private IdenterUtils identerUtils;

    @Mock
    private ServiceUtils serviceUtils;

    @Mock
    private ArbeidssoekerUtils arbeidssoekerUtils;

    @InjectMocks
    private RettighetTilleggService rettighetTilleggService;

    private Long avspillergruppeId = 1L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 1;
    private List<String> identer;

    @Before
    public void setUp() {
        identer = new ArrayList<>(Collections.singletonList("01010101010"));
    }

    @Test
    public void shouldOppretteTilleggsstoenadBoutgifter() {
        when(tilleggSyntConsumer.opprettBoutgifter(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadBoutgifter(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettBoutgifter(antallNyeIdenter);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadDagligReise() {
        when(tilleggSyntConsumer.opprettDagligReise(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadDagligReise(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettDagligReise(antallNyeIdenter);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadFlytting() {
        when(tilleggSyntConsumer.opprettFlytting(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadFlytting(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettFlytting(antallNyeIdenter);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadLaeremidler() {
        when(tilleggSyntConsumer.opprettLaeremidler(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadLaeremidler(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettLaeremidler(antallNyeIdenter);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadHjemreise() {
        when(tilleggSyntConsumer.opprettHjemreise(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadHjemreise(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettHjemreise(antallNyeIdenter);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadReiseObligatoriskSamling() {
        when(tilleggSyntConsumer.opprettReiseObligatoriskSamling(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadReiseObligatoriskSamling(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettReiseObligatoriskSamling(antallNyeIdenter);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadTilsynBarn() {
        when(tilleggSyntConsumer.opprettTilsynBarn(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadTilsynBarn(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettTilsynBarn(antallNyeIdenter);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadTilsynFamiliemedlemmer() {
        when(tilleggSyntConsumer.opprettTilsynFamiliemedlemmer(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadTilsynFamiliemedlemmer(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettTilsynFamiliemedlemmer(antallNyeIdenter);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadTilsynBarnArbeidssoekere() {
        when(tilleggSyntConsumer.opprettTilsynBarnArbeidssoekere(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadTilsynBarnArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettTilsynBarnArbeidssoekere(antallNyeIdenter);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadTilsynFamiliemedlemmerArbeidssoekere() {
        when(tilleggSyntConsumer.opprettTilsynFamiliemedlemmerArbeidssoekere(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadTilsynFamiliemedlemmerArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettTilsynFamiliemedlemmerArbeidssoekere(antallNyeIdenter);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadBoutgifterArbeidssoekere() {
        when(tilleggSyntConsumer.opprettBoutgifterArbeidssoekere(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadBoutgifterArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettBoutgifterArbeidssoekere(antallNyeIdenter);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadDagligReiseArbeidssoekere() {
        when(tilleggSyntConsumer.opprettDagligReiseArbeidssoekere(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadDagligReiseArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettDagligReiseArbeidssoekere(antallNyeIdenter);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadFlyttingArbeidssoekere() {
        when(tilleggSyntConsumer.opprettFlyttingArbeidssoekere(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadFlyttingArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettFlyttingArbeidssoekere(antallNyeIdenter);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadLaeremidlerArbeidssoekere() {
        when(tilleggSyntConsumer.opprettLaeremidlerArbeidssoekere(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadLaeremidlerArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettLaeremidlerArbeidssoekere(antallNyeIdenter);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadHjemreiseArbeidssoekere() {
        when(tilleggSyntConsumer.opprettHjemreiseArbeidssoekere(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadHjemreiseArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettHjemreiseArbeidssoekere(antallNyeIdenter);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadReiseObligatoriskSamlingArbeidssoekere() {
        when(tilleggSyntConsumer.opprettReiseObligatoriskSamlingArbeidssoekere(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadReiseObligatoriskSamlingArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettReiseObligatoriskSamlingArbeidssoekere(antallNyeIdenter);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadReisestoenadArbeidssoekere() {
        when(tilleggSyntConsumer.opprettReisestoenadArbeidssoekere(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadReisestoenadArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettReisestoenadArbeidssoekere(antallNyeIdenter);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }
}