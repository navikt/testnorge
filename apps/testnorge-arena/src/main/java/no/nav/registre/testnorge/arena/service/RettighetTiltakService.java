package no.nav.registre.testnorge.arena.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils;
import no.nav.registre.testnorge.arena.service.util.ServiceUtils;
import no.nav.registre.testnorge.arena.service.util.ArbeidssoekerUtils;
import no.nav.registre.testnorge.arena.service.util.IdenterUtils;
import no.nav.registre.testnorge.arena.service.util.TiltakUtils;
import no.nav.registre.testnorge.arena.service.util.KodeMedSannsynlighet;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.arena.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.TiltakSyntConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTilleggRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTilleggsytelseRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTiltaksaktivitetRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTiltaksdeltakelseRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTiltakspengerRequest;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Deltakerstatuser;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Kvalifiseringsgrupper;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.tilleggsstoenad.Vedtaksperiode;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.BEGRUNNELSE;

@Slf4j
@Service
@RequiredArgsConstructor
public class RettighetTiltakService {

    private static final String RELASJON_MOR = "MORA";
    private static final String RELASJON_FAR = "FARA";

    private final ConsumerUtils consumerUtils;
    private final TiltakSyntConsumer tiltakSyntConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;
    private final ServiceUtils serviceUtils;
    private final IdenterUtils identerUtils;
    private final ArbeidssoekerUtils arbeidsoekerUtils;
    private final TiltakUtils tiltakUtils;
    private final Random rand;

    private static final Map<String, List<KodeMedSannsynlighet>> vedtakMedAktitivetskode;
    private static final Map<String, List<KodeMedSannsynlighet>> vedtakMedStatuskoder;

