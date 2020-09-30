package no.nav.registre.arena.core.service;

import static no.nav.registre.arena.core.service.util.ServiceUtils.BEGRUNNELSE;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.TiltakArenaForvalterConsumer;
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

import no.nav.registre.arena.core.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.TiltakSyntConsumer;
import no.nav.registre.arena.core.consumer.rs.request.RettighetEndreDeltakerstatusRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTilleggRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTilleggsytelseRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTiltaksaktivitetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTiltaksdeltakelseRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTiltakspengerRequest;
import no.nav.registre.arena.core.service.util.KodeMedSannsynlighet;
import no.nav.registre.arena.core.service.util.ServiceUtils;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

@Slf4j
@Service
@RequiredArgsConstructor
public class RettighetTiltakService {

    private static final String RELASJON_MOR = "MORA";
    private static final String RELASJON_FAR = "FARA";
    private static final List<String> IGNORED_DELTAKERSTATUSKODER = Arrays.asList("TILBUD", "AKTUELL", "VENTELISTE");
    public static final String DELTAKERSTATUS_GJENNOMFOERES = "GJENN";

    private final TiltakSyntConsumer tiltakSyntConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;
    private final TiltakArenaForvalterConsumer tiltakArenaForvalterConsumer;
    private final ServiceUtils serviceUtils;
    private final Random rand;

    private static final Map<String, List<KodeMedSannsynlighet>> vedtakMedAktitivetskode;
    private static final Map<String, List<KodeMedSannsynlighet>> vedtakMedStatuskoder;
    private static final Map<String, List<String>> deltakerstatuskoderMedAarsakkoder;

