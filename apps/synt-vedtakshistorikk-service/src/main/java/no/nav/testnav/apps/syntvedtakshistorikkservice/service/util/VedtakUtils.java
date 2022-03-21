package no.nav.testnav.apps.syntvedtakshistorikkservice.service.util;

import no.nav.testnav.libs.domain.dto.arena.testnorge.tilleggsstoenad.Vedtaksperiode;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtak;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

@Service
public class VedtakUtils {

    private VedtakUtils() {
    }

    public static List<List<NyttVedtakTiltak>> getTiltakSekvenser(List<NyttVedtakTiltak> vedtak) {
        List<List<NyttVedtakTiltak>> sekvenser = new ArrayList<>();
        var indices = getIndicesForVedtakSekvenser(vedtak);

        for (var i = 0; i < indices.size() - 1; i++) {
            sekvenser.add(vedtak.subList(indices.get(i), indices.get(i + 1)));
        }

        return sekvenser;
    }

    public static List<List<NyttVedtakTillegg>> getTilleggSekvenser(List<NyttVedtakTillegg> tilleggsliste) {
        List<List<NyttVedtakTillegg>> sekvenser = new ArrayList<>();
        var indices = getIndicesForVedtakSekvenser(tilleggsliste);

        for (var i = 0; i < indices.size() - 1; i++) {
            sekvenser.add(tilleggsliste.subList(indices.get(i), indices.get(i + 1)));
        }

        return sekvenser;
    }

    public static List<Vedtaksperiode> getVedtakperioderForAapSekvenser(List<NyttVedtakAap> aapVedtak) {
        if (isNull(aapVedtak) || aapVedtak.isEmpty()) {
            return Collections.emptyList();
        }

        List<Vedtaksperiode> vedtaksperioder = new ArrayList<>();
        var indices = getIndicesForVedtakSekvenser(aapVedtak);

        for (var i = 0; i < indices.size() - 1; i++) {
            var sequence = aapVedtak.subList(indices.get(i), indices.get(i + 1));

            var fraDato = sequence.stream().map(NyttVedtakAap::getFraDato).filter(Objects::nonNull).min(LocalDate::compareTo).orElse(null);
            var tilDato = sequence.stream().map(NyttVedtakAap::getTilDato).filter(Objects::nonNull).max(LocalDate::compareTo).orElse(null);

            vedtaksperioder.add(new Vedtaksperiode(fraDato, tilDato));
        }

        return vedtaksperioder;
    }

    private static List<Integer> getIndicesForVedtakSekvenser(
            List<? extends NyttVedtak> vedtak
    ) {
        List<Integer> nyRettighetIndices = new ArrayList<>();

        if (vedtak.size() == 1) {
            nyRettighetIndices = Arrays.asList(0, 1);
        } else {
            for (int i = 0; i < vedtak.size(); i++) {
                if (vedtak.get(i).getVedtaktype().equals("O")) {
                    nyRettighetIndices.add(i);
                }
                if (i == vedtak.size() - 1) {
                    nyRettighetIndices.add(i + 1);
                }
            }
        }

        return nyRettighetIndices;
    }

}
