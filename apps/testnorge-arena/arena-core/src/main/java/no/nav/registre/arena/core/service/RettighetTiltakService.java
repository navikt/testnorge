package no.nav.registre.arena.core.service;

import static no.nav.registre.arena.core.service.util.ServiceUtils.BEGRUNNELSE;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetEndreDeltakerstatusRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTilleggRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTilleggsytelseRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTiltaksaktivitetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTiltaksdeltakelseRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTiltakspengerRequest;
import no.nav.registre.arena.core.service.util.KodeMedSannsynlighet;
import no.nav.registre.arena.core.service.util.ServiceUtils;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class RettighetTiltakService {

    private static final String RELASJON_MOR = "MORA";
    private static final String RELASJON_FAR = "FARA";
    private static final List<String> IGNORED_DELTAKERSTATUSKODER = Arrays.asList("TILBUD", "AKTUELL", "VENTELISTE");

    private final TiltakSyntConsumer tiltakSyntConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;
    private final ServiceUtils serviceUtils;
    private final Random rand;

    private static final Map<String, List<KodeMedSannsynlighet>> vedtakMedAktitivetskode;
    public static final Map<String, List<KodeMedSannsynlighet>> vedtakMedStatuskoder;
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
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);

        var identerMedOpprettedeTiltak = aktiverTiltaksdeltakelse(utvalgteIdenter, miljoe);

        serviceUtils.lagreIHodejegeren(identerMedOpprettedeTiltak);

        return identerMedOpprettedeTiltak;
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
        aktiverTiltaksdeltakelse(utvalgteIdenter, miljoe);

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
        aktiverTiltaksdeltakelse(utvalgteIdenter, miljoe);

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


    private Map<String, List<NyttVedtakResponse>> aktiverTiltaksdeltakelse(
            List<String> identer,
            String miljoe
    ) {
        var utvalgteIdenter = new ArrayList<>(identer);
        var syntetiserteRettigheter = tiltakSyntConsumer.opprettTiltaksdeltakelse(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetTiltaksdeltakelseRequest(Collections.singletonList(syntetisertRettighet));

            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        var responses = rettighetArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerTiltak(rettigheter, miljoe));
        for (var response : responses.values()) {
            for (var nyttVedtakResponse : response) {
                if (!nyttVedtakResponse.getFeiledeRettigheter().isEmpty()) {
                    log.error("Kunne ikke opprette deltakelse for alle identer");
                }
            }
        }

        var identerMedOpprettedeEndreDeltakerstatus = endreDeltakerstatus(
                responses,
                rettigheter,
                miljoe);

        return serviceUtils.combineNyttVedtakResponseLists(responses, identerMedOpprettedeEndreDeltakerstatus);
    }


    public Map<String, List<NyttVedtakResponse>> endreDeltakerstatus(
            Map<String, List<NyttVedtakResponse>> identerMedOpprettedeTiltakdeltakelse,
            List<RettighetRequest> rettigheterTiltaksdeltakelse,
            String miljoe
    ) {
        var rettigheter = getRettigheterForEndreDeltakerstatus(
                identerMedOpprettedeTiltakdeltakelse,
                rettigheterTiltaksdeltakelse,
                miljoe,
                false);

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

    public List<RettighetRequest> getRettigheterForEndreDeltakerstatus(
            Map<String, List<NyttVedtakResponse>> identerMedOpprettedeTiltakdeltakelse,
            List<RettighetRequest> rettigheterTiltaksdeltakelse,
            String miljoe,
            boolean erHistoriskStatus
    ) {
        List<RettighetRequest> rettigheter = new ArrayList<>(identerMedOpprettedeTiltakdeltakelse.size());
        for (var entry : identerMedOpprettedeTiltakdeltakelse.entrySet()) {
            var ident = entry.getKey();
            var feiledeRettigheter = identerMedOpprettedeTiltakdeltakelse.get(ident).get(0).getFeiledeRettigheter();

            if (feiledeRettigheter != null && feiledeRettigheter.isEmpty()) {
                List<RettighetRequest> filterteRettigheter = rettigheterTiltaksdeltakelse.stream().
                        filter(request -> request.getPersonident().equals(ident)).collect(Collectors.toList());

                var tiltaksdeltakelse = filterteRettigheter.get(0).getVedtakTiltak().get(0);

                var deltakerstatuskode = serviceUtils.velgKodeBasertPaaSannsynlighet(
                        vedtakMedStatuskoder.get("DELTAKER")).getKode();

                if(!erHistoriskStatus){
                    deltakerstatuskode = tiltakSyntConsumer.opprettDeltakerstatus(1).get(0).getDeltakerstatusKode();
                }

                if (!IGNORED_DELTAKERSTATUSKODER.contains(deltakerstatuskode)) {

                    List<String> endringer = getEndringerMedGyldigRekkefoelge(deltakerstatuskode, tiltaksdeltakelse);

                    for (var endring : endringer) {
                        var rettighetRequest = opprettRettighetEndreDeltakerstatusRequest(ident, miljoe,
                                tiltaksdeltakelse, endring);

                        rettigheter.add(rettighetRequest);
                    }
                }
            }
        }
        return rettigheter;
    }

    public RettighetEndreDeltakerstatusRequest opprettRettighetEndreDeltakerstatusRequest(
            String ident,
            String miljoe,
            NyttVedtakTiltak tiltaksdeltakelse,
            String deltakerstatuskode
    ) {

        NyttVedtakTiltak vedtak = new NyttVedtakTiltak();
        vedtak.setTiltakskarakteristikk(tiltaksdeltakelse.getTiltakskarakteristikk());
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

    public List<String> getEndringerMedGyldigRekkefoelge(String deltakerstatuskode, NyttVedtakTiltak tiltaksdeltakelse) {
        var tiltakskarakteristikk = tiltaksdeltakelse.getTiltakskarakteristikk();
        List<String> endringer = new ArrayList<>();

        if (tiltakskarakteristikk.equals("AMO")) {
            endringer.add("TILBUD");
            endringer.add("JATAKK");
        }
        endringer.add("GJENN");

        if (!deltakerstatuskode.equals("GJENN")) {
            if(ChronoUnit.MONTHS.between(tiltaksdeltakelse.getFraDato(), LocalDate.now()) > 3){
                endringer.add(deltakerstatuskode);
            }
        }

        return endringer;
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
        var responses = rettighetArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoekerTiltak(rettigheter, miljoe));
        for (var response : responses.values()) {
            for (var nyttVedtakResponse : response) {
                if (!nyttVedtakResponse.getFeiledeRettigheter().isEmpty()) {
                    log.error("Kunne ikke opprette deltakelse for alle identer");
                }
            }
        }

        serviceUtils.lagreIHodejegeren(responses);

        return responses;
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
            } else {
                continue;
            }

            if (utvalgteIdenter.size() >= antallIdenter) {
                break;
            }
        }
        return utvalgteIdenter;
    }
}
