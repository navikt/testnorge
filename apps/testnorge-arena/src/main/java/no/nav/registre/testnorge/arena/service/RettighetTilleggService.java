package no.nav.registre.testnorge.arena.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTiltaksaktivitetRequest;
import no.nav.registre.testnorge.arena.service.util.DatoUtils;
import no.nav.registre.testnorge.arena.service.util.KodeMedSannsynlighet;
import no.nav.registre.testnorge.arena.service.util.ServiceUtils;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.tilleggsstoenad.Vedtaksperiode;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.BEGRUNNELSE;

@Slf4j
@Service
@RequiredArgsConstructor
public class RettighetTilleggService {

    private final DatoUtils datoUtils;
    private final ServiceUtils serviceUtils;

    private static final Map<String, List<KodeMedSannsynlighet>> vedtakMedAktitivetskode;

    static {
        vedtakMedAktitivetskode = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();

        URL resourceAktivitetkoder = Resources.getResource("files/vedtak_til_aktivitetkode.json");

        try {
            Map<String, List<KodeMedSannsynlighet>> map = objectMapper.readValue(resourceAktivitetkoder, new TypeReference<>() {
            });
            vedtakMedAktitivetskode.putAll(map);

        } catch (IOException e) {
            log.error("Kunne ikke laste inn fordeling.", e);
        }
    }

    List<List<NyttVedtakTillegg>> getTilleggSekvenser(List<NyttVedtakTillegg> tilleggsliste) {

        List<List<NyttVedtakTillegg>> sekvenser = new ArrayList<>();
        List<NyttVedtakTillegg> currentSekvens = new ArrayList<>();

        for (var tillegg : tilleggsliste) {
            if (tillegg.getRettighetType().equals("O") && !currentSekvens.isEmpty()) {
                sekvenser.add(currentSekvens);
                currentSekvens = new ArrayList<>();
            }
            currentSekvens.add(tillegg);
        }

        return sekvenser;
    }

    RettighetRequest getTiltaksaktivitetForTilleggSekvens(
            String personident,
            String miljoe,
            List<NyttVedtakTillegg> sekvens
    ) {
        var vedtaksperiode = getVedtaksperiodeForTilleggSekvens(sekvens);
        var rettighetKode = sekvens.get(0).getRettighetKode();

        return opprettRettighetTiltaksaktivitetRequest(personident, miljoe, rettighetKode, vedtaksperiode);
    }

    private Vedtaksperiode getVedtaksperiodeForTilleggSekvens(List<NyttVedtakTillegg> sekvens) {
        var perioder = sekvens.stream().map(NyttVedtakTillegg::getVedtaksperiode).collect(Collectors.toList());

        var startdato = perioder.stream().map(Vedtaksperiode::getFom).filter(Objects::nonNull).min(LocalDate::compareTo).orElse(null);
        var sluttdato = perioder.stream().map(Vedtaksperiode::getTom).filter(Objects::nonNull).max(LocalDate::compareTo).orElse(null);

        return new Vedtaksperiode(startdato, sluttdato);
    }


    boolean tilleggSekvensManglerTiltak(List<NyttVedtakTillegg> sekvens, List<NyttVedtakTiltak> tiltak) {
        var vedtaksperiode = getVedtaksperiodeForTilleggSekvens(sekvens);

        for (var vedtak : tiltak) {
            if (datoUtils.datoErInnenforPeriode(vedtaksperiode.getFom(), vedtak.getFraDato(), vedtak.getTilDato()) &&
                    (vedtaksperiode.getTom() == null || datoUtils.datoErInnenforPeriode(vedtaksperiode.getTom(), vedtak.getFraDato(), vedtak.getTilDato()))) {
                return false;
            }
        }
        return true;
    }

    RettighetTiltaksaktivitetRequest opprettRettighetTiltaksaktivitetRequest(
            String personident,
            String miljoe,
            String rettighetKode,
            Vedtaksperiode vedtaksperiode
    ) {
        var statuskode = "BEHOV";
        var aktivitetkode = serviceUtils.velgKodeBasertPaaSannsynlighet(
                vedtakMedAktitivetskode.get(rettighetKode)).getKode();

        var nyttVedtakTiltak = new NyttVedtakTiltak();
        nyttVedtakTiltak.setAktivitetStatuskode(statuskode);
        nyttVedtakTiltak.setAktivitetkode(aktivitetkode);
        nyttVedtakTiltak.setBeskrivelse(BEGRUNNELSE);
        nyttVedtakTiltak.setFraDato(vedtaksperiode.getFom());
        nyttVedtakTiltak.setTilDato(vedtaksperiode.getTom());

        var rettighetRequest = new RettighetTiltaksaktivitetRequest(Collections.singletonList(nyttVedtakTiltak));
        rettighetRequest.setPersonident(personident);
        rettighetRequest.setMiljoe(miljoe);

        return rettighetRequest;
    }
}
