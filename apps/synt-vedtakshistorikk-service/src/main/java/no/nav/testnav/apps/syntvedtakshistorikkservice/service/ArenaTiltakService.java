package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet.RettighetRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.KodeMedSannsynlighet;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Deltakerstatuser;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.testnav.libs.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
import no.nav.testnav.libs.domain.dto.arena.testnorge.tilleggsstoenad.Vedtaksperiode;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtak;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;
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

import static java.time.temporal.ChronoUnit.DAYS;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.DatoUtils.datoerOverlapper;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.DatoUtils.vedtakOverlapperIkkeVedtaksperioder;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.VedtakUtils.getVedtakperioderForAapSekvenser;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.VedtakUtils.getTiltakSekvenser;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils.getRettighetTilleggsytelseRequest;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils.getRettighetTiltaksdeltakelseRequest;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils.getRettighetTiltakspengerRequest;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArenaTiltakService {

    private final Random rand = new Random();
    private final ServiceUtils serviceUtils;
    private final RequestUtils requestUtils;
    private final ArenaForvalterService arenaForvalterService;

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

    public void oppdaterTiltaksdeltakelse(
            Vedtakshistorikk historikk,
            PersonDTO person,
            String miljoe,
            List<NyttVedtakTiltak> tiltaksliste,
            NyttVedtak senesteVedtak,
            LocalDate tidligsteDato
    ) {
        var tiltaksdeltakelser = historikk.getTiltaksdeltakelse();
        if (nonNull(tiltaksdeltakelser) && !tiltaksdeltakelser.isEmpty()) {
            Kvalifiseringsgrupper kvalifiseringsgruppe;
            try {
                kvalifiseringsgruppe = arenaForvalterService.opprettArbeidssoekerTiltaksdeltakelse(person, miljoe, senesteVedtak.getRettighetType(), tidligsteDato);
            } catch (Exception e) {
                historikk.setTiltaksdeltakelse(Collections.emptyList());
                return;
            }

            tiltaksdeltakelser.forEach(deltakelse -> {
                if (harIkkeGyldigTiltakKode(deltakelse, kvalifiseringsgruppe)) {
                    deltakelse.setTiltakKode(getGyldigTiltakKode(deltakelse, kvalifiseringsgruppe));
                }
                deltakelse.setFodselsnr(person.getIdent());
                deltakelse.setTiltakYtelse("J");
            });
            tiltaksdeltakelser.forEach(deltakelse -> {
                var tiltak = arenaForvalterService.finnTiltak(person.getIdent(), miljoe, deltakelse);

                if (nonNull(tiltak)) {
                    deltakelse.setTiltakId(tiltak.getTiltakId());
                    deltakelse.setTiltakProsentDeltid(tiltak.getTiltakProsentDeltid());
                    deltakelse.setFraDato(tiltak.getFraDato());
                    deltakelse.setTilDato(tiltak.getTilDato());
                    deltakelse.setTiltakYtelse(tiltak.getTiltakYtelse());
                    tiltaksliste.add(tiltak);
                }
            });

            var nyeTiltaksdeltakelser = tiltaksdeltakelser.stream()
                    .filter(deltakelse -> nonNull(deltakelse.getTiltakId())).toList();

            nyeTiltaksdeltakelser = removeOverlappingTiltakVedtak(nyeTiltaksdeltakelser, historikk.getAap());

            historikk.setTiltaksdeltakelse(nyeTiltaksdeltakelser);
        }
    }

    public void opprettVedtakTiltaksdeltakelse(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var tiltaksdeltakelser = historikk.getTiltaksdeltakelse();
        if (nonNull(tiltaksdeltakelser) && !tiltaksdeltakelser.isEmpty()) {
            for (var deltakelse : tiltaksdeltakelser) {
                rettigheter.add(getRettighetTiltaksdeltakelseRequest(personident, miljoe, deltakelse));
            }
        }
    }

    public void opprettFoersteVedtakEndreDeltakerstatus(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var tiltaksdeltakelser = historikk.getTiltaksdeltakelse();

        var nyeTiltaksedeltakelser = new ArrayList<NyttVedtakTiltak>();

        if (isNull(tiltaksdeltakelser) || tiltaksdeltakelser.isEmpty()) {
            return;
        }
        for (var deltakelse : tiltaksdeltakelser) {
            if (canSetDeltakelseTilGjennomfoeres(deltakelse)) {
                List<String> endringer = getFoersteEndringerDeltakerstatus(deltakelse.getTiltakAdminKode());

                for (var endring : endringer) {
                    var rettighetRequest = requestUtils.getRettighetEndreDeltakerstatusRequest(personident, miljoe,
                            deltakelse, endring);

                    rettigheter.add(rettighetRequest);
                }

                // Hvis siste endring ikke er lik GJENN kan ikke andre tiltak-vedtak (BASI/BTIL) knyttes til tiltaksdeltakelsen.
                if (!endringer.isEmpty() && endringer.get(endringer.size() - 1).equals(Deltakerstatuser.GJENN.toString())) {
                    nyeTiltaksedeltakelser.add(deltakelse);
                }
            }
        }

        historikk.setTiltaksdeltakelse(nyeTiltaksedeltakelser);
    }

    public void opprettAvsluttendeVedtakEndreDeltakerstatus(
            Vedtakshistorikk vedtak,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter,
            List<NyttVedtakTiltak> tiltak
    ) {
        var tiltaksdeltakelser = vedtak.getTiltaksdeltakelse();
        if (nonNull(tiltaksdeltakelser) && !tiltaksdeltakelser.isEmpty()) {
            for (var deltakelse : tiltaksdeltakelser) {
                if (canSetDeltakelseTilFinished(deltakelse, tiltak)) {
                    var deltakerstatuskode = getAvsluttendeDeltakerstatus(deltakelse, tiltak).toString();

                    var rettighetRequest = requestUtils.getRettighetEndreDeltakerstatusRequest(personident, miljoe,
                            deltakelse, deltakerstatuskode);

                    rettigheter.add(rettighetRequest);
                }
            }
        }
    }

    public void opprettVedtakTiltakspenger(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var tiltakspenger = nonNull(historikk.getTiltakspenger()) ? historikk.getTiltakspenger() : new ArrayList<NyttVedtakTiltak>();
        var tiltaksdeltakelser = historikk.getTiltaksdeltakelse();

        List<NyttVedtakTiltak> nyeTiltakspenger = oppdaterVedtakslisteBasertPaaTiltaksdeltakelse(
                tiltakspenger, tiltaksdeltakelser);

        nyeTiltakspenger = removeOverlappingTiltakSequences(nyeTiltakspenger);

        if (nonNull(nyeTiltakspenger) && !nyeTiltakspenger.isEmpty()) {
            for (var vedtak : nyeTiltakspenger) {
                rettigheter.add(getRettighetTiltakspengerRequest(personident, miljoe, vedtak));
            }
        }
        historikk.setTiltakspenger(nyeTiltakspenger);
    }

    public void opprettVedtakBarnetillegg(
            Vedtakshistorikk historikk,
            String personident,
            String miljoe,
            List<RettighetRequest> rettigheter
    ) {
        var barnetillegg = nonNull(historikk.getBarnetillegg()) ? historikk.getBarnetillegg() : new ArrayList<NyttVedtakTiltak>();
        var tiltakspenger = nonNull(historikk.getTiltakspenger()) ? historikk.getTiltakspenger() : new ArrayList<NyttVedtakTiltak>();
        var tiltaksdeltakelser = historikk.getTiltaksdeltakelse();

        List<NyttVedtakTiltak> nyeBarnetillegg = new ArrayList<>();
        if (!tiltakspenger.isEmpty()) {
            nyeBarnetillegg = oppdaterVedtakslisteBasertPaaTiltaksdeltakelse(
                    barnetillegg, tiltaksdeltakelser);

            nyeBarnetillegg = removeOverlappingTiltakSequences(nyeBarnetillegg);

            if (nonNull(nyeBarnetillegg) && !nyeBarnetillegg.isEmpty()) {
                for (var vedtak : nyeBarnetillegg) {
                    rettigheter.add(getRettighetTilleggsytelseRequest(personident, miljoe, vedtak));
                }
            }
        }

        historikk.setBarnetillegg(nyeBarnetillegg);
    }

    private List<NyttVedtakTiltak> oppdaterVedtakslisteBasertPaaTiltaksdeltakelse(
            List<NyttVedtakTiltak> vedtaksliste,
            List<NyttVedtakTiltak> tiltaksdeltakelser
    ) {
        if (isNull(vedtaksliste) || vedtaksliste.isEmpty()) {
            return vedtaksliste;
        }
        if (isNull(tiltaksdeltakelser) || tiltaksdeltakelser.isEmpty()) {
            return Collections.emptyList();
        }

        var deltakelser = tiltaksdeltakelser.stream()
                .filter(t -> nonNull(t.getTiltakYtelse()) && t.getTiltakYtelse().equals("J")).toList();

        List<NyttVedtakTiltak> nyVedtaksliste = new ArrayList<>();

        var vedtakSequences = getTiltakSekvenser(vedtaksliste);
        var brukteIndices = new ArrayList<Integer>();

        for (var sequence : vedtakSequences) {
            var initialFraDato = sequence.get(0).getFraDato();

            var deltakelseIndex = finnNoedvendigTiltaksdeltakelse(deltakelser, brukteIndices);
            if (nonNull(deltakelseIndex)) {
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
        if (!vedtak.getVedtaktype().equals("O") && nonNull(initialFraDato) && nonNull(vedtak.getFraDato())) {
            var initialShift = DAYS.between(initialFraDato, vedtak.getFraDato());
            newFraDato = deltakelse.getFraDato().plusDays(initialShift);

            if (nonNull(deltakelse.getTilDato()) && newFraDato.isAfter(deltakelse.getTilDato())) {
                newFraDato = deltakelse.getTilDato().minusDays(1);
            }
        }
        vedtak.setFraDato(newFraDato);
        vedtak.setDatoMottatt(newFraDato);
        vedtak.setTilDato(deltakelse.getTilDato());
        return vedtak;
    }

    private Integer finnNoedvendigTiltaksdeltakelse(List<NyttVedtakTiltak> tiltaksdeltakelser, List<Integer> brukteIndices) {
        if (nonNull(tiltaksdeltakelser) && !tiltaksdeltakelser.isEmpty()) {
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
        if (isNull(vedtaksliste) || vedtaksliste.isEmpty()) {
            return vedtaksliste;
        }

        var relatedVedtak = getVedtakperioderForAapSekvenser(aapVedtak);

        List<NyttVedtakTiltak> nyeVedtak = new ArrayList<>();

        for (var vedtak : vedtaksliste) {
            var vedtaksperiode = new Vedtaksperiode(vedtak.getFraDato(), vedtak.getTilDato());
            if (vedtakOverlapperIkkeVedtaksperioder(vedtaksperiode, relatedVedtak) &&
                    vedtakHarIkkeOverlappOver100Prosent(vedtak, nyeVedtak)) {
                nyeVedtak.add(vedtak);
            }
        }

        return nyeVedtak;
    }

    private List<NyttVedtakTiltak> removeOverlappingTiltakSequences(List<NyttVedtakTiltak> vedtaksliste) {
        if (isNull(vedtaksliste) || vedtaksliste.isEmpty()) {
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
        if (isNull(vedtaksliste) || vedtaksliste.isEmpty()) {
            return true;
        }
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

    private boolean canSetDeltakelseTilGjennomfoeres(NyttVedtakTiltak tiltaksdeltakelse) {
        var fraDato = tiltaksdeltakelse.getFraDato();
        return (nonNull(fraDato) && fraDato.isBefore(LocalDate.now().plusDays(1)));
    }

    private boolean canSetDeltakelseTilFinished(NyttVedtakTiltak tiltaksdeltakelse, List<NyttVedtakTiltak> tiltak) {
        var tilknyttetTiltak = tiltak.stream()
                .filter(t -> t.getTiltakId().equals(tiltaksdeltakelse.getTiltakId())).toList();
        if (!tilknyttetTiltak.isEmpty() && nonNull(tilknyttetTiltak.get(0))) {
            var status = tilknyttetTiltak.get(0).getTiltakStatusKode();
            if (nonNull(status) && AVBRUTT_TILTAK_STATUSER.contains(status)) {
                return true;
            }
        }

        var fraDato = tiltaksdeltakelse.getFraDato();
        var tilDato = tiltaksdeltakelse.getTilDato();

        if (isNull(fraDato) || isNull(tilDato)) {
            return false;
        }
        return fraDato.isBefore(LocalDate.now().plusDays(1)) && tilDato.isBefore(LocalDate.now().plusDays(1));
    }

    private List<String> getFoersteEndringerDeltakerstatus(String adminkode) {
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

    private Deltakerstatuser getAvsluttendeDeltakerstatus(NyttVedtakTiltak tiltaksdeltakelse, List<NyttVedtakTiltak> tiltak) {
        var tilknyttetTiltak = tiltak.stream()
                .filter(t -> t.getTiltakId().equals(tiltaksdeltakelse.getTiltakId())).toList();
        if (!tilknyttetTiltak.isEmpty() && AVBRUTT_TILTAK_STATUSER.contains(tilknyttetTiltak.get(0).getTiltakStatusKode())) {
            return Deltakerstatuser.DELAVB;
        }
        if (tiltaksdeltakelse.getTiltakAdminKode().equals("INST")) {
            return rand.nextDouble() > 0.28 ? Deltakerstatuser.FULLF : Deltakerstatuser.DELAVB;
        } else {
            return Deltakerstatuser.FULLF;
        }
    }

    private boolean harIkkeGyldigTiltakKode(NyttVedtakTiltak tiltak, Kvalifiseringsgrupper kvalifiseringsgruppe) {
        var adminKode = tiltak.getTiltakAdminKode();
        var tiltakKode = tiltak.getTiltakKode();
        return !innsatsTilTiltakKoder.get(kvalifiseringsgruppe.toString()).get(adminKode).contains(tiltakKode);
    }

    private String getGyldigTiltakKode(NyttVedtakTiltak tiltak, Kvalifiseringsgrupper kvalifiseringsgruppe) {
        var adminKode = tiltak.getTiltakAdminKode();
        var gyldigeTiltakKoder = innsatsTilTiltakKoder.get(kvalifiseringsgruppe.toString()).get(adminKode);
        return gyldigeTiltakKoder.get(rand.nextInt(gyldigeTiltakKoder.size()));
    }
}
