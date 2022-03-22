package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet.RettighetRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.Kontoinfo;
import no.nav.testnav.libs.domain.dto.arena.testnorge.aap.gensaksopplysninger.Saksopplysning;
import no.nav.testnav.libs.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.ARENA_AAP_UNG_UFOER_DATE_LIMIT;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.AKTIVITETSFASE_SYKEPENGEERSTATNING;

@RunWith(MockitoJUnitRunner.class)
public class ArenaAapServiceTest {

    @Mock
    private IdentService identService;

    @InjectMocks
    private ArenaAapService arenaAapService;

    private final String fnr1 = "27869949421";
    private final String miljoe = "q2";
    private Vedtakshistorikk historikk;
    private List<NyttVedtakAap> aap115Rettigheter;

    @Before
    public void setUp() {
        Saksopplysning saksopplysning = new Saksopplysning();
        var nyRettighetAap = NyttVedtakAap.builder()
                .build();
        nyRettighetAap.setFraDato(ARENA_AAP_UNG_UFOER_DATE_LIMIT.minusDays(7));
        nyRettighetAap.setTilDato(ARENA_AAP_UNG_UFOER_DATE_LIMIT);
        nyRettighetAap.setGenSaksopplysninger(Collections.singletonList(saksopplysning));

        var nyRettighetUngUfoer = NyttVedtakAap.builder()
                .build();
        nyRettighetUngUfoer.setFraDato(ARENA_AAP_UNG_UFOER_DATE_LIMIT.minusDays(7));

        var aa115RettighetO = NyttVedtakAap.builder().build();
        aa115RettighetO.setVedtaktype("O");
        var aa115RettighetS = NyttVedtakAap.builder().build();
        aa115RettighetS.setVedtaktype("S");

        var nyttVedtakAap = NyttVedtakAap.builder().build();
        var defaultRettigheter = Collections.singletonList(nyttVedtakAap);
        var aapRettigheter = Collections.singletonList(nyRettighetAap);
        aap115Rettigheter = Arrays.asList(aa115RettighetO, aa115RettighetS);
        var ungUfoerRettigheter = Collections.singletonList(nyRettighetUngUfoer);

        historikk = Vedtakshistorikk.builder()
                .aap(aapRettigheter)
                .aap115(aap115Rettigheter)
                .ungUfoer(ungUfoerRettigheter)
                .tvungenForvaltning(defaultRettigheter)
                .fritakMeldekort(defaultRettigheter)
                .build();

        when(identService.getIdentMedKontoinformasjon()).thenReturn(Kontoinfo.builder()
                        .fnr("12345678910")
                        .kontonummer("123")
                .build());
    }

    @Test
    public void shouldUpdateRettigheter(){
        List<RettighetRequest> rettigheter = new ArrayList<>();

        arenaAapService.opprettVedtakAap115(historikk.getAap115(), fnr1, miljoe, rettigheter);
        arenaAapService.opprettVedtakAap(historikk, fnr1, miljoe, rettigheter);
        arenaAapService.opprettVedtakUngUfoer(historikk, fnr1, miljoe, rettigheter);
        arenaAapService.opprettVedtakFritakMeldekort(historikk, fnr1, miljoe, rettigheter);
        arenaAapService.opprettVedtakTvungenForvaltning(historikk, fnr1, miljoe, rettigheter);

        assertThat(rettigheter).hasSize(6);
    }

    @Test
    public void shouldGetIkkeAvsluttende115Vedtak(){
        var ikkeAvsluttendeVedtak = arenaAapService.getIkkeAvsluttendeVedtakAap115(aap115Rettigheter);
        var emptyVedtak = arenaAapService.getIkkeAvsluttendeVedtakAap115(null);

        assertThat(ikkeAvsluttendeVedtak).hasSize(1);
        assertThat(ikkeAvsluttendeVedtak.get(0).getVedtaktype()).isEqualTo("O");
        assertThat(emptyVedtak).isEmpty();

    }

    @Test
    public void shouldGetAvsluttende115Vedtak(){
        var avsluttendeVedtak = arenaAapService.getAvsluttendeVedtakAap115(aap115Rettigheter);
        var emptyVedtak = arenaAapService.getAvsluttendeVedtakAap115(null);

        assertThat(avsluttendeVedtak).hasSize(1);
        assertThat(avsluttendeVedtak.get(0).getVedtaktype()).isEqualTo("S");
        assertThat(emptyVedtak).isEmpty();
    }

    @Test
    public void shouldFjernAapUngUfoerMedUgyldigeDatoer(){
        var ungUfoer1 = NyttVedtakAap.builder().build();
        ungUfoer1.setFraDato(ARENA_AAP_UNG_UFOER_DATE_LIMIT.minusDays(7));
        var ungUfoer2 = NyttVedtakAap.builder().build();
        ungUfoer2.setFraDato(ARENA_AAP_UNG_UFOER_DATE_LIMIT.plusDays(7));
        var vedtak = Arrays.asList(ungUfoer1, ungUfoer2);

        var response = arenaAapService.fjernAapUngUfoerMedUgyldigeDatoer(vedtak);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getFraDato()).isEqualTo(ARENA_AAP_UNG_UFOER_DATE_LIMIT.minusDays(7));
    }

    @Test
    public void shouldUpdateAapSykepengerDatoer(){
        var aap1 = NyttVedtakAap.builder().build();
        aap1.setAktivitetsfase(AKTIVITETSFASE_SYKEPENGEERSTATNING);
        aap1.setFraDato(LocalDate.now());
        aap1.setTilDato(LocalDate.now().plusMonths(7));

        var aap2 = NyttVedtakAap.builder().build();
        aap2.setAktivitetsfase(AKTIVITETSFASE_SYKEPENGEERSTATNING);
        aap2.setFraDato(LocalDate.now().plusMonths(7));
        aap2.setTilDato(LocalDate.now().plusMonths(10));

        var vedtak = Arrays.asList(aap1, aap2);
        arenaAapService.oppdaterAapSykepengeerstatningDatoer(vedtak);

        var diff = ChronoUnit.DAYS.between(LocalDate.now().plusMonths(6), LocalDate.now().plusMonths(7));
        assertThat(vedtak.get(0).getFraDato()).isEqualTo(LocalDate.now());
        assertThat(vedtak.get(0).getTilDato()).isEqualTo(LocalDate.now().plusMonths(6));
        assertThat(vedtak.get(1).getFraDato()).isEqualTo(LocalDate.now().plusMonths(7).minusDays(diff));
        assertThat(vedtak.get(1).getTilDato()).isEqualTo(LocalDate.now().plusMonths(10).minusDays(diff));
    }
}
