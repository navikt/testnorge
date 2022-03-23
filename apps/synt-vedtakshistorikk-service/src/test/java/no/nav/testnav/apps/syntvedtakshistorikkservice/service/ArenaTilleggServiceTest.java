package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet.RettighetAap115Request;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet.RettighetRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils;
import no.nav.testnav.libs.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.testnav.libs.domain.dto.arena.testnorge.tilleggsstoenad.Vedtaksperiode;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.ArenaTilleggService.ARENA_TILLEGG_TILSYN_FAMILIEMEDLEMMER_DATE_LIMIT;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.ArenaTilleggService.MAALGRUPPEKODE_TILKNYTTET_AAP;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.ArenaTilleggService.MAALGRUPPEKODE_TILKNYTTET_TILTAKSPENGER;

@RunWith(MockitoJUnitRunner.class)
public class ArenaTilleggServiceTest {

    @Mock
    private RequestUtils requestUtils;

    @InjectMocks
    private ArenaTilleggService arenaTilleggService;

    private List<NyttVedtakAap> aap115;
    private List<NyttVedtakAap> aap;
    private List<NyttVedtakTiltak> tiltak;
    private RettighetAap115Request aap115Request;

    private static final String IDENT = "12345678910";
    private static final String MILJOE = "test";

    @Before
    public void setUp() {
        aap115 = Collections.singletonList(NyttVedtakAap.builder().build());
        aap115Request = new RettighetAap115Request(aap115);
        aap115Request.setPersonident(IDENT);
        aap115Request.setMiljoe(MILJOE);

        var aapVedtak = NyttVedtakAap.builder().build();
        aapVedtak.setFraDato(LocalDate.now());
        aapVedtak.setTilDato(LocalDate.now().plusMonths(2));
        aap = Collections.singletonList(aapVedtak);

        var tiltakVedtak = NyttVedtakTiltak.builder().build();
        tiltakVedtak.setFraDato(LocalDate.now());
        tiltakVedtak.setTilDato(LocalDate.now().plusMonths(2));
        tiltak = Collections.singletonList(tiltakVedtak);
    }

    @Test
    public void shouldFilterOutTilleggWithoutRelatedTiltak(){
        var rettigheter = new ArrayList<RettighetRequest>();
        rettigheter.add(aap115Request);

        var tilleggVedtak = NyttVedtakTillegg.builder()
                .maalgruppeKode(MAALGRUPPEKODE_TILKNYTTET_TILTAKSPENGER)
                .vedtaksperiode(new Vedtaksperiode(LocalDate.now(), LocalDate.now().plusMonths(1)))
                .build();
        var tillegg = Collections.singletonList(tilleggVedtak);

        var historikk = Vedtakshistorikk.builder()
                .aap115(aap115)
                .dagligReise(tillegg)
                .build();

        arenaTilleggService.opprettVedtakTillegg(historikk, IDENT, MILJOE, rettigheter, Collections.emptyList());

        assertThat(rettigheter).hasSize(1);
    }

    @Test
    public void shouldFilterOutTilleggWithoutRelatedAap(){
        var rettigheter = new ArrayList<RettighetRequest>();
        rettigheter.add(aap115Request);

        var tilleggVedtak = NyttVedtakTillegg.builder()
                .maalgruppeKode(MAALGRUPPEKODE_TILKNYTTET_AAP)
                .vedtaksperiode(new Vedtaksperiode(LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1)))
                .build();
        var tillegg = Collections.singletonList(tilleggVedtak);

        var historikk = Vedtakshistorikk.builder()
                .aap115(aap115)
                .aap(aap)
                .dagligReise(tillegg)
                .build();

        arenaTilleggService.opprettVedtakTillegg(historikk, IDENT, MILJOE, rettigheter, Collections.emptyList());

        assertThat(rettigheter).hasSize(1);
    }

    @Test
    public void shouldAddRettighetRequest(){
        var rettigheter = new ArrayList<RettighetRequest>();
        rettigheter.add(aap115Request);

        var tilleggVedtak = NyttVedtakTillegg.builder()
                .maalgruppeKode("Test")
                .vedtaksperiode(new Vedtaksperiode(LocalDate.now(), LocalDate.now().plusMonths(1)))
                .build();
        tilleggVedtak.setVedtaktype("O");
        var tillegg = Collections.singletonList(tilleggVedtak);

        var historikk = Vedtakshistorikk.builder()
                .aap115(aap115)
                .dagligReise(tillegg)
                .build();
        arenaTilleggService.opprettVedtakTillegg(historikk, IDENT, MILJOE, rettigheter, tiltak);

        assertThat(rettigheter).hasSize(2);
    }

    @Test
    public void shouldAddRettighetRequests(){
        var rettigheter = new ArrayList<RettighetRequest>();
        rettigheter.add(aap115Request);

        var tilleggVedtak = NyttVedtakTillegg.builder()
                .maalgruppeKode("Test")
                .vedtaksperiode(new Vedtaksperiode(LocalDate.now(), LocalDate.now().plusMonths(1)))
                .build();
        tilleggVedtak.setVedtaktype("O");
        var tillegg = Collections.singletonList(tilleggVedtak);

        var historikk = Vedtakshistorikk.builder()
                .aap115(aap115)
                .dagligReise(tillegg)
                .build();
        arenaTilleggService.opprettVedtakTillegg(historikk, IDENT, MILJOE, rettigheter, Collections.emptyList());

        assertThat(rettigheter).hasSize(3);
    }


    @Test
    public void shouldFjerneVedtakMedUgyldigeDatoer(){
        var vedtak1 = NyttVedtakTillegg.builder().build();
        vedtak1.setFraDato(ARENA_TILLEGG_TILSYN_FAMILIEMEDLEMMER_DATE_LIMIT.plusDays(1));

        var vedtak2 = NyttVedtakTillegg.builder().build();
        vedtak2.setFraDato(ARENA_TILLEGG_TILSYN_FAMILIEMEDLEMMER_DATE_LIMIT);

        var response = arenaTilleggService
                .fjernTilsynFamiliemedlemmerVedtakMedUgyldigeDatoer(Arrays.asList(vedtak1, vedtak2));

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getFraDato()).isEqualTo(ARENA_TILLEGG_TILSYN_FAMILIEMEDLEMMER_DATE_LIMIT);
    }
}
