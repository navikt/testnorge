package no.nav.registre.testnorge.arena.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.TiltakArenaForvalterConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetEndreDeltakerstatusRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetFinnTiltakRequest;
import no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils;
import no.nav.registre.testnorge.arena.service.util.ServiceUtils;
import no.nav.registre.testnorge.arena.service.util.ArbeidssoekerUtils;
import no.nav.registre.testnorge.arena.service.util.IdenterUtils;
import no.nav.registre.testnorge.arena.service.util.KodeMedSannsynlighet;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Deltakerstatuser;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtak;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.arena.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.TiltakSyntConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTiltaksaktivitetRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.tilleggsstoenad.Vedtaksperiode;

import static java.time.temporal.ChronoUnit.DAYS;
import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.BEGRUNNELSE;

@Slf4j
@Service
@RequiredArgsConstructor
public class RettighetTiltakService {


    private final ConsumerUtils consumerUtils;
    private final TiltakSyntConsumer tiltakSyntConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;
    private final ServiceUtils serviceUtils;
    private final IdenterUtils identerUtils;
    private final ArbeidssoekerUtils arbeidsoekerUtils;
    private final TiltakArenaForvalterConsumer tiltakArenaForvalterConsumer;
    private final Random rand;

    private static final Map<String, List<KodeMedSannsynlighet>> vedtakMedAktitivetskode;
    private static final Map<String, List<KodeMedSannsynlighet>> vedtakMedStatuskoder;
    private static final Map<String, List<String>> deltakerstatuskoderMedAarsakkoder;
    private static final Map<String, List<KodeMedSannsynlighet>> adminkodeTilDeltakerstatus;
    private static final Map<String, Map<String, List<String>>> innsatsTilTiltakKoder;

    private static final List<String> AVBRUTT_TILTAK_STATUSER = new ArrayList<>(Arrays.asList("AVLYST", "AVBRUTT"));

