package no.nav.registre.arena.core.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.registre.arena.core.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.TilleggSyntConsumer;
import no.nav.registre.arena.core.service.util.ServiceUtils;
import no.nav.registre.arena.domain.vedtak.NyttVedtakResponse;
import no.nav.registre.arena.domain.vedtak.NyttVedtakTillegg;

@RunWith(MockitoJUnitRunner.class)
public class RettighetTilleggServiceTest {

    @Mock
    private TilleggSyntConsumer tilleggSyntConsumer;

    @Mock
    private RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;

    @Mock
    private RettighetTiltakService rettighetTiltakService;

    @Mock
    private ServiceUtils serviceUtils;

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
    public void shouldOppretteTilleggsstoenadLaeremidler() {
        when(tilleggSyntConsumer.opprettLaeremidler(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(Collections.singletonList(new NyttVedtakResponse()));
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(Collections.singletonList(new NyttVedtakResponse()));

        rettighetTilleggService.opprettTilleggsstoenadLaeremidler(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettLaeremidler(antallNyeIdenter);
        verify(serviceUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        verify(serviceUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadBoutgifter() {
        when(tilleggSyntConsumer.opprettBoutgifter(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(Collections.singletonList(new NyttVedtakResponse()));
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(Collections.singletonList(new NyttVedtakResponse()));

        rettighetTilleggService.opprettTilleggsstoenadBoutgifter(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettBoutgifter(antallNyeIdenter);
        verify(serviceUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        verify(serviceUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadFlytting() {
        when(tilleggSyntConsumer.opprettFlytting(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(Collections.singletonList(new NyttVedtakResponse()));
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(Collections.singletonList(new NyttVedtakResponse()));

        rettighetTilleggService.opprettTilleggsstoenadFlytting(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettFlytting(antallNyeIdenter);
        verify(serviceUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        verify(serviceUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadHjemreise() {
        when(tilleggSyntConsumer.opprettHjemreise(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(Collections.singletonList(new NyttVedtakResponse()));
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(Collections.singletonList(new NyttVedtakResponse()));

        rettighetTilleggService.opprettTilleggsstoenadHjemreise(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettHjemreise(antallNyeIdenter);
        verify(serviceUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        verify(serviceUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadReisestoenadArbeidssoekere() {
        when(tilleggSyntConsumer.opprettReisestoenadArbeidssoekere(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(Collections.singletonList(new NyttVedtakResponse()));
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(Collections.singletonList(new NyttVedtakResponse()));

        rettighetTilleggService.opprettTilleggsstoenadReisestoenadArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettReisestoenadArbeidssoekere(antallNyeIdenter);
        verify(serviceUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        verify(serviceUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadBoutgifterArbeidssoekere() {
        when(tilleggSyntConsumer.opprettBoutgifterArbeidssoekere(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(Collections.singletonList(new NyttVedtakResponse()));
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(Collections.singletonList(new NyttVedtakResponse()));

        rettighetTilleggService.opprettTilleggsstoenadBoutgifterArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettBoutgifterArbeidssoekere(antallNyeIdenter);
        verify(serviceUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        verify(serviceUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadDagligReise() {
        when(tilleggSyntConsumer.opprettDagligReise(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(Collections.singletonList(new NyttVedtakResponse()));
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(Collections.singletonList(new NyttVedtakResponse()));

        rettighetTilleggService.opprettTilleggsstoenadDagligReise(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettDagligReise(antallNyeIdenter);
        verify(serviceUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        verify(serviceUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadReiseObligatoriskSamling() {
        when(tilleggSyntConsumer.opprettReiseObligatoriskSamling(antallNyeIdenter)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(Collections.singletonList(new NyttVedtakResponse()));
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(Collections.singletonList(new NyttVedtakResponse()));

        rettighetTilleggService.opprettTilleggsstoenadReiseObligatoriskSamling(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettReiseObligatoriskSamling(antallNyeIdenter);
        verify(serviceUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        verify(serviceUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }
}