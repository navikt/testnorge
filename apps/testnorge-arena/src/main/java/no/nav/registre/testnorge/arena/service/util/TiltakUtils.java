package no.nav.registre.testnorge.arena.service.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Deltakerstatuser;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.testnav.libs.domain.dto.arena.testnorge.tilleggsstoenad.Vedtaksperiode;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static no.nav.registre.testnorge.arena.service.util.DatoUtils.datoerOverlapper;
import static no.nav.registre.testnorge.arena.service.util.DatoUtils.vedtakOverlapperIkkeVedtaksperioder;
import static no.nav.registre.testnorge.arena.service.util.VedtakUtils.getVedtakperioderForAapSekvenser;
import static no.nav.registre.testnorge.arena.service.util.VedtakUtils.getTiltakSekvenser;

@Slf4j
@Service
@RequiredArgsConstructor
public class TiltakUtils {

    private final Random rand = new Random();
    private final ServiceUtils serviceUtils;

    private static final Map<String, List<KodeMedSannsynlighet>> adminkodeTilDeltakerstatus;
    private static final Map<String, Map<String, List<String>>> innsatsTilTiltakKoder;

    private static final List<String> AVBRUTT_TILTAK_STATUSER = new ArrayList<>(Arrays.asList("AVLYST", "AVBRUTT"));

    static {
        adminkodeTilDeltakerstatus = new HashMap<>();
        innsatsTilTiltakKoder = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();

        URL resourceDeltakerstatus = Resources.getResource("files/adminkode_til_deltakerstatus_endring.json");
        URL resourceOversikt = Resources.getResource("files/innsats_til_tiltakkode.json");

        try {
            Map<String, List<KodeMedSannsynlighet>> mapDeltakerstatus = objectMapper.readValue(resourceDeltakerstatus, new TypeReference<>() {
            });
            adminkodeTilDeltakerstatus.putAll(mapDeltakerstatus);

            Map<String, Map<String, List<String>>> oversiktMap = objectMapper.readValue(resourceOversikt, new TypeReference<>() {
            });
            innsatsTilTiltakKoder.putAll(oversiktMap);

        } catch (IOException e) {
            log.error("Kunne ikke laste inn fordeling.", e);
        }
    }

    public List<NyttVedtakTiltak> oppdaterVedtakslisteBasertPaaTiltaksdeltakelse(
            List<NyttVedtakTiltak> vedtaksliste,
            List<NyttVedtakTiltak> tiltaksdeltakelser
    ) {
        if (vedtaksliste == null || vedtaksliste.isEmpty()) {
            return vedtaksliste;
        }

        var deltakelser = tiltaksdeltakelser.stream().filter(t -> t.getTiltakYtelse() != null && t.getTiltakYtelse().equals("J")).collect(Collectors.toList());

        List<NyttVedtakTiltak> nyVedtaksliste = new ArrayList<>();

        var vedtakSequences = getTiltakSekvenser(vedtaksliste);
        var brukteIndices = new ArrayList<Integer>();

        for (var sequence : vedtakSequences) {
            var initialFraDato = sequence.get(0).getFraDato();

            var deltakelseIndex = finnNoedvendigTiltaksdeltakelse(deltakelser, brukteIndices);
            if (deltakelseIndex != null) {
                brukteIndices.add(deltakelseIndex);
                var deltakelse = deltakelser.get(deltakelseIndex);
                for (var vedtak : sequence) {
                    var nyttVedtak = shiftVedtakDatesBasertPaaTiltaksdeltakelse(vedtak, deltakelse, initialFraDato);
                    nyVedtaksliste.add(nyttVedtak);
                }
            }

        }
        return nyVedtaksliste;
    }