    static {
        vedtakMedAktitivetskode = new HashMap<>();
        URL resourceAktivitetkoder = Resources.getResource("files/vedtak_til_aktivitetkode.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, List<KodeMedSannsynlighet>> map = objectMapper.readValue(resourceAktivitetkoder, new TypeReference<>() {
            });
            vedtakMedAktitivetskode.putAll(map);
        } catch (IOException e) {
            log.error("Kunne ikke laste inn aktivitetskoder.", e);
        }

        vedtakMedStatuskoder = new HashMap<>();
        URL resourceStatuskoder = Resources.getResource("files/vedtak_til_statuskode.json");
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
        List<NyttVedtakTiltak> tiltak = new ArrayList<>();
        var rettigheter = hentRettigheterForTiltaksdeltakelse(utvalgteIdenter, miljoe, tiltaksdeltakelser, tiltak);
        var innsendteTiltaksdeltakelser = aktiverTiltaksdeltakelse(rettigheter, miljoe);
        addResponses(responses, innsendteTiltaksdeltakelser);

        var endretDeltakerstatus = hentRettigheterForEndreDeltakerstatus(miljoe, tiltaksdeltakelser, tiltak);
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

        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        var syntetiserteRettigheter = tiltakSyntConsumer.opprettTiltakspenger(syntRequest);

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
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        var syntetiserteRettigheter = tiltakSyntConsumer.opprettBarnetillegg(syntRequest);

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

    RettighetTiltaksaktivitetRequest opprettRettighetTiltaksaktivitetRequest(
            RettighetRequest rettighet,
            boolean erHistoriskAktivitet
    ) {
        var statuskode = serviceUtils.velgKodeBasertPaaSannsynlighet(
                vedtakMedStatuskoder.get("AKTIVITET")).getKode();

        if (!erHistoriskAktivitet) {
            var syntRequest = consumerUtils.createSyntRequest(1);
            statuskode = tiltakSyntConsumer.opprettTiltaksaktivitet(syntRequest).get(0).getAktivitetStatuskode();
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

            var tilleggRequests = getTilleggRequestsForTiltaksaktivitet(personident, miljoe, tillegg);

            for (var request : tilleggRequests) {
                rettigheter.add(opprettRettighetTiltaksaktivitetRequest(request, true));
            }

            return rettigheter;
        }
        return Collections.emptyList();
    }

    private List<RettighetTilleggRequest> getTilleggRequestsForTiltaksaktivitet(
            String personident,
            String miljoe,
            List<NyttVedtakTillegg> tillegg
    ) {
        var tilleggRequests = new ArrayList<RettighetTilleggRequest>();

        ArrayList<Integer> nyRettighetIndices = new ArrayList<>();

        if (tillegg.size() == 1) {
            nyRettighetIndices = (ArrayList<Integer>) Arrays.asList(0, 1);
        } else {
            for (int i = 0; i < tillegg.size(); i++) {
                if (tillegg.get(i).getVedtaktype().equals("O")) {
                    nyRettighetIndices.add(i);
                }
                if (i == tillegg.size() - 1) {
                    nyRettighetIndices.add(i + 1);
                }
            }
        }


        for (int j = 0; j < nyRettighetIndices.size() - 1; j++) {
            var subList = tillegg.subList(nyRettighetIndices.get(j), nyRettighetIndices.get(j + 1));
            var perioder = subList.stream().map(NyttVedtakTillegg::getVedtaksperiode).collect(Collectors.toList());
            var fraDato = Collections.min(perioder.stream().map(Vedtaksperiode::getFom).collect(Collectors.toList()));
            var tilDato = Collections.max(perioder.stream().map(Vedtaksperiode::getTom).collect(Collectors.toList()));

            var oppdatertVedtak = new NyttVedtakTillegg();
            oppdatertVedtak.getVedtaksperiode().setFom(fraDato);
            oppdatertVedtak.getVedtaksperiode().setTom(tilDato);
            oppdatertVedtak.setRettighetKode(subList.get(0).getRettighetKode());
            oppdatertVedtak.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetTilleggRequest(Collections.singletonList(oppdatertVedtak));
            rettighetRequest.setPersonident(personident);
            rettighetRequest.setMiljoe(miljoe);

            tilleggRequests.add(rettighetRequest);
        }

        return tilleggRequests;
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

    private Map<String, List<NyttVedtakResponse>> endreDeltakerstatus(
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
            List<NyttVedtakTiltak> tiltaksdeltakelser,
            List<NyttVedtakTiltak> tiltaksliste
    ) {
        List<RettighetRequest> rettigheter = new ArrayList<>();
        for (var ident : identer) {
            var syntRequest = consumerUtils.createSyntRequest(1);
            var syntetisertDeltakelser = tiltakSyntConsumer.opprettTiltaksdeltakelse(syntRequest);

            if (syntetisertDeltakelser != null && !syntetisertDeltakelser.isEmpty()) {
                var deltakelse = syntetisertDeltakelser.get(0);

                var kvalifiseringsgruppe = rand.nextBoolean() ? Kvalifiseringsgrupper.BATT : Kvalifiseringsgrupper.BFORM;
                arbeidsoekerUtils.opprettArbeidssoekerTiltakdeltakelse(ident, miljoe, kvalifiseringsgruppe);

                var tiltak = tiltakUtils.finnTiltak(ident, miljoe, deltakelse);

                if (tiltak != null) {
                    if (!tiltakUtils.harGyldigTiltakKode(tiltak, kvalifiseringsgruppe)) {
                        tiltak.setTiltakKode(tiltakUtils.getGyldigTiltakKode(tiltak, kvalifiseringsgruppe));
                    }
                    deltakelse.setTiltakId(tiltak.getTiltakId());
                    deltakelse.setFodselsnr(ident);
                    deltakelse.setFraDato(tiltak.getFraDato());
                    deltakelse.setTilDato(tiltak.getTilDato());
                    tiltaksdeltakelser.add(deltakelse);
                    tiltaksliste.add(tiltak);

                    var nyTiltakdeltakelse = tiltakUtils.getVedtakForTiltaksdeltakelseRequest(deltakelse);

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
            List<NyttVedtakTiltak> tiltaksdeltakelser,
            List<NyttVedtakTiltak> tiltak
    ) {
        List<RettighetRequest> rettigheter = new ArrayList<>();
        for (var vedtak : tiltaksdeltakelser) {
            List<String> endringer = getEndringerMedGyldigRekkefoelge(vedtak, tiltak);

            for (var endring : endringer) {
                var rettighetRequest = tiltakUtils.opprettRettighetEndreDeltakerstatusRequest(vedtak.getFodselsnr(),
                        miljoe, vedtak, endring);

                rettigheter.add(rettighetRequest);
            }
        }
        return rettigheter;
    }

    private List<String> getEndringerMedGyldigRekkefoelge(
            NyttVedtakTiltak tiltaksdeltakelse,
            List<NyttVedtakTiltak> tiltak
    ) {
        List<String> endringer = new ArrayList<>();
        if (tiltakUtils.canSetDeltakelseTilGjennomfoeres(tiltaksdeltakelse, tiltak)) {
            endringer = tiltakUtils.getFoersteEndringerDeltakerstatus(tiltaksdeltakelse.getTiltakAdminKode());
        }

        if (!endringer.isEmpty() && endringer.contains(Deltakerstatuser.GJENN.toString()) && tiltakUtils.canSetDeltakelseTilFinished(tiltaksdeltakelse, tiltak)) {
            endringer.add(tiltakUtils.getAvsluttendeDeltakerstatus(tiltaksdeltakelse, tiltak).toString());
        }

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
