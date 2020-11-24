package no.nav.registre.arena.core.service;

import static no.nav.registre.arena.core.service.util.ServiceUtils.BEGRUNNELSE;
import static no.nav.registre.arena.core.service.util.ServiceUtils.DELTAKERSTATUS_GJENNOMFOERES;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.service.util.ServiceUtils;
import no.nav.registre.arena.core.service.util.ArbeidssoekerUtils;
import no.nav.registre.arena.core.service.util.IdenterUtils;
import no.nav.registre.arena.core.service.util.VedtakUtils;
import no.nav.registre.arena.core.service.util.KodeMedSannsynlighet;
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

import no.nav.registre.arena.core.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.TiltakSyntConsumer;
import no.nav.registre.arena.core.consumer.rs.request.RettighetEndreDeltakerstatusRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTilleggRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTilleggsytelseRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTiltaksaktivitetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTiltaksdeltakelseRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTiltakspengerRequest;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;


@Slf4j
@Service
@RequiredArgsConstructor
public class RettighetTiltakService {

    private static final String RELASJON_MOR = "MORA";
    private static final String RELASJON_FAR = "FARA";

    private final TiltakSyntConsumer tiltakSyntConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;
    private final ServiceUtils serviceUtils;
    private final IdenterUtils identerUtils;
    private final ArbeidssoekerUtils arbeidsoekerUtils;
    private final VedtakUtils vedtakUtils;
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
        var utvalgteIdenter = identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);

        List<NyttVedtakTiltak> tiltaksdeltakelser = new ArrayList<>();
        var rettigheter = hentRettigheterForTiltaksdeltakelse(utvalgteIdenter, miljoe, tiltaksdeltakelser);
        var innsendteTiltaksdeltakelser = aktiverTiltaksdeltakelse(rettigheter, miljoe);
        addResponses(responses, innsendteTiltaksdeltakelser);

        var endretDeltakerstatus = hentRettigheterForEndreDeltakerstatus(miljoe, tiltaksdeltakelser);
        var innsendteEndringerDeltakerstatus = endreDeltakerstatus(endretDeltakerstatus);
        addResponses(responses, innsendteEndringerDeltakerstatus);

        return responses;
    }

    public Map<String, List<NyttVedtakResponse>> opprettTiltakspenger(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        List<KontoinfoResponse> identerMedKontonummer = new ArrayList<>();
        if (rand.nextBoolean()) {
            identerMedKontonummer = identerUtils.getIdenterMedKontoinformasjon(avspillergruppeId, miljoe, antallNyeIdenter);
        }
        var utvalgteIdenter = identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);

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
        var utvalgteIdenter = identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);

        var syntetiserteRettigheter = tiltakSyntConsumer.opprettTiltaksaktivitet(utvalgteIdenter.size());

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

    private Map<String, List<NyttVedtakResponse>> aktiverTiltaksdeltakelse(
            List<RettighetRequest> rettigheter,
            String miljoe
    ) {
        if (!rettigheter.isEmpty()) {
            var responses = rettighetArenaForvalterConsumer
                    .opprettRettighet(arbeidsoekerUtils.opprettArbeidssoekerTiltak(rettigheter, miljoe));
            for (var response : responses.values()) {
                for (var nyttVedtakResponse : response) {
                    if (nyttVedtakResponse.getFeiledeRettigheter() != null && !nyttVedtakResponse.getFeiledeRettigheter().isEmpty()) {
                        log.error("Kunne ikke opprette deltakelse for alle identer");
                    }
                }
            }
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
                    if (nyttVedtakResponse.getFeiledeRettigheter() != null && !nyttVedtakResponse.getFeiledeRettigheter().isEmpty()) {
                        log.error("Kunne ikke endre deltakerstatus for alle identer");
                    }
                }
            }
            return responses;
        } else {
            log.info("Endret ingen tiltaksdeltakelser.");
            return new HashMap<>();
        }
    }

    private List<RettighetRequest> hentRettigheterForTiltaksdeltakelse(
            List<String> identer,
            String miljoe,
            List<NyttVedtakTiltak> tiltaksdeltakelser
    ) {
        List<RettighetRequest> rettigheter = new ArrayList<>();
        for (var ident : identer) {
            var syntetisertDeltakelser = tiltakSyntConsumer.opprettTiltaksdeltakelse(1);

            if (syntetisertDeltakelser != null && !syntetisertDeltakelser.isEmpty()) {
                var deltakelse = syntetisertDeltakelser.get(0);

                arbeidsoekerUtils.opprettArbeidssoekerTiltakdeltakelse(ident, miljoe);
                var tiltak = vedtakUtils.finnTiltak(ident, miljoe, deltakelse);

                if (tiltak != null) {
                    deltakelse.setTiltakId(tiltak.getTiltakId());
                    deltakelse.setFodselsnr(ident);
                    deltakelse.setFraDato(tiltak.getFraDato());
                    deltakelse.setTilDato(tiltak.getTilDato());
                    tiltaksdeltakelser.add(deltakelse);

                    var nyTiltakdeltakelse = vedtakUtils.getVedtakForTiltaksdeltakelseRequest(deltakelse);

                    var rettighetRequest = new RettighetTiltaksdeltakelseRequest(Collections.singletonList(nyTiltakdeltakelse));

                    rettighetRequest.setPersonident(ident);
                    rettighetRequest.setMiljoe(miljoe);

                    rettigheter.add(rettighetRequest);
                }
            }
        }
        return rettigheter;
    }

    private List<RettighetRequest> hentRettigheterForEndreDeltakerstatus(
            String miljoe,
            List<NyttVedtakTiltak> tiltaksdeltakelser
    ) {
        List<RettighetRequest> rettigheter = new ArrayList<>();
        for (var vedtak : tiltaksdeltakelser) {
            var deltakerstatus = getDeltakerstatus(vedtak);
            if (deltakerstatus != null) {
                List<String> endringer = getEndringerMedGyldigRekkefoelge(deltakerstatus, vedtak.getTiltakAdminKode());

                for (var endring : endringer) {
                    var rettighetRequest = opprettRettighetEndreDeltakerstatusRequest(vedtak.getFodselsnr(),
                            miljoe, vedtak, endring);

                    rettigheter.add(rettighetRequest);
                }
            }
        }
        return rettigheter;
    }

    public String getDeltakerstatus(NyttVedtakTiltak tiltakdeltakelse) {
        String deltakerstatus = null;
        var fraDato = tiltakdeltakelse.getFraDato();
        var tilDato = tiltakdeltakelse.getTilDato();
        if (fraDato != null && fraDato.isBefore(LocalDate.now().plusDays(1))) {
            deltakerstatus = DELTAKERSTATUS_GJENNOMFOERES;
            if (tilDato != null && tilDato.isBefore(LocalDate.now().plusDays(1))) {
                deltakerstatus = serviceUtils.velgKodeBasertPaaSannsynlighet(
                        vedtakMedStatuskoder.get("AVSLUTTET_DELTAKER")).getKode();
            }
        }
        return deltakerstatus;
    }


    public List<String> getEndringerMedGyldigRekkefoelge(
            String deltakerstatuskode,
            String adminKode
    ) {
        List<String> endringer = new ArrayList<>();

        if (adminKode.equals("AMO")) {
            endringer.add("TILBUD");
            endringer.add("JATAKK");
        }

        if (!deltakerstatuskode.equals(DELTAKERSTATUS_GJENNOMFOERES)) {
            endringer.add(DELTAKERSTATUS_GJENNOMFOERES);
        }
        endringer.add(deltakerstatuskode);

        return endringer;
    }

    private List<String> finnIdenterMedBarn(
            Long avspillergruppeId,
            String miljoe,
            int antallIdenter
    ) {
        var foedteIdenter = identerUtils.getIdenterMedFoedselsmelding(avspillergruppeId, 17);

        Collections.shuffle(foedteIdenter);
        List<String> utvalgteIdenter = new ArrayList<>(antallIdenter);

        for (var ident : foedteIdenter) {
            var relasjonsResponse = identerUtils.getRelasjonerTilIdent(ident, miljoe);

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
