package no.nav.dolly.bestilling.arenaforvalter.utils;

import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaStatusResponse;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaAap;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaDagpenger;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ArenaEksisterendeVedtakUtilTest {

    @Test
    void vedtakAapEksistererIkke() {

        var util = ArenaEksisterendeVedtakUtil.getArenaOperasjoner(Arenadata.builder()
                        .aap(List.of(RsArenaAap.builder()
                                .fraDato(LocalDateTime.of(2023, 3, 1, 0, 0))
                                .build()))
                        .build(),
                new ArenaStatusResponse());

        assertThat(util.getAapVedtak().getAvslutteVedtak(), is(nullValue()));
        assertThat(util.getAapVedtak().isEksisterendeVedtak(), is(false));
        assertThat(util.getAapVedtak().getNyttVedtak().getFom(), is(equalTo(LocalDate.of(2023, 3, 1))));
        assertThat(util.getAapVedtak().getNyttVedtak().getTom(), is(nullValue()));
        assertThat(util.getDagpengeVedtak().getAvslutteVedtak(), is(nullValue()));
        assertThat(util.getDagpengeVedtak().isEksisterendeVedtak(), is(false));
        assertThat(util.getDagpengeVedtak().getNyttVedtak(), is(nullValue()));
    }

    @Test
    void vedtakAapEksistereAllerede() {

        var util = ArenaEksisterendeVedtakUtil.getArenaOperasjoner(Arenadata.builder()
                        .aap(List.of(RsArenaAap.builder()
                                .fraDato(LocalDateTime.of(2023, 1, 1, 0, 0))
                                .build()))
                        .build(),
                getVedtak());

        assertThat(util.getAapVedtak().getAvslutteVedtak(), is(nullValue()));
        assertThat(util.getAapVedtak().isEksisterendeVedtak(), is(true));
        assertThat(util.getAapVedtak().getNyttVedtak(), is(nullValue()));
        assertThat(util.getDagpengeVedtak().getAvslutteVedtak(), is(nullValue()));
        assertThat(util.getDagpengeVedtak().isEksisterendeVedtak(), is(false));
        assertThat(util.getDagpengeVedtak().getNyttVedtak(), is(nullValue()));
    }

    @Test
    void vedtakAapOverlapperEksisterende() {

        var util = ArenaEksisterendeVedtakUtil.getArenaOperasjoner(Arenadata.builder()
                        .aap(List.of(RsArenaAap.builder()
                                .fraDato(LocalDateTime.of(2023, 3, 1, 0, 0))
                                .build()))
                        .build(),
                getVedtak());

        assertThat(util.getAapVedtak().getAvslutteVedtak(), is(nullValue()));
        assertThat(util.getAapVedtak().isEksisterendeVedtak(), is(false));
        assertThat(util.getAapVedtak().getNyttVedtak().getFom(), is(equalTo(LocalDate.of(2023, 3, 1))));
        assertThat(util.getAapVedtak().getNyttVedtak().getTom(), is(equalTo(LocalDate.of(2023, 4, 30))));
        assertThat(util.getDagpengeVedtak().getAvslutteVedtak().getFom(), is(equalTo(LocalDate.of(2023, 1, 1))));
        assertThat(util.getDagpengeVedtak().getAvslutteVedtak().getTom(), is(equalTo(LocalDate.of(2023, 2, 28))));
        assertThat(util.getDagpengeVedtak().isEksisterendeVedtak(), is(false));
        assertThat(util.getDagpengeVedtak().getNyttVedtak(), is(nullValue()));
    }

    @Test
    void vedtakAapForanledningerEksisterende() {

        var util = ArenaEksisterendeVedtakUtil.getArenaOperasjoner(Arenadata.builder()
                        .aap(List.of(RsArenaAap.builder()
                                .fraDato(LocalDateTime.of(2022, 10, 1, 0, 0))
                                .build()))
                        .build(),
                getVedtak());

        assertThat(util.getAapVedtak().getAvslutteVedtak(), is(nullValue()));
        assertThat(util.getAapVedtak().isEksisterendeVedtak(), is(false));
        assertThat(util.getAapVedtak().getNyttVedtak().getFom(), is(equalTo(LocalDate.of(2022, 10, 1))));
        assertThat(util.getAapVedtak().getNyttVedtak().getTom(), is(equalTo(LocalDate.of(2022, 12, 31))));
        assertThat(util.getDagpengeVedtak().getAvslutteVedtak(), is(nullValue()));
        assertThat(util.getDagpengeVedtak().isEksisterendeVedtak(), is(false));
        assertThat(util.getDagpengeVedtak().getNyttVedtak(), is(nullValue()));
    }

    @Test
    void vedtakDagpengerEksisterAllleredeIngenEndring() {

        var util = ArenaEksisterendeVedtakUtil.getArenaOperasjoner(Arenadata.builder()
                        .dagpenger(List.of(RsArenaDagpenger.builder()
                                .fraDato(LocalDateTime.of(2023, 1, 1, 0, 0))
                                .tilDato(LocalDateTime.of(2023, 4, 30, 0, 0))
                                .build()))
                        .build(),
                getVedtak());

        assertThat(util.getAapVedtak().getAvslutteVedtak(), is(nullValue()));
        assertThat(util.getAapVedtak().isEksisterendeVedtak(), is(false));
        assertThat(util.getAapVedtak().getNyttVedtak(), is(nullValue()));
        assertThat(util.getDagpengeVedtak().getAvslutteVedtak(), is(nullValue()));
        assertThat(util.getDagpengeVedtak().isEksisterendeVedtak(), is(true));
        assertThat(util.getDagpengeVedtak().getNyttVedtak(), is(nullValue()));
    }

    private static ArenaStatusResponse getVedtak() {

        return ArenaStatusResponse.builder()
                .vedtakListe(List.of(
                        ArenaStatusResponse.Vedtak.builder()
                                .sak(ArenaStatusResponse.Sak.builder()
                                        .status("Aktiv")
                                        .sakNr("20230007856")
                                        .kode("AA")
                                        .navn("Arbeidsavklaringspenger")
                                        .build())
                                .vedtakNr(3)
                                .rettighet(ArenaStatusResponse.Egenskap.builder()
                                        .kode("AAP")
                                        .navn("Arbeidsavklaringspenger")
                                        .build())
                                .aktivitetfase(ArenaStatusResponse.Egenskap.builder()
                                        .kode("UA")
                                        .navn("Under arbeidavklaring")
                                        .build())
                                .type(ArenaStatusResponse.Egenskap.builder()
                                        .kode("O")
                                        .navn("Ny rettighet")
                                        .build())
                                .status(ArenaStatusResponse.Egenskap.builder()
                                        .kode("MOTAT")
                                        .navn("Mottatt")
                                        .build())
                                .utfall("Ja")
                                .fraDato(LocalDate.of(2023, 5, 1))
                                .build(),
                        ArenaStatusResponse.Vedtak.builder()
                                .sak(ArenaStatusResponse.Sak.builder()
                                        .status("Lukket")
                                        .sakNr("20230007856")
                                        .kode("DAGP")
                                        .navn("Dagpenger")
                                        .build())
                                .vedtakNr(1)
                                .rettighet(ArenaStatusResponse.Egenskap.builder()
                                        .kode("DAGO")
                                        .navn("Ordinære dagpenger")
                                        .build())
                                .aktivitetfase(ArenaStatusResponse.Egenskap.builder()
                                        .kode("IKKE")
                                        .navn("Ikke spesif.aktivitesfase")
                                        .build())
                                .type(ArenaStatusResponse.Egenskap.builder()
                                        .kode("O")
                                        .navn("Ny rettighet")
                                        .build())
                                .status(ArenaStatusResponse.Egenskap.builder()
                                        .kode("IVERK")
                                        .navn("Iverksatt")
                                        .build())
                                .utfall("Ja")
                                .fraDato(LocalDate.of(2023, 1, 1))
                                .tilDato(LocalDate.of(2023, 4, 30))
                                .build(),
                        ArenaStatusResponse.Vedtak.builder()
                                .sak(ArenaStatusResponse.Sak.builder()
                                        .status("Aktiv")
                                        .sakNr("20230007856")
                                        .kode("AA")
                                        .navn("Arbeidsavklaringspenger")
                                        .build())
                                .vedtakNr(1)
                                .rettighet(ArenaStatusResponse.Egenskap.builder()
                                        .kode("AAP115")
                                        .navn("§11-5 nedsatt arbeidsevne")
                                        .build())
                                .aktivitetfase(ArenaStatusResponse.Egenskap.builder()
                                        .kode("IKKE")
                                        .navn("Ikke spesif.aktivitetsfase")
                                        .build())
                                .type(ArenaStatusResponse.Egenskap.builder()
                                        .kode("O")
                                        .navn("Ny rettighet")
                                        .build())
                                .status(ArenaStatusResponse.Egenskap.builder()
                                        .kode("IVERK")
                                        .navn("Iverksatt")
                                        .build())
                                .utfall("Ja")
                                .fraDato(LocalDate.of(2023, 1, 1))
                                .build()))
                .build();
    }
}