    static {
        deltakerstatuskoderMedAarsakkoder = new HashMap<>();
        deltakerstatuskoderMedAarsakkoder.put(Deltakerstatuser.NEITAKK.toString(), Arrays.asList("ANN", "BEGA", "FRISM", "FTOAT", "HENLU", "SYK", "UTV"));
        deltakerstatuskoderMedAarsakkoder.put(Deltakerstatuser.IKKEM.toString(), Arrays.asList("ANN", "BEGA", "SYK"));
        deltakerstatuskoderMedAarsakkoder.put(Deltakerstatuser.DELAVB.toString(), Arrays.asList("ANN", "BEGA", "FTOAT", "SYK"));

        adminkodeTilDeltakerstatus = new HashMap<>();
        vedtakMedAktitivetskode = new HashMap<>();
        innsatsTilTiltakKoder = new HashMap<>();
        vedtakMedStatuskoder = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();

        URL resourceAktivitetkoder = Resources.getResource("files/vedtak_til_aktivitetkode.json");
        URL resourceStatuskoder = Resources.getResource("files/vedtak_til_statuskode.json");
        URL resourceDeltakerstatus = Resources.getResource("files/adminkode_til_deltakerstatus_endring.json");
        URL resourceOversikt = Resources.getResource("files/innsats_til_tiltakkode.json");

        try {
            Map<String, List<KodeMedSannsynlighet>> map = objectMapper.readValue(resourceAktivitetkoder, new TypeReference<>() {
            });
            vedtakMedAktitivetskode.putAll(map);

            Map<String, List<KodeMedSannsynlighet>> mapStatuskoder = objectMapper.readValue(resourceStatuskoder, new TypeReference<>() {
            });
            vedtakMedStatuskoder.putAll(mapStatuskoder);

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

        List<NyttVedtakTiltak> nyVedtaksliste = new ArrayList<>();

        var vedtakSequences = getVedtakSequencesTiltak(vedtaksliste);
        var brukteIndices = new ArrayList<Integer>();

        for (var sequence : vedtakSequences) {
            var initialFraDato = sequence.get(0).getFraDato();

            var deltakelseIndex = finnNoedvendigTiltaksdeltakelse(tiltaksdeltakelser, brukteIndices);
            if (deltakelseIndex != null) {
                brukteIndices.add(deltakelseIndex);
                var deltakelse = tiltaksdeltakelser.get(deltakelseIndex);
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
            List<NyttVedtakAap> aapVedtak
    ) {

        if (vedtaksliste == null || vedtaksliste.isEmpty()) {
            return vedtaksliste;
        }

        var relatedVedtak = getVedtakForSequencesAap(aapVedtak);

        List<NyttVedtakTiltak> nyeVedtak = new ArrayList<>();

        for (var vedtak : vedtaksliste) {
            if (nyeVedtak.isEmpty() || (harIkkeOverlappendeVedtak(vedtak, relatedVedtak) &&
                    harIkkeOverlappendeTiltakOver100Prosent(vedtak, nyeVedtak))) {
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
        List<NyttVedtakTiltak> sekvensVedtak = new ArrayList<>();

        var vedtakSequences = getVedtakSequencesTiltak(vedtaksliste);

        for (var sequence : vedtakSequences) {
            var fraDato = sequence.stream().map(NyttVedtakTiltak::getFraDato).filter(Objects::nonNull).min(LocalDate::compareTo).orElse(null);
            var tilDato = sequence.stream().map(NyttVedtakTiltak::getTilDato).filter(Objects::nonNull).max(LocalDate::compareTo).orElse(null);

            var vedtak = new NyttVedtakTiltak();
            vedtak.setFraDato(fraDato);
            vedtak.setTilDato(tilDato);

            if (oppdatertVedtaksliste.isEmpty() || harIkkeOverlappendeVedtak(vedtak, sekvensVedtak)) {
                sekvensVedtak.add(vedtak);
                oppdatertVedtaksliste.addAll(sequence);
            }
        }

        return oppdatertVedtaksliste;
    }

    private boolean harIkkeOverlappendeVedtak(NyttVedtakTiltak vedtak, List<? extends NyttVedtak> vedtaksliste) {
        if (vedtaksliste == null || vedtaksliste.isEmpty()) {
            return true;
        }
        var fraDato = vedtak.getFraDato();
        var tilDato = vedtak.getTilDato();

        for (var item : vedtaksliste) {
            if (datoerOverlapper(fraDato, tilDato, item.getFraDato(), item.getTilDato())) {
                return false;
            }
        }
        return true;
    }

    private boolean harIkkeOverlappendeTiltakOver100Prosent(
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

    private boolean datoerOverlapper(LocalDate fraDatoA, LocalDate tilDatoA, LocalDate fraDatoB, LocalDate tilDatoB) {
        try {
            return fraDatoA.isBefore(tilDatoB) && fraDatoB.isBefore(tilDatoA);
        } catch (Exception e) {
            return false;
        }
    }

    private List<List<NyttVedtakTiltak>> getVedtakSequencesTiltak(List<NyttVedtakTiltak> vedtak) {
        List<List<NyttVedtakTiltak>> vedtakSequences = new ArrayList<>();
        var indices = getIndicesForVedtakSequences(vedtak);

        for (int j = 0; j < indices.size() - 1; j++) {
            vedtakSequences.add(vedtak.subList(indices.get(j), indices.get(j + 1)));
        }

        return vedtakSequences;
    }

    private List<NyttVedtakAap> getVedtakForSequencesAap(List<NyttVedtakAap> aapVedtak) {
        if (aapVedtak == null || aapVedtak.isEmpty()) {
            return Collections.emptyList();
        }

        List<NyttVedtakAap> vedtakForSequences = new ArrayList<>();
        var indices = getIndicesForVedtakSequences(aapVedtak);

        for (int j = 0; j < indices.size() - 1; j++) {
            var sequence = aapVedtak.subList(indices.get(j), indices.get(j + 1));

            var fraDato = sequence.stream().map(NyttVedtakAap::getFraDato).filter(Objects::nonNull).min(LocalDate::compareTo).orElse(null);
            var tilDato = sequence.stream().map(NyttVedtakAap::getTilDato).filter(Objects::nonNull).max(LocalDate::compareTo).orElse(null);

            var vedtak = new NyttVedtakAap();
            vedtak.setFraDato(fraDato);
            vedtak.setTilDato(tilDato);
            vedtakForSequences.add(vedtak);
        }

        return vedtakForSequences;
    }

    public List<Integer> getIndicesForVedtakSequences(
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

    public Map<String, List<NyttVedtakResponse>> opprettTiltaksaktivitet(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);

        var syntRequest = consumerUtils.createSyntRequest(utvalgteIdenter.size());
        var syntetiserteRettigheter = tiltakSyntConsumer.opprettTiltaksaktivitet(syntRequest);

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            var rettighetRequest = new RettighetTiltaksaktivitetRequest(Collections.singletonList(syntetisertRettighet));

            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }
        var responses = rettighetArenaForvalterConsumer
                .opprettRettighet(arbeidsoekerUtils.opprettArbeidssoekerTiltak(rettigheter, miljoe));
        for (var response : responses.values()) {
            for (var nyttVedtakResponse : response) {
                if (!nyttVedtakResponse.getFeiledeRettigheter().isEmpty()) {
                    log.error("Kunne ikke opprette tiltaksaktivitet for alle identer");
                }
            }
        }

        serviceUtils.lagreIHodejegeren(responses);

        return responses;
    }

    List<RettighetTiltaksaktivitetRequest> getTiltaksaktivitetRettigheter(
            String personident,
            String miljoe,
            List<NyttVedtakTillegg> tillegg
    ) {
        if (tillegg != null && !tillegg.isEmpty()) {
            var rettigheter = new ArrayList<RettighetTiltaksaktivitetRequest>();

            var tilleggVedtak = getTilleggVedtakForTiltaksaktivitet(tillegg);

            for (var vedtak : tilleggVedtak) {
                rettigheter.add(opprettRettighetTiltaksaktivitetRequest(personident, miljoe, vedtak));
            }

            return rettigheter;
        }
        return Collections.emptyList();
    }

    private List<NyttVedtakTillegg> getTilleggVedtakForTiltaksaktivitet(
            List<NyttVedtakTillegg> tillegg
    ) {
        var tilleggVedtak = new ArrayList<NyttVedtakTillegg>();

        var nyRettighetIndices = getIndicesForVedtakSequences(tillegg);

        for (int j = 0; j < nyRettighetIndices.size() - 1; j++) {
            var subList = tillegg.subList(nyRettighetIndices.get(j), nyRettighetIndices.get(j + 1));
            var perioder = subList.stream().map(NyttVedtakTillegg::getVedtaksperiode).collect(Collectors.toList());

            var fraDato = perioder.stream().map(Vedtaksperiode::getFom).filter(Objects::nonNull).min(LocalDate::compareTo).orElse(null);
            var tilDato = perioder.stream().map(Vedtaksperiode::getTom).filter(Objects::nonNull).max(LocalDate::compareTo).orElse(null);

            var oppdatertVedtak = new NyttVedtakTillegg();
            oppdatertVedtak.setVedtaksperiode(new Vedtaksperiode(fraDato, tilDato));
            oppdatertVedtak.setRettighetKode(subList.get(0).getRettighetKode());

            tilleggVedtak.add(oppdatertVedtak);
        }

        return tilleggVedtak;
    }

    RettighetTiltaksaktivitetRequest opprettRettighetTiltaksaktivitetRequest(
            String personident,
            String miljoe,
            NyttVedtakTillegg tillegg
    ) {
        var statuskode = serviceUtils.velgKodeBasertPaaSannsynlighet(
                vedtakMedStatuskoder.get("AKTIVITET")).getKode();
        var aktivitetkode = serviceUtils.velgKodeBasertPaaSannsynlighet(
                vedtakMedAktitivetskode.get(tillegg.getRettighetKode())).getKode();

        var nyttVedtakTiltak = new NyttVedtakTiltak();
        nyttVedtakTiltak.setAktivitetStatuskode(statuskode);
        nyttVedtakTiltak.setAktivitetkode(aktivitetkode);
        nyttVedtakTiltak.setBeskrivelse(BEGRUNNELSE);
        nyttVedtakTiltak.setFraDato(tillegg.getVedtaksperiode().getFom());
        nyttVedtakTiltak.setTilDato(tillegg.getVedtaksperiode().getTom());

        var rettighetRequest = new RettighetTiltaksaktivitetRequest(Collections.singletonList(nyttVedtakTiltak));
        rettighetRequest.setPersonident(personident);
        rettighetRequest.setMiljoe(miljoe);

        return rettighetRequest;
    }

}
