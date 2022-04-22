package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet.RettighetRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet.RettighetTiltaksaktivitetRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils;
import no.nav.testnav.libs.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.testnav.libs.domain.dto.arena.testnorge.tilleggsstoenad.Vedtaksperiode;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtak;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils.getRettighetTilleggRequest;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.VedtakUtils.getTilleggSekvenser;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.DatoUtils.datoErInnenforPeriode;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ArenaTilleggService {

    private final RequestUtils requestUtils;

    public static final String MAALGRUPPEKODE_TILKNYTTET_AAP = "NEDSARBEVN";
    public static final String MAALGRUPPEKODE_TILKNYTTET_TILTAKSPENGER = "MOTTILTPEN";
    public static final LocalDate ARENA_TILLEGG_TILSYN_FAMILIEMEDLEMMER_DATE_LIMIT = LocalDate.of(2020, 2, 29);

    public void opprettVedtakTillegg(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter,
            List<NyttVedtakTiltak> tiltak
    ) {
        var tillegg = oppdaterVedtakTillegg(historikk);

        if (nonNull(tillegg) && !tillegg.isEmpty() && !rettigheter.isEmpty()) {
            var tilleggSekvenser = getTilleggSekvenser(tillegg);

            for (var sekvens : tilleggSekvenser) {
                if (tilleggSekvensManglerTiltak(sekvens, tiltak)) {
                    rettigheter.add(getTiltaksaktivitetForTilleggSekvens(personident, miljoe, sekvens));
                }

                for (var vedtak : sekvens) {
                    rettigheter.add(getRettighetTilleggRequest(personident, miljoe, vedtak));
                }
            }
        }
    }

    private List<NyttVedtakTillegg> oppdaterVedtakTillegg(Vedtakshistorikk historikk) {
        var tillegg = historikk.getAlleTilleggVedtak();

        if (isNull(historikk.getAap()) || historikk.getAap().isEmpty()) {
            tillegg = filtrerBortTilleggMedUoensketMaalgruppekode(tillegg, MAALGRUPPEKODE_TILKNYTTET_AAP);
        } else {
            tillegg = filtrerBortTilleggUtenGyldigTilknyttetVedtak(tillegg, historikk.getAap(),
                    MAALGRUPPEKODE_TILKNYTTET_AAP);
        }

        if (isNull(historikk.getTiltakspenger()) || historikk.getTiltakspenger().isEmpty()) {
            tillegg = filtrerBortTilleggMedUoensketMaalgruppekode(tillegg, MAALGRUPPEKODE_TILKNYTTET_TILTAKSPENGER);
        } else {
            tillegg = filtrerBortTilleggUtenGyldigTilknyttetVedtak(tillegg, historikk.getTiltakspenger(),
                    MAALGRUPPEKODE_TILKNYTTET_TILTAKSPENGER);
        }
        return tillegg;

    }

    private RettighetTiltaksaktivitetRequest getTiltaksaktivitetForTilleggSekvens(
            String personident,
            String miljoe,
            List<NyttVedtakTillegg> sekvens
    ) {
        var vedtaksperiode = getVedtaksperiodeForTilleggSekvens(sekvens);
        var rettighetKode = sekvens.get(0).getRettighetKode();

        return requestUtils.getRettighetTiltaksaktivitetRequest(personident, miljoe, rettighetKode, vedtaksperiode);
    }

    private boolean tilleggSekvensManglerTiltak(List<NyttVedtakTillegg> sekvens, List<NyttVedtakTiltak> tiltak) {
        var vedtaksperiode = getVedtaksperiodeForTilleggSekvens(sekvens);

        for (var vedtak : tiltak) {
            if (datoErInnenforPeriode(vedtaksperiode.getFom(), vedtak.getFraDato(), vedtak.getTilDato()) &&
                    (isNull(vedtaksperiode.getTom()) || datoErInnenforPeriode(vedtaksperiode.getTom(), vedtak.getFraDato(), vedtak.getTilDato()))) {
                return false;
            }
        }
        return true;
    }

    private Vedtaksperiode getVedtaksperiodeForTilleggSekvens(List<NyttVedtakTillegg> sekvens) {
        var perioder = sekvens.stream().map(NyttVedtakTillegg::getVedtaksperiode).toList();

        var startdato = perioder.stream().map(Vedtaksperiode::getFom).filter(Objects::nonNull).min(LocalDate::compareTo).orElse(null);
        var sluttdato = perioder.stream().map(Vedtaksperiode::getTom).filter(Objects::nonNull).max(LocalDate::compareTo).orElse(null);

        return new Vedtaksperiode(startdato, sluttdato);
    }

    private List<NyttVedtakTillegg> filtrerBortTilleggMedUoensketMaalgruppekode(
            List<NyttVedtakTillegg> vedtak,
            String maalgruppekode
    ) {
        var filterteVedtak = vedtak;
        if (nonNull(vedtak) && !vedtak.isEmpty()) {
            filterteVedtak = vedtak.stream()
                    .filter(tillegg -> !tillegg.getMaalgruppeKode().equals(maalgruppekode))
                    .toList();
        }
        return filterteVedtak;
    }

    private List<NyttVedtakTillegg> filtrerBortTilleggUtenGyldigTilknyttetVedtak(
            List<NyttVedtakTillegg> vedtak,
            List<? extends NyttVedtak> tilknyttetVedtak,
            String maalgruppekode
    ) {
        var filterteVedtak = vedtak;
        if (nonNull(vedtak) && !vedtak.isEmpty()) {
            filterteVedtak = vedtak.stream().filter(tillegg -> !tillegg.getMaalgruppeKode()
                            .equals(maalgruppekode) || (tillegg.getMaalgruppeKode()
                            .equals(maalgruppekode) && harGyldigTilknyttetVedtak(tillegg, tilknyttetVedtak)))
                    .toList();
        }
        return filterteVedtak;
    }

    private boolean harGyldigTilknyttetVedtak(NyttVedtakTillegg vedtak, List<? extends NyttVedtak> vedtaksliste) {
        if (isNull(vedtaksliste) || vedtaksliste.isEmpty()) {
            return false;
        }
        var fraDato = vedtak.getVedtaksperiode().getFom();

        if (isNull(fraDato)) {
            return false;
        }

        for (var item : vedtaksliste) {
            var fraDatoItem = item.getFraDato();
            var tilDatoItem = item.getTilDato();

            if (datoErInnenforPeriode(fraDato, fraDatoItem, tilDatoItem)) {
                return true;
            }
        }
        return false;
    }

    public List<NyttVedtakTillegg> fjernTilsynFamiliemedlemmerVedtakMedUgyldigeDatoer(List<NyttVedtakTillegg> tilsynFamiliemedlemmer) {
        List<NyttVedtakTillegg> nyTilsynFamiliemedlemmer = new ArrayList<>();
        if (nonNull(tilsynFamiliemedlemmer)) {
            nyTilsynFamiliemedlemmer = tilsynFamiliemedlemmer.stream()
                    .filter(vedtak -> !vedtak.getFraDato().isAfter(ARENA_TILLEGG_TILSYN_FAMILIEMEDLEMMER_DATE_LIMIT))
                    .toList();
        }

        return nyTilsynFamiliemedlemmer.isEmpty() ? null : nyTilsynFamiliemedlemmer;
    }
}