    private NyttVedtakTiltak shiftVedtakDatesBasertPaaTiltaksdeltakelse(
            NyttVedtakTiltak vedtak,
            NyttVedtakTiltak deltakelse,
            LocalDate initialFraDato
    ) {
        var newFraDato = deltakelse.getFraDato();
        if (!vedtak.getVedtaktype().equals("O") && initialFraDato != null && vedtak.getFraDato() != null) {
            var initialShift = DAYS.between(initialFraDato, vedtak.getFraDato());
            newFraDato = deltakelse.getFraDato().plusDays(initialShift);

            if (deltakelse.getTilDato() != null && newFraDato.isAfter(deltakelse.getTilDato())) {
                newFraDato = deltakelse.getTilDato().minusDays(1);
            }
        }
        vedtak.setFraDato(newFraDato);
        vedtak.setDatoMottatt(newFraDato);
        vedtak.setTilDato(deltakelse.getTilDato());
        return vedtak;
    }

    private Integer finnNoedvendigTiltaksdeltakelse(List<NyttVedtakTiltak> tiltaksdeltakelser, List<Integer> brukteIndices) {
        if (tiltaksdeltakelser != null && !tiltaksdeltakelser.isEmpty()) {
            for (var i = 0; i < tiltaksdeltakelser.size(); i++) {
                if (!brukteIndices.contains(i)) {
                    return i;
                }
            }
        }
        return null;
    }

    public List<NyttVedtakTiltak> removeOverlappingTiltakVedtak(
            List<NyttVedtakTiltak> vedtaksliste,
            List<NyttVedtakAap> aapVedtak
    ) {
        if (vedtaksliste == null || vedtaksliste.isEmpty()) {
            return vedtaksliste;
        }

        var relatedVedtak = getVedtakperioderForAapSekvenser(aapVedtak);

        List<NyttVedtakTiltak> nyeVedtak = new ArrayList<>();

        for (var vedtak : vedtaksliste) {
            var vedtaksperiode = new Vedtaksperiode(vedtak.getFraDato(), vedtak.getTilDato());
            if (nyeVedtak.isEmpty() || (vedtakOverlapperIkkeVedtaksperioder(vedtaksperiode, relatedVedtak) &&
                    vedtakHarIkkeOverlappOver100Prosent(vedtak, nyeVedtak))) {
                nyeVedtak.add(vedtak);
            }
        }

        return nyeVedtak;
    }

    public List<NyttVedtakTiltak> removeOverlappingTiltakSequences(List<NyttVedtakTiltak> vedtaksliste) {
        if (vedtaksliste == null || vedtaksliste.isEmpty()) {
            return vedtaksliste;
        }

        List<NyttVedtakTiltak> oppdatertVedtaksliste = new ArrayList<>();
        List<Vedtaksperiode> sekvensVedtak = new ArrayList<>();

        var vedtakSequences = getTiltakSekvenser(vedtaksliste);

        for (var sequence : vedtakSequences) {
            var fraDato = sequence.stream().map(NyttVedtakTiltak::getFraDato).filter(Objects::nonNull).min(LocalDate::compareTo).orElse(null);
            var tilDato = sequence.stream().map(NyttVedtakTiltak::getTilDato).filter(Objects::nonNull).max(LocalDate::compareTo).orElse(null);

            var vedtak = new Vedtaksperiode(fraDato, tilDato);

            if (oppdatertVedtaksliste.isEmpty() || vedtakOverlapperIkkeVedtaksperioder(vedtak, sekvensVedtak)) {
                sekvensVedtak.add(vedtak);
                oppdatertVedtaksliste.addAll(sequence);
            }
        }

        return oppdatertVedtaksliste;
    }

