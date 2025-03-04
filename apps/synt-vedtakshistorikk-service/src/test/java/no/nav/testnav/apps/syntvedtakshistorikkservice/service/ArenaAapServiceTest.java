package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet.RettighetRequest;
import no.nav.testnav.libs.dto.arena.testnorge.aap.gensaksopplysninger.Saksopplysning;
import no.nav.testnav.libs.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.testnav.libs.dto.personsearchservice.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.AKTIVITETSFASE_SYKEPENGEERSTATNING;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.ARENA_AAP_UNG_UFOER_DATE_LIMIT;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ArenaAapServiceTest {

    @Mock
    private IdentService identService;

    @InjectMocks
    private ArenaAapService arenaAapService;

    private final String fnr1 = "27869949421";
    private final PersonDTO person = PersonDTO.builder()
            .ident(fnr1)
            .foedselsdato(FoedselsdatoDTO.builder()
                    .foedselsdato(LocalDate.of(1999, 6, 27))
                    .build())
            .build();
    private final String miljoe = "q2";
    private Vedtakshistorikk historikk;
    private List<NyttVedtakAap> aap115Rettigheter;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void shouldUpdateRettigheter() {
        List<RettighetRequest> rettigheter = new ArrayList<>();

        arenaAapService.opprettVedtakAap115(historikk.getAap115(), fnr1, miljoe, rettigheter);
        arenaAapService.opprettVedtakAap(historikk, fnr1, miljoe, rettigheter);
        arenaAapService.opprettVedtakUngUfoer(historikk, person, miljoe, rettigheter);
        arenaAapService.opprettVedtakFritakMeldekort(historikk, fnr1, miljoe, rettigheter);
        arenaAapService.opprettVedtakTvungenForvaltning(historikk, fnr1, miljoe, rettigheter);

        assertThat(rettigheter).hasSize(5);
    }

    @Test
    void shouldGetIkkeAvsluttende115Vedtak() {
        var ikkeAvsluttendeVedtak = arenaAapService.getIkkeAvsluttendeVedtakAap115(aap115Rettigheter);
        var emptyVedtak = arenaAapService.getIkkeAvsluttendeVedtakAap115(null);

        assertThat(ikkeAvsluttendeVedtak).hasSize(1);
        assertThat(ikkeAvsluttendeVedtak.getFirst().getVedtaktype()).isEqualTo("O");
        assertThat(emptyVedtak).isEmpty();

    }

    @Test
    void shouldGetAvsluttende115Vedtak() {
        var avsluttendeVedtak = arenaAapService.getAvsluttendeVedtakAap115(aap115Rettigheter);
        var emptyVedtak = arenaAapService.getAvsluttendeVedtakAap115(null);

        assertThat(avsluttendeVedtak).hasSize(1);
        assertThat(avsluttendeVedtak.getFirst().getVedtaktype()).isEqualTo("S");
        assertThat(emptyVedtak).isEmpty();
    }

    @Test
    void shouldFjernAapUngUfoerMedUgyldigeDatoer() {
        var ungUfoer1 = NyttVedtakAap.builder().build();
        ungUfoer1.setFraDato(ARENA_AAP_UNG_UFOER_DATE_LIMIT.minusDays(7));
        var ungUfoer2 = NyttVedtakAap.builder().build();
        ungUfoer2.setFraDato(ARENA_AAP_UNG_UFOER_DATE_LIMIT.plusDays(7));
        var vedtak = Arrays.asList(ungUfoer1, ungUfoer2);

        var response = arenaAapService.fjernAapUngUfoerMedUgyldigeDatoer(vedtak);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getFraDato()).isEqualTo(ARENA_AAP_UNG_UFOER_DATE_LIMIT.minusDays(7));
    }

    @Test
    void shouldUpdateAapSykepengerDatoer() {
        var aap1 = NyttVedtakAap.builder().build();
        aap1.setAktivitetsfase(AKTIVITETSFASE_SYKEPENGEERSTATNING);
        aap1.setFraDato(LocalDate.now());
        aap1.setTilDato(LocalDate.now().plusMonths(7));
        aap1.setVedtaktype("O");

        var aap2 = NyttVedtakAap.builder().build();
        aap2.setAktivitetsfase("TEST");
        aap2.setFraDato(LocalDate.now().plusMonths(7));
        aap2.setTilDato(LocalDate.now().plusMonths(10));
        aap2.setVedtaktype("E");

        var aap3 = NyttVedtakAap.builder().build();
        aap3.setAktivitetsfase(AKTIVITETSFASE_SYKEPENGEERSTATNING);
        aap3.setFraDato(LocalDate.now().plusMonths(10));
        aap3.setTilDato(LocalDate.now().plusMonths(17));
        aap3.setVedtaktype("S");

        var aap4 = NyttVedtakAap.builder().build();
        aap4.setAktivitetsfase("TEST");
        aap4.setFraDato(LocalDate.now().plusMonths(7));
        aap4.setTilDato(LocalDate.now().plusMonths(10));
        aap4.setVedtaktype("O");

        var vedtak = Arrays.asList(aap1, aap2, aap3, aap4);
        var vedtakshistorikk = Vedtakshistorikk
                .builder()
                .aap(vedtak)
                .build();
        arenaAapService.oppdaterAapSykepengeerstatningDatoer(vedtakshistorikk);

        var diff1 = ChronoUnit.DAYS.between(LocalDate.now().plusMonths(6), LocalDate.now().plusMonths(7));

        assertThat(vedtakshistorikk.getAap()).hasSize(4);
        assertThat(vedtakshistorikk.getAap().get(0).getFraDato()).isEqualTo(LocalDate.now());
        assertThat(vedtakshistorikk.getAap().get(0).getTilDato()).isEqualTo(LocalDate.now().plusMonths(6));
        assertThat(vedtakshistorikk.getAap().get(1).getFraDato()).isEqualTo(LocalDate.now().plusMonths(7).minusDays(diff1));
        assertThat(vedtakshistorikk.getAap().get(1).getTilDato()).isEqualTo(LocalDate.now().plusMonths(10).minusDays(diff1));
        assertThat(vedtakshistorikk.getAap().get(2).getFraDato()).isEqualTo(LocalDate.now().plusMonths(10).minusDays(diff1));
        assertThat(vedtakshistorikk.getAap().get(2).getTilDato()).isEqualTo(LocalDate.now().plusMonths(10).minusDays(diff1).plusMonths(6));
        assertThat(vedtakshistorikk.getAap().get(3).getFraDato()).isEqualTo(LocalDate.now().plusMonths(7));
        assertThat(vedtakshistorikk.getAap().get(3).getTilDato()).isEqualTo(LocalDate.now().plusMonths(10));
    }

}
