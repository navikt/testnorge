package no.nav.registre.testnorge.arena.service.util;

import static java.time.temporal.ChronoUnit.DAYS;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.BEGRUNNELSE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.TiltakArenaForvalterConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetEndreDeltakerstatusRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetFinnTiltakRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Deltakerstatuser;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtak;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

@Slf4j
@Service
@RequiredArgsConstructor
public class TiltakUtils {

    private final TiltakArenaForvalterConsumer tiltakArenaForvalterConsumer;
    private final ServiceUtils serviceUtils;
    private final Random rand;

    private static final Map<String, List<String>> deltakerstatuskoderMedAarsakkoder;
    private static final Map<String, List<KodeMedSannsynlighet>> adminkodeTilDeltakerstatus;
    private static final Map<String, Map<String, List<String>>> innsatsTilTiltakKoder;

    private static final List<String> AVBRUTT_TILTAK_STATUSER = new ArrayList<>(Arrays.asList("AVLYST", "AVBRUTT"));
    private static final String PLANLAGT_TILTAK_STATUS = "PLANLAGT";

    static {
        deltakerstatuskoderMedAarsakkoder = new HashMap<>();
        deltakerstatuskoderMedAarsakkoder.put(Deltakerstatuser.NEITAKK.toString(), Arrays.asList("ANN", "BEGA", "FRISM", "FTOAT", "HENLU", "SYK", "UTV"));
        deltakerstatuskoderMedAarsakkoder.put(Deltakerstatuser.IKKEM.toString(), Arrays.asList("ANN", "BEGA", "SYK"));
        deltakerstatuskoderMedAarsakkoder.put(Deltakerstatuser.DELAVB.toString(), Arrays.asList("ANN", "BEGA", "FTOAT", "SYK"));

        adminkodeTilDeltakerstatus = new HashMap<>();

        URL resourceDeltakerstatus = Resources.getResource("files/adminkode_til_deltakerstatus_endring.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, List<KodeMedSannsynlighet>> map = objectMapper.readValue(resourceDeltakerstatus, new TypeReference<>() {
            });