    private boolean vedtakHarIkkeOverlappOver100Prosent(
            NyttVedtakTiltak vedtak,
            List<NyttVedtakTiltak> vedtaksliste
    ) {
        var prosent = vedtak.getTiltakProsentDeltid();
        var fraDato = vedtak.getFraDato();
        var tilDato = vedtak.getTilDato();

        for (var item : vedtaksliste) {
            if (datoerOverlapper(fraDato, tilDato, item.getFraDato(), item.getTilDato())) {
                prosent += item.getTiltakProsentDeltid();
                if (prosent > 100) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean canSetDeltakelseTilGjennomfoeres(NyttVedtakTiltak tiltaksdeltakelse) {
        var fraDato = tiltaksdeltakelse.getFraDato();
        return (fraDato != null && fraDato.isBefore(LocalDate.now().plusDays(1)));
    }

    public boolean canSetDeltakelseTilFinished(NyttVedtakTiltak tiltaksdeltakelse, List<NyttVedtakTiltak> tiltak) {
        var tilknyttetTiltak = tiltak.stream().filter(t -> t.getTiltakId().equals(tiltaksdeltakelse.getTiltakId())).collect(Collectors.toList());
        if (!tilknyttetTiltak.isEmpty() && tilknyttetTiltak.get(0) != null) {
            var status = tilknyttetTiltak.get(0).getTiltakStatusKode();
            if (status != null && AVBRUTT_TILTAK_STATUSER.contains(status)) {
                return true;
            }
        }

        var fraDato = tiltaksdeltakelse.getFraDato();
        var tilDato = tiltaksdeltakelse.getTilDato();

        if (fraDato == null || tilDato == null) {
            return false;
        }
        return fraDato.isBefore(LocalDate.now().plusDays(1)) && tilDato.isBefore(LocalDate.now().plusDays(1));
    }

    public List<String> getFoersteEndringerDeltakerstatus(String adminkode) {
        if (adminkodeTilDeltakerstatus.containsKey(adminkode)) {
            var sisteEndring = serviceUtils.velgKodeBasertPaaSannsynlighet(adminkodeTilDeltakerstatus.get(adminkode)).getKode();
            if (adminkode.equals("AMO")) {
                if (sisteEndring.equals(Deltakerstatuser.GJENN.toString()) || sisteEndring.equals(Deltakerstatuser.IKKEM.toString())) {
                    return new ArrayList<>(Arrays.asList(Deltakerstatuser.TILBUD.toString(), Deltakerstatuser.JATAKK.toString(), sisteEndring));
                } else if (sisteEndring.equals(Deltakerstatuser.NEITAKK.toString())) {
                    return new ArrayList<>(Arrays.asList(Deltakerstatuser.TILBUD.toString(), sisteEndring));
                }
            }
            return sisteEndring.equals("") ? new ArrayList<>() : Collections.singletonList(sisteEndring);
        } else {
            log.info("Ugylding tiltak adminkode.");
            return new ArrayList<>();
        }
    }

    public Deltakerstatuser getAvsluttendeDeltakerstatus(NyttVedtakTiltak tiltaksdeltakelse, List<NyttVedtakTiltak> tiltak) {
        var tilknyttetTiltak = tiltak.stream().filter(t -> t.getTiltakId().equals(tiltaksdeltakelse.getTiltakId())).collect(Collectors.toList());
        if (!tilknyttetTiltak.isEmpty() && AVBRUTT_TILTAK_STATUSER.contains(tilknyttetTiltak.get(0).getTiltakStatusKode())) {
            return Deltakerstatuser.DELAVB;
        }
        if (tiltaksdeltakelse.getTiltakAdminKode().equals("INST")) {
            return rand.nextDouble() > 0.28 ? Deltakerstatuser.FULLF : Deltakerstatuser.DELAVB;
        } else {
            return Deltakerstatuser.FULLF;
        }
    }

    public boolean harIkkeGyldigTiltakKode(NyttVedtakTiltak tiltak, Kvalifiseringsgrupper kvalifiseringsgruppe) {
        var adminKode = tiltak.getTiltakAdminKode();
        var tiltakKode = tiltak.getTiltakKode();
        return !innsatsTilTiltakKoder.get(kvalifiseringsgruppe.toString()).get(adminKode).contains(tiltakKode);
    }

    public String getGyldigTiltakKode(NyttVedtakTiltak tiltak, Kvalifiseringsgrupper kvalifiseringsgruppe) {
        var adminKode = tiltak.getTiltakAdminKode();
        var gyldigeTiltakKoder = innsatsTilTiltakKoder.get(kvalifiseringsgruppe.toString()).get(adminKode);
        return gyldigeTiltakKoder.get(rand.nextInt(gyldigeTiltakKoder.size()));
    }
}
