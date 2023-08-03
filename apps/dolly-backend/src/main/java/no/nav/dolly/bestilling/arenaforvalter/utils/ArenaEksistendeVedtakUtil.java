package no.nav.dolly.bestilling.arenaforvalter.utils;

import lombok.experimental.UtilityClass;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaStatusResponse;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaVedtakOperasjoner;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaPeriode;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaAap;
import no.nav.dolly.domain.resultset.arenaforvalter.RsArenaDagpenger;
import no.nav.dolly.util.NullcheckUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@UtilityClass
public class ArenaEksistendeVedtakUtil {

    enum Ytelse {AAP, DAGO}

    public static ArenaVedtakOperasjoner getArenaOperasjoner(Arenadata arenadata, ArenaStatusResponse response) {

        return ArenaVedtakOperasjoner.builder()
                .registrertDato(response.getRegistrertDato())
                .aapVedtak(ArenaVedtakOperasjoner.Operasjon.builder()
                        .avslutteVedtak(getAvslutteVedtak(Ytelse.AAP, arenadata, response.getVedtakListe()))
                        .nyttVedtak(isNull(arenadata.getInaktiveringDato()) &&
                                !arenadata.getAap().isEmpty() &&
                                !isVedtak(response.getVedtakListe(), "O", arenadata.getAap()) ?
                                ArenaVedtakOperasjoner.Periode.builder()
                                        .fom(arenadata.getAap().stream()
                                                .map(RsArenaAap::getFraDato)
                                                .map(LocalDateTime::toLocalDate)
                                                .findFirst()
                                                .orElse(null))
                                        .tom(getNyttVedtakTom(arenadata.getAap(), response.getVedtakListe()))
                                        .build() : null)
                        .build())
                .dagpengeVedtak(ArenaVedtakOperasjoner.Operasjon.builder()
                        .avslutteVedtak(getAvslutteVedtak(Ytelse.DAGO, arenadata, response.getVedtakListe()))
                        .nyttVedtak(isNull(arenadata.getInaktiveringDato()) &&
                                !arenadata.getDagpenger().isEmpty() &&
                                !isVedtak(response.getVedtakListe(), "O", arenadata.getDagpenger()) ?
                                ArenaVedtakOperasjoner.Periode.builder()
                                        .fom(arenadata.getDagpenger().stream()
                                                .map(RsArenaDagpenger::getFraDato)
                                                .map(LocalDateTime::toLocalDate)
                                                .findFirst()
                                                .orElse(null))
                                        .tom(getNyttVedtakTom(arenadata.getDagpenger(), response.getVedtakListe()))
                                        .build() : null)
                        .build())
                .build();
    }

    private static boolean isVedtak(List<ArenaStatusResponse.Vedtak> vedtak, String operasjon, List<? extends ArenaPeriode> request) {

        return vedtak.stream().anyMatch(vedtak1 -> operasjon.equals(vedtak1.getType().getKode()) &&
                vedtak1.isVedtak() &&
                request.stream().anyMatch(aap -> aap.getFraDato().toLocalDate().equals(vedtak1.getFraDato())));
    }

    private static LocalDate getNyttVedtakTom(List<? extends ArenaPeriode> request, List<ArenaStatusResponse.Vedtak> vedtak) {

        var vedtaker = getVedtaker(vedtak, null);

        var tom = new AtomicReference<LocalDate>(null);
        request.stream()
                .findFirst()
                .ifPresent(request1 -> {

                    tom.set(toLocalDate(request1.getTilDato()));

                    int i = 0;
                    while (i < vedtaker.size() && request1.getFraDato().toLocalDate().isAfter(vedtaker.get(i).getFraDato())) {
                        i++;
                    }

                    if (!vedtaker.isEmpty() && i < vedtaker.size()) {

                        Stream.of(NullcheckUtil.nullcheckSetDefaultValue(toLocalDate(request1.getTilDato()), LocalDate.now()),
                                        vedtaker.get(i).getFraDato().minusDays(1))
                                .min(LocalDate::compareTo)
                                .ifPresent(tom::set);
                    }
                });

        return tom.get();
    }

    private static ArenaVedtakOperasjoner.Periode getAvslutteVedtak(Ytelse ytelse, Arenadata request, List<ArenaStatusResponse.Vedtak> vedtak) {

        var vedtaker = getVedtaker(vedtak, ytelse);

        var periode = new AtomicReference<ArenaVedtakOperasjoner.Periode>(null);

        Stream.of(
                        request.getAap(),
                        request.getDagpenger())
                .flatMap(Collection::stream)
                .forEach(arenaPeriode -> {

                    int i = 0;
                    while (i < vedtaker.size() && arenaPeriode.getFraDato().toLocalDate().isAfter(vedtaker.get(i).getFraDato())) {
                        i++;
                    }

                    int finalI = Math.max(i - 1, 0);

                    if (!vedtaker.isEmpty() &&
                            arenaPeriode.getFraDato().toLocalDate().isAfter(vedtaker.get(finalI).getFraDato()) &&
                            (isNull(vedtak.get(finalI).getTilDato()) ||
                            vedtak.get(finalI).getTilDato().isAfter(arenaPeriode.getFraDato().toLocalDate()))) {

                        Stream.of(NullcheckUtil.nullcheckSetDefaultValue(vedtaker.get(finalI).getTilDato(), LocalDate.now()),
                                        arenaPeriode.getFraDato().toLocalDate().minusDays(1))
                                .min(Comparator.comparing(LocalDate::from))
                                .ifPresent(tomDato -> periode.set(ArenaVedtakOperasjoner.Periode.builder()
                                        .fom(vedtaker.get(finalI).getFraDato())
                                        .tom(tomDato)
                                        .build()));
                    }
                });

        return periode.get();
    }

    private static List<ArenaStatusResponse.Vedtak> getVedtaker(List<ArenaStatusResponse.Vedtak> vedtak, Ytelse ytelse) {

        return vedtak.stream()
                .filter(ArenaStatusResponse.Vedtak::isVedtak)
                .filter(vedtak1 -> isNull(ytelse) || ytelse.name().equals(vedtak1.getRettighet().getKode()))
                .filter(vedtak1 -> "O".equals(vedtak1.getType().getKode()) &&
                        vedtak.stream().noneMatch(vedtak2 -> "S".equals(vedtak2.getType().getKode()) &&
                                vedtak2.getRettighet().equals(vedtak1.getRettighet()) &&
                                vedtak2.getFraDato().equals(vedtak1.getFraDato())))
                .sorted(Comparator.comparing(ArenaStatusResponse.Vedtak::getFraDato))
                .toList();
    }

    private LocalDate toLocalDate(LocalDateTime tid) {

        return nonNull(tid) ? tid.toLocalDate() : null;
    }
}
