package no.nav.registre.testnorge.arena.service.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.tilleggsstoenad.Vedtaksperiode;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtak;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class TilleggUtils {

    private final DatoUtils datoUtils;
    private final RequestUtils requestUtils;
    private final TiltakUtils tiltakUtils;

    public List<List<NyttVedtakTillegg>> getTilleggSekvenser(List<NyttVedtakTillegg> tilleggsliste) {
        List<List<NyttVedtakTillegg>> sekvenser = new ArrayList<>();
        var indices = tiltakUtils.getIndicesForVedtakSequences(tilleggsliste);

        for (var i = 0; i < indices.size() - 1; i++) {
            sekvenser.add(tilleggsliste.subList(indices.get(i), indices.get(i + 1)));
        }

        return sekvenser;
    }

    public RettighetRequest getTiltaksaktivitetForTilleggSekvens(
            String personident,
            String miljoe,
            List<NyttVedtakTillegg> sekvens
    ) {
        var vedtaksperiode = getVedtaksperiodeForTilleggSekvens(sekvens);
        var rettighetKode = sekvens.get(0).getRettighetKode();

        return requestUtils.getRettighetTiltaksaktivitetRequest(personident, miljoe, rettighetKode, vedtaksperiode);
    }

    private Vedtaksperiode getVedtaksperiodeForTilleggSekvens(List<NyttVedtakTillegg> sekvens) {
        var perioder = sekvens.stream().map(NyttVedtakTillegg::getVedtaksperiode).collect(Collectors.toList());

        var startdato = perioder.stream().map(Vedtaksperiode::getFom).filter(Objects::nonNull).min(LocalDate::compareTo).orElse(null);
        var sluttdato = perioder.stream().map(Vedtaksperiode::getTom).filter(Objects::nonNull).max(LocalDate::compareTo).orElse(null);

        return new Vedtaksperiode(startdato, sluttdato);
    }


    public boolean tilleggSekvensManglerTiltak(List<NyttVedtakTillegg> sekvens, List<NyttVedtakTiltak> tiltak) {
        var vedtaksperiode = getVedtaksperiodeForTilleggSekvens(sekvens);

        for (var vedtak : tiltak) {
            if (datoUtils.datoErInnenforPeriode(vedtaksperiode.getFom(), vedtak.getFraDato(), vedtak.getTilDato()) &&
                    (vedtaksperiode.getTom() == null || datoUtils.datoErInnenforPeriode(vedtaksperiode.getTom(), vedtak.getFraDato(), vedtak.getTilDato()))) {
                return false;
            }
        }
        return true;
    }


    public List<NyttVedtakTillegg> filtrerBortTilleggMedUoensketMaalgruppekode(
            List<NyttVedtakTillegg> vedtak,
            String maalgruppekode
    ) {
        var filterteVedtak = vedtak;
        if (vedtak != null && !vedtak.isEmpty()) {
            filterteVedtak = vedtak.stream().filter(tillegg -> !tillegg.getMaalgruppeKode()
                    .equals(maalgruppekode)).collect(Collectors.toList());
        }
        return filterteVedtak;
    }

    public List<NyttVedtakTillegg> filtrerBortTilleggUtenGyldigTilknyttetVedtak(
            List<NyttVedtakTillegg> vedtak,
            List<? extends NyttVedtak> tilknyttetVedtak,
            String maalgruppekode
    ) {
        var filterteVedtak = vedtak;
        if (vedtak != null && !vedtak.isEmpty()) {
            filterteVedtak = vedtak.stream().filter(tillegg -> !tillegg.getMaalgruppeKode()
                    .equals(maalgruppekode) || (tillegg.getMaalgruppeKode()
                    .equals(maalgruppekode) && harGyldigTilknyttetVedtak(tillegg, tilknyttetVedtak)))
                    .collect(Collectors.toList());
        }
        return filterteVedtak;
    }

    private boolean harGyldigTilknyttetVedtak(NyttVedtakTillegg vedtak, List<? extends NyttVedtak> vedtaksliste) {
        if (vedtaksliste == null || vedtaksliste.isEmpty()) {
            return false;
        }
        var fraDato = vedtak.getVedtaksperiode().getFom();

        if (fraDato == null) {
            return false;
        }

        for (var item : vedtaksliste) {
            var fraDatoItem = item.getFraDato();
            var tilDatoItem = item.getTilDato();

            if (datoUtils.datoErInnenforPeriode(fraDato, fraDatoItem, tilDatoItem)) {
                return true;
            }
        }
        return false;
    }
}
