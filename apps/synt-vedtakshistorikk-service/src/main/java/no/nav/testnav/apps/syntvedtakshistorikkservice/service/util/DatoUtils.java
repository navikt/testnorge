package no.nav.testnav.apps.syntvedtakshistorikkservice.service.util;

import no.nav.testnav.libs.domain.dto.arena.testnorge.aap.gensaksopplysninger.GensakKoder;
import no.nav.testnav.libs.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.testnav.libs.domain.dto.arena.testnorge.tilleggsstoenad.Vedtaksperiode;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class DatoUtils {

    private DatoUtils() {
    }

    public static LocalDate finnTidligsteDato(Vedtakshistorikk vedtakshistorikken) {
        LocalDate tidligsteDato;
        var aap = vedtakshistorikken.getAap();
        var aapType = vedtakshistorikken.getAlleAapVedtak();
        var tiltak = vedtakshistorikken.getAlleTiltakVedtak();
        var tillegg = vedtakshistorikken.getAlleTilleggVedtak();

        if (nonNull(aap) && !aap.isEmpty()) {
            tidligsteDato = finnTidligsteDatoAap(aap);
        } else if (!aapType.isEmpty()) {
            tidligsteDato = finnTidligsteDatoAapType(aapType);
        } else if (!tiltak.isEmpty()) {
            tidligsteDato = finnTidligsteDatoTiltak(tiltak);
        } else if (!tillegg.isEmpty()) {
            tidligsteDato = finnTidligsteDatoTillegg(tillegg);
        } else {
            return null;
        }
        return tidligsteDato;
    }

    public static LocalDate finnTidligeDatoBarnetillegg(List<NyttVedtakTiltak> barnetillegg) {
        if (nonNull(barnetillegg) && !barnetillegg.isEmpty()) {
            return finnTidligsteDatoTiltak(barnetillegg);
        }
        return null;
    }

    public static LocalDate finnTidligsteDatoAap(List<NyttVedtakAap> vedtak) {
        var tidligsteDato = LocalDate.now();
        for (var vedtaket : vedtak) {
            tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, vedtaket.getFraDato());
            var genSaksopplysninger = vedtaket.getGenSaksopplysninger();
            if (nonNull(genSaksopplysninger)) {
                for (var saksopplysning : genSaksopplysninger) {
                    if (isNullOrEmpty(saksopplysning.getVerdi())) {
                        continue;
                    }
                    if (GensakKoder.KDATO.equals(saksopplysning.getKode())
                            || GensakKoder.BTID.equals(saksopplysning.getKode())
                            || GensakKoder.UFTID.equals(saksopplysning.getKode())
                            || GensakKoder.YDATO.equals(saksopplysning.getKode())) {
                        tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, LocalDate.parse(saksopplysning.getVerdi(), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    }
                }
            }
        }
        return tidligsteDato;
    }

    private static LocalDate finnTidligsteDatoAapType(List<NyttVedtakAap> vedtak) {
        var tidligsteDato = LocalDate.now();
        for (var vedtaket : vedtak) {
            tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, vedtaket.getFraDato());
        }
        return tidligsteDato;
    }

    private static LocalDate finnTidligsteDatoTiltak(List<NyttVedtakTiltak> vedtak) {
        var tidligsteDato = LocalDate.now();
        for (var vedtaket : vedtak) {
            tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, vedtaket.getFraDato());
        }
        return tidligsteDato;
    }

    private static LocalDate finnTidligsteDatoTillegg(List<NyttVedtakTillegg> vedtak) {
        var tidligsteDato = LocalDate.now();
        for (var vedtaket : vedtak) {
            tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, vedtaket.getFraDato());
            tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, vedtaket.getVedtaksperiode().getFom());
        }
        return tidligsteDato;
    }

    private static LocalDate finnTidligsteDatoAvTo(
            LocalDate date1,
            LocalDate date2
    ) {
        if (isNull(date2)) {
            return date1;
        }
        if (date2.isBefore(date1)) {
            return date2;
        } else {
            return date1;
        }
    }

    public static NyttVedtak finnSenesteVedtak(List<? extends NyttVedtak> vedtak) {
        var senesteDato = LocalDate.MIN;
        NyttVedtak senesteVedtak = null;
        for (var vedtaket : vedtak) {
            LocalDate vedtakFraDato;
            if (vedtaket.getRettighetType() == RettighetType.TILLEGG) {
                vedtakFraDato = ((NyttVedtakTillegg) vedtaket).getVedtaksperiode().getFom();
            } else {
                vedtakFraDato = vedtaket.getFraDato();
            }
            if (nonNull(vedtakFraDato) && senesteDato.isBefore(vedtakFraDato)) {
                senesteDato = vedtakFraDato;
                senesteVedtak = vedtaket;
            }
        }
        return senesteVedtak;
    }

    public static boolean datoErInnenforPeriode(LocalDate vedtakDato, LocalDate periodeStart, LocalDate periodeSlutt) {
        if (isNull(periodeStart) || isNull(vedtakDato)) {
            return false;
        }

        if (isNull(periodeSlutt)) {
            return periodeStart.isEqual(vedtakDato);
        } else {
            if (periodeSlutt.isBefore(vedtakDato.plusDays(1))) {
                return false;
            } else {
                return periodeStart.isBefore(vedtakDato.plusDays(1)) && periodeSlutt.isAfter(vedtakDato);
            }
        }
    }

    public static void setDatoPeriodeVedtakInnenforMaxAntallMaaneder(
            NyttVedtak vedtak,
            int antallMaaneder
    ) {
        var tilDato = vedtak.getTilDato();
        if (nonNull(tilDato)) {
            var tilDatoLimit = vedtak.getFraDato().plusMonths(antallMaaneder);

            if (tilDato.isAfter(tilDatoLimit)) {
                vedtak.setTilDato(tilDatoLimit);
            }
        }
    }

    public static boolean datoerOverlapper(LocalDate fraDatoA, LocalDate tilDatoA, LocalDate fraDatoB, LocalDate tilDatoB) {
        try {
            return fraDatoA.isBefore(tilDatoB) && fraDatoB.isBefore(tilDatoA);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean vedtakOverlapperIkkeVedtaksperioder(Vedtaksperiode vedtak, List<Vedtaksperiode> vedtaksliste) {
        if (isNull(vedtaksliste) || vedtaksliste.isEmpty()) {
            return true;
        }
        var fraDato = vedtak.getFom();
        var tilDato = vedtak.getTom();

        for (var item : vedtaksliste) {
            if (datoerOverlapper(fraDato, tilDato, item.getFom(), item.getTom())) {
                return false;
            }
        }
        return true;
    }

}