    static {
        deltakerstatuskoderMedAarsakkoder = new HashMap<>();
        deltakerstatuskoderMedAarsakkoder.put("NEITAKK", Arrays.asList("ANN", "BEGA", "FRISM", "FTOAT", "HENLU", "SYK", "UTV"));
        deltakerstatuskoderMedAarsakkoder.put("IKKEM", Arrays.asList("ANN", "BEGA", "SYK"));
        deltakerstatuskoderMedAarsakkoder.put("DELAVB", Arrays.asList("ANN", "BEGA", "FTOAT", "SYK"));

        vedtakMedAktitivetskode = new HashMap<>();
        URL resourceAktivitetkoder = Resources.getResource("vedtak_til_aktivitetkode.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, List<KodeMedSannsynlighet>> map = objectMapper.readValue(resourceAktivitetkoder, new TypeReference<>() {
            });
            vedtakMedAktitivetskode.putAll(map);
        } catch (IOException e) {
            log.error("Kunne ikke laste inn aktivitetskoder.", e);
        }

        vedtakMedStatuskoder = new HashMap<>();
        URL resourceStatuskoder = Resources.getResource("vedtak_til_statuskode.json");
        try {
            Map<String, List<KodeMedSannsynlighet>> map = objectMapper.readValue(resourceStatuskoder, new TypeReference<>() {
            });
            vedtakMedStatuskoder.putAll(map);
        } catch (IOException e) {
            log.error("Kunne ikke laste inn statuskoder.", e);
        }
    }

    public Map<String, List<NyttVedtakResponse>> opprettTiltaksdeltakelse(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        Map<String, List<NyttVedtakResponse>> responses = new HashMap<>();

        var utvalgteIdenter = serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);

        var tiltaksdeltakelser = hentRettigheterForTiltaksdeltakelse(utvalgteIdenter, miljoe);
        var innsendteTiltaksdeltakelser = aktiverTiltaksdeltakelse(tiltaksdeltakelser, miljoe);
        addResponses(responses, innsendteTiltaksdeltakelser);

        var endretDeltakerstatusGjennomfoert = hentRettigheterForEndreDeltakerstatus(miljoe, tiltaksdeltakelser, false);
        var innsendteEndringerDeltakerstatusGjennomfoert = endreDeltakerstatus(endretDeltakerstatusGjennomfoert);
        addResponses(responses, innsendteEndringerDeltakerstatusGjennomfoert);

        var endretDeltakerstatusAvsluttet = hentRettigheterForEndreDeltakerstatus(miljoe, tiltaksdeltakelser, true);
        var innsendteEndringerDeltakerstatusAvsluttet = endreDeltakerstatus(endretDeltakerstatusAvsluttet);
        addResponses(responses, innsendteEndringerDeltakerstatusAvsluttet);

        return responses;
    }

    public Map<String, List<NyttVedtakResponse>> opprettTiltakspenger(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        List<KontoinfoResponse> identerMedKontonummer = new ArrayList<>();
        if (rand.nextBoolean()) {
            identerMedKontonummer = serviceUtils.getIdenterMedKontoinformasjon(avspillergruppeId, miljoe, antallNyeIdenter);
        }
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);

        var syntetiserteRettigheter = tiltakSyntConsumer.opprettTiltakspenger(antallNyeIdenter);

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);

            if (!identerMedKontonummer.isEmpty()) {
                syntetisertRettighet.setAlternativMottaker(ServiceUtils.buildForvalter(identerMedKontonummer.remove(identerMedKontonummer.size() - 1)));
            }

            var rettighetRequest = new RettighetTiltakspengerRequest(Collections.singletonList(syntetisertRettighet));

            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        var identerMedOpprettedeTiltak = rettighetArenaForvalterConsumer.opprettRettighet(rettigheter);

        serviceUtils.lagreIHodejegeren(identerMedOpprettedeTiltak);

        return identerMedOpprettedeTiltak;
    }


    public Map<String, List<NyttVedtakResponse>> opprettBarnetillegg(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = finnIdenterMedBarn(avspillergruppeId, miljoe, antallNyeIdenter);
        var syntetiserteRettigheter = tiltakSyntConsumer.opprettBarnetillegg(antallNyeIdenter);

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetTilleggsytelseRequest(Collections.singletonList(syntetisertRettighet));

            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }
        var identerMedOpprettedeTiltak = rettighetArenaForvalterConsumer.opprettRettighet(rettigheter);

        serviceUtils.lagreIHodejegeren(identerMedOpprettedeTiltak);

        return identerMedOpprettedeTiltak;
    }

    public RettighetEndreDeltakerstatusRequest opprettRettighetEndreDeltakerstatusRequest(
            String ident,
            String miljoe,
            NyttVedtakTiltak tiltaksdeltakelse,
            String deltakerstatuskode
    ) {

        NyttVedtakTiltak vedtak = new NyttVedtakTiltak();
        vedtak.setTiltakskarakteristikk(tiltaksdeltakelse.getTiltakAdminKode());
        vedtak.setDato(tiltaksdeltakelse.getFraDato());
        vedtak.setDeltakerstatusKode(deltakerstatuskode);

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

    Map<String, List<NyttVedtakResponse>> opprettTiltaksaktiviteter(
            List<RettighetRequest> rettigheter
    ) {
        List<RettighetRequest> tiltaksaktiviteter = new ArrayList<>(rettigheter.size());
        for (var rettighet : rettigheter) {
            if (!(rettighet instanceof RettighetTilleggRequest)) {
                log.error("Opprettelse av tiltaksaktivitet er kun støttet for tilleggsstønad");
                continue;
            }
            if (rettighet.getVedtakTillegg() != null && !rettighet.getVedtakTillegg().isEmpty()) {
                tiltaksaktiviteter.add(opprettRettighetTiltaksaktivitetRequest(rettighet, false));
            }
        }

        return rettighetArenaForvalterConsumer.opprettRettighet(tiltaksaktiviteter);
    }

    RettighetTiltaksaktivitetRequest opprettRettighetTiltaksaktivitetRequest(
            RettighetRequest rettighet,
            boolean erHistoriskAktivitet
    ) {
        var statuskode = serviceUtils.velgKodeBasertPaaSannsynlighet(
                vedtakMedStatuskoder.get("AKTIVITET")).getKode();

        if (!erHistoriskAktivitet) {
            statuskode = tiltakSyntConsumer.opprettTiltaksaktivitet(1).get(0).getAktivitetStatuskode();
        }

        var nyttVedtakTiltak = new NyttVedtakTiltak();

        nyttVedtakTiltak.setAktivitetStatuskode(statuskode);
        nyttVedtakTiltak.setAktivitetkode(serviceUtils.velgKodeBasertPaaSannsynlighet(
                vedtakMedAktitivetskode.get(rettighet.getVedtakTillegg().get(0).getRettighetKode())).getKode());

        nyttVedtakTiltak.setBeskrivelse(BEGRUNNELSE);

        var antallVedtakTillegg = rettighet.getVedtakTillegg().size();
        nyttVedtakTiltak.setFraDato(rettighet.getVedtakTillegg().get(0).getVedtaksperiode().getFom());
        nyttVedtakTiltak.setTilDato(rettighet.getVedtakTillegg().get(antallVedtakTillegg - 1).getVedtaksperiode().getTom());

        var rettighetRequest = new RettighetTiltaksaktivitetRequest(Collections.singletonList(nyttVedtakTiltak));
        rettighetRequest.setPersonident(rettighet.getPersonident());
        rettighetRequest.setMiljoe(rettighet.getMiljoe());

        return rettighetRequest;
    }

    public Map<String, List<NyttVedtakResponse>> opprettTiltaksaktivitet(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);

        var syntetiserteRettigheter = tiltakSyntConsumer.opprettTiltaksaktivitet(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            var rettighetRequest = new RettighetTiltaksaktivitetRequest(Collections.singletonList(syntetisertRettighet));

            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }
        var responses = rettighetArenaForvalterConsumer
                .opprettRettighet(serviceUtils.opprettArbeidssoekerTiltak(rettigheter, miljoe));
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

    private Map<String, List<NyttVedtakResponse>> aktiverTiltaksdeltakelse(
            List<RettighetRequest> rettigheter,
            String miljoe
    ) {
        if (!rettigheter.isEmpty()) {
            var responses = rettighetArenaForvalterConsumer
                    .opprettRettighet(serviceUtils.opprettArbeidssoekerTiltak(rettigheter, miljoe));
            for (var response : responses.values()) {
                for (var nyttVedtakResponse : response) {
                    if (!nyttVedtakResponse.getFeiledeRettigheter().isEmpty()) {
                        log.error("Kunne ikke opprette deltakelse for alle identer");
                    }
                }
            }
            serviceUtils.lagreIHodejegeren(responses);
            return responses;
        } else {
            log.info("Aktiverte ingen tiltaksdeltakelser.");
            return new HashMap<>();
        }
    }

    public Map<String, List<NyttVedtakResponse>> endreDeltakerstatus(
            List<RettighetRequest> rettigheter
    ) {
        if (!rettigheter.isEmpty()) {
            var responses = rettighetArenaForvalterConsumer.opprettRettighet(rettigheter);

            for (var response : responses.values()) {
                for (var nyttVedtakResponse : response) {
                    if (!nyttVedtakResponse.getFeiledeRettigheter().isEmpty()) {
                        log.error("Kunne ikke endre deltakerstatus for alle identer");
                    }
                }
            }
            serviceUtils.lagreIHodejegeren(responses);
            return responses;
        } else {
            log.info("Endret ingen tiltaksdeltakelser.");
            return new HashMap<>();
        }
    }

    private List<RettighetRequest> hentRettigheterForTiltaksdeltakelse(
            List<String> identer,
            String miljoe
    ) {
        var utvalgteIdenter = new ArrayList<>(identer);
        List<RettighetRequest> rettigheter = new ArrayList<>();

        for (var ident : utvalgteIdenter) {
            var syntetisertDeltakelser = tiltakSyntConsumer.opprettTiltaksdeltakelse(1);
            syntetisertDeltakelser.forEach(deltakelse -> deltakelse.setFodselsnr(ident));
            var nyeTiltaksdeltakelser =
                    tiltakArenaForvalterConsumer.finnTiltak(syntetisertDeltakelser);

            if (nyeTiltaksdeltakelser != null && !nyeTiltaksdeltakelser.isEmpty()) {
                var rettighet = nyeTiltaksdeltakelser.get(0);
                rettighet.setBegrunnelse(BEGRUNNELSE);
                var rettighetRequest = new RettighetTiltaksdeltakelseRequest(Collections.singletonList(rettighet));

                rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
                rettighetRequest.setMiljoe(miljoe);

                rettigheter.add(rettighetRequest);
            }
        }
        return rettigheter;
    }

    private List<RettighetRequest> hentRettigheterForEndreDeltakerstatus(
            String miljoe,
            List<RettighetRequest> tiltaksdeltakelser,
            boolean erAvsluttet

    ) {
        List<RettighetRequest> rettigheter = new ArrayList<>();

        for (var deltakelse : tiltaksdeltakelser) {
            var vedtakTiltak = deltakelse.getVedtakTiltak();
            for (var vedtak : vedtakTiltak) {
                if (erAvsluttet) {
                    rettigheter.addAll(hentRettigheterForEndreDeltakerstatusAvsluttet(vedtak, miljoe));
                } else {
                    rettigheter.addAll(hentRettigheterForEndreDeltakerstatusGjennomfoeres(vedtak, miljoe));
                }
            }
        }
        return rettigheter;
    }

    public List<RettighetRequest> hentRettigheterForEndreDeltakerstatusGjennomfoeres(
            NyttVedtakTiltak tiltaksdeltakelse,
            String miljoe
    ) {
        List<RettighetRequest> rettigheter = new ArrayList<>();

        var fraDato = tiltaksdeltakelse.getFraDato();
        if (fraDato != null && fraDato.isBefore(LocalDate.now().plusDays(1))) {
            List<String> endringer = getEndringerMedGyldigRekkefoelge(DELTAKERSTATUS_GJENNOMFOERES, tiltaksdeltakelse);

            for (var endring : endringer) {
                var rettighetRequest = opprettRettighetEndreDeltakerstatusRequest(tiltaksdeltakelse.getFodselsnr(),
                        miljoe, tiltaksdeltakelse, endring);

                rettigheter.add(rettighetRequest);
            }
        }

        return rettigheter;
    }

    public List<RettighetRequest> hentRettigheterForEndreDeltakerstatusAvsluttet(
            NyttVedtakTiltak tiltaksdeltakelse,
            String miljoe
    ) {
        List<RettighetRequest> rettigheter = new ArrayList<>();

        var fraDato = tiltaksdeltakelse.getFraDato();
        var tilDato = tiltaksdeltakelse.getTilDato();

        if (fraDato != null && fraDato.isBefore(LocalDate.now().plusDays(1))
                && tilDato != null && tilDato.isAfter(LocalDate.now().minusDays(1))) {
            var deltakerstatus = serviceUtils.velgKodeBasertPaaSannsynlighet(
                    vedtakMedStatuskoder.get("AVSLUTTET_DELTAKER")).getKode();

            var rettighetRequest = opprettRettighetEndreDeltakerstatusRequest(tiltaksdeltakelse.getFodselsnr(),
                    miljoe, tiltaksdeltakelse, deltakerstatus);

            rettigheter.add(rettighetRequest);
        }


        return rettigheter;
    }

    public List<String> getEndringerMedGyldigRekkefoelge(String deltakerstatuskode, NyttVedtakTiltak tiltaksdeltakelse) {
        var tiltakskarakteristikk = tiltaksdeltakelse.getTiltakskarakteristikk();
        List<String> endringer = new ArrayList<>();

        if (tiltakskarakteristikk.equals("AMO")) {
            endringer.add("TILBUD");
            endringer.add("JATAKK");
        }

        if (!deltakerstatuskode.equals("GJENN")) {
            endringer.add("GJENN");
        }
        endringer.add(deltakerstatuskode);

        return endringer;
    }

    private List<String> finnIdenterMedBarn(
            Long avspillergruppeId,
            String miljoe,
            int antallIdenter
    ) {
        var foedteIdenter = serviceUtils.getIdenterMedFoedselsmelding(avspillergruppeId, 17);

        Collections.shuffle(foedteIdenter);
        List<String> utvalgteIdenter = new ArrayList<>(antallIdenter);

        for (var ident : foedteIdenter) {
            var relasjonsResponse = serviceUtils.getRelasjonerTilIdent(ident, miljoe);

            var farFnr = "";
            var morFnr = "";

            for (var relasjon : relasjonsResponse.getRelasjoner()) {
                if (RELASJON_MOR.equals(relasjon.getTypeRelasjon())) {
                    morFnr = relasjon.getFnrRelasjon();
                } else if (RELASJON_FAR.equals(relasjon.getTypeRelasjon())) {
                    farFnr = relasjon.getFnrRelasjon();
                }
            }

            setUtvalgteIdenterFoedselsnr(utvalgteIdenter, farFnr, morFnr);

            if (utvalgteIdenter.size() >= antallIdenter) {
                break;
            }
        }
        return utvalgteIdenter;
    }

    private void setUtvalgteIdenterFoedselsnr(List<String> utvalgteIdenter, String farFnr, String morFnr) {
        if (!morFnr.isEmpty() && !farFnr.isEmpty()) {
            if (rand.nextBoolean()) {
                utvalgteIdenter.add(morFnr);
            } else {
                utvalgteIdenter.add(farFnr);
            }
        } else if (!morFnr.isEmpty()) {
            utvalgteIdenter.add(morFnr);
        } else if (!farFnr.isEmpty()) {
            utvalgteIdenter.add(farFnr);
        }
    }

    public Map<String, List<KodeMedSannsynlighet>> getVedtakMedStatuskoder() {
        return vedtakMedStatuskoder;
    }

    private void addResponses(
            Map<String, List<NyttVedtakResponse>> opprinneligeResponses,
            Map<String, List<NyttVedtakResponse>> nyeResponses
    ) {
        for (var entry : nyeResponses.entrySet()) {
            var ident = entry.getKey();
            if (opprinneligeResponses.containsKey(ident)) {
                opprinneligeResponses.get(ident).addAll(nyeResponses.get(ident));
            } else {
                opprinneligeResponses.put(ident, nyeResponses.get(ident));
            }
        }
    }

}