            adminkodeTilDeltakerstatus.putAll(map);
        } catch (IOException e) {
            log.error("Kunne ikke laste inn deltakerstatus fordeling.", e);
        }

        innsatsTilTiltakKoder = new HashMap<>();

        URL resourceOversikt = Resources.getResource("files/innsats_til_tiltakkode.json");
        try {
            Map<String, Map<String, List<String>>> oversiktMap = objectMapper.readValue(resourceOversikt, new TypeReference<>() {
            });

            innsatsTilTiltakKoder.putAll(oversiktMap);
        } catch (IOException e) {
            log.error("Kunne ikke laste inn fordeling for tiltakkoder.", e);
        }
    }

    public List<NyttVedtakTiltak> oppdaterVedtakslisteBasertPaaTiltaksdeltakelse(
            List<NyttVedtakTiltak> vedtaksliste,
            List<NyttVedtakTiltak> tiltaksdeltakelser
    ) {
        if (vedtaksliste == null || vedtaksliste.isEmpty()) {
            return vedtaksliste;
        }

        List<NyttVedtakTiltak> nyVedtaksliste = new ArrayList<>();

        var vedtakSequences = getVedtakSequences(vedtaksliste);

        for (var sequence : vedtakSequences) {
            var initialFraDato = sequence.get(0).getFraDato();
            for (var vedtak : sequence) {
                var deltakelse = finnNoedvendigTiltaksdeltakelse(vedtak, tiltaksdeltakelser);
                if (deltakelse != null) {
                    var nyttVedtak = shiftVedtakDatesBasertPaaTiltaksdeltakelse(vedtak, deltakelse, initialFraDato);
                    nyVedtaksliste.add(nyttVedtak);
                } else if (vedtak.getVedtaktype().equals("O")) {
                    break;
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
                newFraDato = deltakelse.getTilDato();
            }
        }
        vedtak.setFraDato(newFraDato);
        vedtak.setDatoMottatt(newFraDato);
        vedtak.setTilDato(deltakelse.getTilDato());
        return vedtak;
    }

    private NyttVedtakTiltak finnNoedvendigTiltaksdeltakelse(NyttVedtakTiltak vedtak, List<NyttVedtakTiltak> tiltaksdeltakelser) {
        if (tiltaksdeltakelser != null && !tiltaksdeltakelser.isEmpty()) {
            var fraDato = vedtak.getFraDato();
            var tilDato = vedtak.getTilDato();

            if (fraDato != null) {
                for (var deltakelse : tiltaksdeltakelser) {
                    var fraDatoDeltakelse = deltakelse.getFraDato();
                    var tilDatoDeltakelse = deltakelse.getTilDato();

                    if ((fraDatoDeltakelse != null && fraDato.isAfter(fraDatoDeltakelse.minusDays(1))) &&
                            (tilDato == null || tilDatoDeltakelse != null && tilDato.isBefore(tilDatoDeltakelse.plusDays(1)))) {
                        return deltakelse;
                    }

                }
            }
        }
        return null;
    }

    public NyttVedtakTiltak finnTiltak(String personident, String miljoe, NyttVedtakTiltak tiltaksdeltakelse) {
        var finnTiltak = getVedtakForFinnTiltakRequest(tiltaksdeltakelse);

        NyttVedtakTiltak tiltak = null;
        var rettighetRequest = new RettighetFinnTiltakRequest(Collections.singletonList(finnTiltak));

        rettighetRequest.setPersonident(personident);
        rettighetRequest.setMiljoe(miljoe);
        var response = tiltakArenaForvalterConsumer.finnTiltak(rettighetRequest);
        if (response != null && !response.getNyeRettigheterTiltak().isEmpty()) {
            tiltak = response.getNyeRettigheterTiltak().get(0);
        } else {
            log.info("Fant ikke tiltak for tiltakdeltakelse.");
        }
        return tiltak;
    }

    public NyttVedtakTiltak getVedtakForTiltaksdeltakelseRequest(NyttVedtakTiltak syntetiskDeltakelse) {
        var nyTiltaksdeltakelse = NyttVedtakTiltak.builder()
                .lagOppgave(syntetiskDeltakelse.getLagOppgave())
                .tiltakId(syntetiskDeltakelse.getTiltakId())
                .build();
        nyTiltaksdeltakelse.setBegrunnelse(BEGRUNNELSE);
        nyTiltaksdeltakelse.setTilDato(syntetiskDeltakelse.getTilDato());
        nyTiltaksdeltakelse.setFraDato(syntetiskDeltakelse.getFraDato());

        return nyTiltaksdeltakelse;
    }

    private NyttVedtakTiltak getVedtakForFinnTiltakRequest(NyttVedtakTiltak tiltaksdeltakelse) {
        var vedtak = NyttVedtakTiltak.builder()
                .tiltakKode(tiltaksdeltakelse.getTiltakKode())
                .tiltakProsentDeltid(tiltaksdeltakelse.getTiltakProsentDeltid())
                .tiltakVedtak(tiltaksdeltakelse.getTiltakVedtak())
                .tiltakYtelse(tiltaksdeltakelse.getTiltakYtelse())
                .tiltakAdminKode(tiltaksdeltakelse.getTiltakAdminKode())
                .build();
        vedtak.setFraDato(tiltaksdeltakelse.getFraDato());
        vedtak.setTilDato(tiltaksdeltakelse.getTilDato());
        return vedtak;
    }

    public List<NyttVedtakTiltak> removeOverlappingTiltakVedtak(
            List<NyttVedtakTiltak> vedtaksliste,
            List<? extends NyttVedtak> relatedVedtak
    ) {

        if (vedtaksliste == null || vedtaksliste.isEmpty()) {
            return vedtaksliste;
        }

        List<NyttVedtakTiltak> nyeVedtak = new ArrayList<>();

        for (var vedtak : vedtaksliste) {
            if (nyeVedtak.isEmpty() || ((!harOverlappendeVedtak(vedtak, relatedVedtak)) &&
                    !harOverlappendeTiltakOver100Prosent(vedtak, nyeVedtak))) {
                nyeVedtak.add(vedtak);
            }
        }

        return nyeVedtak;
    }

    public List<NyttVedtakTiltak> removeOverlappingTiltakSequences(List<NyttVedtakTiltak> vedtaksliste) {
        if (vedtaksliste == null || vedtaksliste.isEmpty()) {
            return vedtaksliste;
        }

        List<NyttVedtakTiltak> nyeVedtak = new ArrayList<>();

        var vedtakSequences = getVedtakSequences(vedtaksliste);

        for (var sequence : vedtakSequences) {
            var validSequence = true;
            for (var vedtak : sequence) {
                if (!nyeVedtak.isEmpty() && harOverlappendeVedtak(vedtak, nyeVedtak)) {
                    validSequence = false;
                    break;
                }
            }
            if (validSequence) {
                nyeVedtak.addAll(sequence);
            }
        }

        return nyeVedtak;
    }

    private boolean harOverlappendeVedtak(NyttVedtakTiltak vedtak, List<? extends NyttVedtak> vedtaksliste) {
        if (vedtaksliste == null || vedtaksliste.isEmpty()) {
            return false;
        }
        var fraDato = vedtak.getFraDato();
        var tilDato = vedtak.getTilDato();

        if (fraDato == null) {
            return false;
        }

        for (var item : vedtaksliste) {
            var fraDatoItem = item.getFraDato();
            var tilDatoItem = item.getTilDato();

            if (datoerOverlapper(fraDato, tilDato, fraDatoItem, tilDatoItem)) {
                return true;
            }
        }
        return false;
    }

    private boolean harOverlappendeTiltakOver100Prosent(
            NyttVedtakTiltak vedtak,
            List<NyttVedtakTiltak> vedtaksliste
    ) {
        var prosent = vedtak.getTiltakProsentDeltid();
        var fraDato = vedtak.getFraDato();
        var tilDato = vedtak.getTilDato();

        if (fraDato == null) {
            return false;
        }

        for (var item : vedtaksliste) {
            var fraDatoItem = item.getFraDato();
            var tilDatoItem = item.getTilDato();

            if (datoerOverlapper(fraDato, tilDato, fraDatoItem, tilDatoItem)) {
                prosent += item.getTiltakProsentDeltid();
                if (prosent > 100) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean datoerOverlapper(LocalDate fraDatoA, LocalDate tilDatoA, LocalDate fraDatoB, LocalDate tilDatoB) {
        if (fraDatoB == null || (tilDatoA == null && tilDatoB == null)) {
            return false;
        }

        if (tilDatoA == null) {
            return (fraDatoA.isAfter(fraDatoB.minusDays(1)) && fraDatoA.isBefore(tilDatoB));
        } else {
            if (tilDatoB == null) {
                return (fraDatoB.isAfter(fraDatoA.minusDays(1)) && fraDatoB.isBefore(tilDatoA));
            } else {
                return ((fraDatoA == fraDatoB) || (fraDatoA.isBefore(fraDatoB) && tilDatoA.isAfter(fraDatoB)) ||
                        (fraDatoA.isAfter(fraDatoB) && fraDatoA.isBefore(tilDatoB)));
            }

        }
    }

    private List<List<NyttVedtakTiltak>> getVedtakSequences(List<NyttVedtakTiltak> vedtak) {
        List<List<NyttVedtakTiltak>> vedtakSequences = new ArrayList<>();
        List<NyttVedtakTiltak> sequence = new ArrayList<>();
        for (var tiltak : vedtak) {
            if (tiltak.getVedtaktype() != null && tiltak.getVedtaktype().equals("O") && !sequence.isEmpty()) {
                vedtakSequences.add(sequence);
                sequence = new ArrayList<>();
            }
            sequence.add(tiltak);
        }
        if (!sequence.isEmpty()) {
            vedtakSequences.add(sequence);
        }

        return vedtakSequences;
    }

    public boolean canSetDeltakelseTilGjennomfoeres(NyttVedtakTiltak tiltaksdeltakelse, List<NyttVedtakTiltak> tiltak) {
        var tilknyttetTiltak = tiltak.stream().filter(t -> t.getTiltakId().equals(tiltaksdeltakelse.getTiltakId())).collect(Collectors.toList());
        if (!tilknyttetTiltak.isEmpty() && tilknyttetTiltak.get(0) != null) {
            var status = tilknyttetTiltak.get(0).getTiltakStatusKode();
            if (PLANLAGT_TILTAK_STATUS.equals(status)) {
                return false;
            }
        }

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

    public RettighetEndreDeltakerstatusRequest opprettRettighetEndreDeltakerstatusRequest(
            String ident,
            String miljoe,
            NyttVedtakTiltak tiltaksdeltakelse,
            String deltakerstatuskode
    ) {

        NyttVedtakTiltak vedtak = new NyttVedtakTiltak();
        vedtak.setDeltakerstatusKode(deltakerstatuskode);
        vedtak.setTiltakId(tiltaksdeltakelse.getTiltakId());

        if (deltakerstatuskoderMedAarsakkoder.containsKey(deltakerstatuskode)) {
            List<String> aarsakkoder = deltakerstatuskoderMedAarsakkoder.get(deltakerstatuskode);
            String aarsakkode = aarsakkoder.get(rand.nextInt(aarsakkoder.size()));
            vedtak.setAarsakKode(aarsakkode);
        }

        var rettighetRequest = new RettighetEndreDeltakerstatusRequest(Collections.singletonList(vedtak));

        rettighetRequest.setPersonident(ident);
        rettighetRequest.setMiljoe(miljoe);
        return rettighetRequest;
    }

    public boolean harGyldigTiltakKode(NyttVedtakTiltak tiltak, Kvalifiseringsgrupper kvalifiseringsgruppe) {
        var adminKode = tiltak.getTiltakAdminKode();
        var tiltakKode = tiltak.getTiltakKode();
        return innsatsTilTiltakKoder.get(kvalifiseringsgruppe.toString()).get(adminKode).contains(tiltakKode);
    }

    public String getGyldigTiltakKode(NyttVedtakTiltak tiltak, Kvalifiseringsgrupper kvalifiseringsgruppe) {
        var adminKode = tiltak.getTiltakAdminKode();
        var gyldigeTiltakKoder = innsatsTilTiltakKoder.get(kvalifiseringsgruppe.toString()).get(adminKode);
        return gyldigeTiltakKoder.get(rand.nextInt(gyldigeTiltakKoder.size()));
    }

}
