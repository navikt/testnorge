package no.nav.registre.testnorge.arena.service.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.aap.gensaksopplysninger.GensakKoder;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtak;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatoUtils {

    public LocalDate finnTidligsteDato(Vedtakshistorikk vedtakshistorikken) {

        LocalDate tidligsteDato;
        var aap = finnUtfyltAap(vedtakshistorikken);
        var aapType = vedtakshistorikken.getAlleAapVedtak();
        var tiltak = vedtakshistorikken.getAlleTiltakVedtak();
        var tillegg = vedtakshistorikken.getAlleTilleggVedtak();

        if (!aap.isEmpty()) {
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

    public LocalDate finnTidligeDatoBarnetillegg(List<NyttVedtakTiltak> barnetillegg) {
        if (barnetillegg != null && !barnetillegg.isEmpty()) {
            return finnTidligsteDatoTiltak(barnetillegg);
        }
        return null;
    }

    private LocalDate finnTidligsteDatoAap(List<NyttVedtakAap> vedtak) {
        var tidligsteDato = LocalDate.now();
        for (var vedtaket : vedtak) {
            tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, vedtaket.getFraDato());
            var genSaksopplysninger = vedtaket.getGenSaksopplysninger();
            if (genSaksopplysninger != null) {
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

    private LocalDate finnTidligsteDatoAapType(List<NyttVedtakAap> vedtak) {
        var tidligsteDato = LocalDate.now();
        for (var vedtaket : vedtak) {
            tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, vedtaket.getFraDato());
        }
        return tidligsteDato;
    }

    private LocalDate finnTidligsteDatoTiltak(List<NyttVedtakTiltak> vedtak) {
        var tidligsteDato = LocalDate.now();
        for (var vedtaket : vedtak) {
            tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, vedtaket.getFraDato());
        }
        return tidligsteDato;
    }

    private LocalDate finnTidligsteDatoTillegg(List<NyttVedtakTillegg> vedtak) {
        var tidligsteDato = LocalDate.now();
        for (var vedtaket : vedtak) {
            tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, vedtaket.getFraDato());
            tidligsteDato = finnTidligsteDatoAvTo(tidligsteDato, vedtaket.getVedtaksperiode().getFom());
        }
        return tidligsteDato;
    }

    private LocalDate finnTidligsteDatoAvTo(
            LocalDate date1,
            LocalDate date2
    ) {
        if (date2 == null) {
            return date1;
        }
        if (date2.isBefore(date1)) {
            return date2;
        } else {
            return date1;
        }
    }

    public NyttVedtak finnSenesteVedtak(List<? extends NyttVedtak> vedtak) {
        var senesteDato = LocalDate.MIN;
        NyttVedtak senesteVedtak = null;
        for (var vedtaket : vedtak) {
            LocalDate vedtakFraDato;
            if (vedtaket instanceof NyttVedtakTillegg) {
                vedtakFraDato = ((NyttVedtakTillegg) vedtaket).getVedtaksperiode().getFom();
            } else {
                vedtakFraDato = vedtaket.getFraDato();
            }
            if (vedtakFraDato != null && senesteDato.isBefore(vedtakFraDato)) {
                senesteDato = vedtakFraDato;
                senesteVedtak = vedtaket;
            }
        }
        return senesteVedtak;
    }

    private List<NyttVedtakAap> finnUtfyltAap(Vedtakshistorikk vedtakshistorikk) {
        var aap = vedtakshistorikk.getAap();

        if (aap != null && !aap.isEmpty()) {
            return aap;
        }

        return Collections.emptyList();
    }

    public boolean datoErInnenforPeriode(LocalDate vedtakDato, LocalDate periodeStart, LocalDate periodeSlutt) {
        if (periodeStart == null || vedtakDato == null) {
            return false;
        }

        if (periodeSlutt == null) {
            return periodeStart.isEqual(vedtakDato);
        } else {
            if (periodeSlutt.isBefore(vedtakDato.plusDays(1))) {
                return false;
            } else {
                return periodeStart.isBefore(vedtakDato.plusDays(1)) && periodeSlutt.isAfter(vedtakDato);
            }
        }
    }


    public void setDatoPeriodeVedtakInnenforMaxAntallMaaneder(
            NyttVedtak vedtak,
            int antallMaaneder
    ) {
        var tilDato = vedtak.getTilDato();
        if (tilDato != null) {
            var tilDatoLimit = vedtak.getFraDato().plusMonths(antallMaaneder);

            if (tilDato.isAfter(tilDatoLimit)) {
                vedtak.setTilDato(tilDatoLimit);
            }
        }
    }
}
