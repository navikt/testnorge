package no.nav.registre.testnorge.arena.service;

import static no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils.UTFALL_JA;
import static no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils.VEDTAK_TYPE_KODE_O;
import static no.nav.registre.testnorge.arena.service.RettighetTilleggService.ARENA_TILLEGG_TILSYN_FAMILIEMEDLEMMER_DATE_LIMIT;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetSyntRequest;
import no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils;
import no.nav.registre.testnorge.arena.service.util.ArbeidssoekerUtils;
import no.nav.registre.testnorge.arena.service.util.IdenterUtils;
import no.nav.registre.testnorge.arena.service.util.ServiceUtils;
import no.nav.registre.testnorge.arena.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import no.nav.registre.testnorge.arena.consumer.rs.TilleggSyntConsumer;

@RunWith(MockitoJUnitRunner.class)
public class RettighetTilleggServiceTest {

    @Mock
    private ConsumerUtils consumerUtils;

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
    private List<RettighetSyntRequest> syntRequest;

    @Before
    public void setUp() {
        identer = new ArrayList<>(Collections.singletonList("01010101010"));
        syntRequest = new ArrayList<>(Collections.singletonList(
                RettighetSyntRequest.builder()
                        .fraDato(LocalDate.now().toString())
                        .tilDato(LocalDate.now().toString())
                        .utfall(UTFALL_JA)
                        .vedtakTypeKode(VEDTAK_TYPE_KODE_O)
                        .vedtakDato(LocalDate.now().toString())
                        .build()
        ));
    }

    @Test
    public void shouldOppretteTilleggsstoenadBoutgifter() {
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tilleggSyntConsumer.opprettBoutgifter(syntRequest)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadBoutgifter(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettBoutgifter(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadDagligReise() {
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tilleggSyntConsumer.opprettDagligReise(syntRequest)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadDagligReise(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettDagligReise(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadFlytting() {
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tilleggSyntConsumer.opprettFlytting(syntRequest)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadFlytting(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettFlytting(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadLaeremidler() {
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tilleggSyntConsumer.opprettLaeremidler(syntRequest)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadLaeremidler(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettLaeremidler(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadHjemreise() {
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tilleggSyntConsumer.opprettHjemreise(syntRequest)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadHjemreise(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettHjemreise(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadReiseObligatoriskSamling() {
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tilleggSyntConsumer.opprettReiseObligatoriskSamling(syntRequest)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadReiseObligatoriskSamling(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettReiseObligatoriskSamling(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadTilsynBarn() {
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tilleggSyntConsumer.opprettTilsynBarn(syntRequest)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadTilsynBarn(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettTilsynBarn(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadTilsynFamiliemedlemmer() {
        when(consumerUtils.createSyntRequest(antallNyeIdenter, ARENA_TILLEGG_TILSYN_FAMILIEMEDLEMMER_DATE_LIMIT)).thenReturn(syntRequest);
        when(tilleggSyntConsumer.opprettTilsynFamiliemedlemmer(syntRequest)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadTilsynFamiliemedlemmer(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettTilsynFamiliemedlemmer(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadTilsynBarnArbeidssoekere() {
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tilleggSyntConsumer.opprettTilsynBarnArbeidssoekere(syntRequest)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadTilsynBarnArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettTilsynBarnArbeidssoekere(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadTilsynFamiliemedlemmerArbeidssoekere() {
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tilleggSyntConsumer.opprettTilsynFamiliemedlemmerArbeidssoekere(syntRequest)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadTilsynFamiliemedlemmerArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettTilsynFamiliemedlemmerArbeidssoekere(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadBoutgifterArbeidssoekere() {
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tilleggSyntConsumer.opprettBoutgifterArbeidssoekere(syntRequest)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadBoutgifterArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettBoutgifterArbeidssoekere(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadDagligReiseArbeidssoekere() {
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tilleggSyntConsumer.opprettDagligReiseArbeidssoekere(syntRequest)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadDagligReiseArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettDagligReiseArbeidssoekere(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadFlyttingArbeidssoekere() {
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tilleggSyntConsumer.opprettFlyttingArbeidssoekere(syntRequest)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadFlyttingArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettFlyttingArbeidssoekere(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadLaeremidlerArbeidssoekere() {
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tilleggSyntConsumer.opprettLaeremidlerArbeidssoekere(syntRequest)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadLaeremidlerArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettLaeremidlerArbeidssoekere(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadHjemreiseArbeidssoekere() {
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tilleggSyntConsumer.opprettHjemreiseArbeidssoekere(syntRequest)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadHjemreiseArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettHjemreiseArbeidssoekere(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadReiseObligatoriskSamlingArbeidssoekere() {
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tilleggSyntConsumer.opprettReiseObligatoriskSamlingArbeidssoekere(syntRequest)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadReiseObligatoriskSamlingArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettReiseObligatoriskSamlingArbeidssoekere(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }

    @Test
    public void shouldOppretteTilleggsstoenadReisestoenadArbeidssoekere() {
        when(consumerUtils.createSyntRequest(antallNyeIdenter)).thenReturn(syntRequest);
        when(tilleggSyntConsumer.opprettReisestoenadArbeidssoekere(syntRequest)).thenReturn(Collections.singletonList(new NyttVedtakTillegg()));
        when(identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe)).thenReturn(identer);
        when(rettighetTiltakService.opprettTiltaksaktiviteter(anyList())).thenReturn(new HashMap<>());
        when(rettighetArenaForvalterConsumer.opprettRettighet(anyList())).thenReturn(new HashMap<>());

        rettighetTilleggService.opprettTilleggsstoenadReisestoenadArbeidssoekere(avspillergruppeId, miljoe, antallNyeIdenter);

        verify(tilleggSyntConsumer).opprettReisestoenadArbeidssoekere(syntRequest);
        verify(identerUtils).getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);
        verify(arbeidssoekerUtils).opprettArbeidssoekerTillegg(anyList(), eq(miljoe));
        verify(rettighetTiltakService).opprettTiltaksaktiviteter(anyList());
        verify(rettighetArenaForvalterConsumer).opprettRettighet(anyList());
    }
}