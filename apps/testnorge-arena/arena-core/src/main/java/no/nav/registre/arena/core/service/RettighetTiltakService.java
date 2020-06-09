package no.nav.registre.arena.core.service;

import static no.nav.registre.arena.core.service.util.ServiceUtils.BEGRUNNELSE;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.core.consumer.rs.BrukereArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.request.*;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.arena.core.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.TiltakSyntConsumer;
import no.nav.registre.arena.core.service.util.AktivitetskodeMedSannsynlighet;
import no.nav.registre.arena.core.service.util.ServiceUtils;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class RettighetTiltakService {

    private static final String RELASJON_MOR = "MORA";
    private static final String RELASJON_FAR = "FARA";

    private final TiltakSyntConsumer tiltakSyntConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;
    private final ServiceUtils serviceUtils;
    private final Random rand;

    private static final Map<String, List<AktivitetskodeMedSannsynlighet>> vedtakMedAktitivetskode;

    static {
        vedtakMedAktitivetskode = new HashMap<>();
        URL resource = Resources.getResource("vedtak_til_aktivitetkode.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, List<AktivitetskodeMedSannsynlighet>> map = objectMapper.readValue(resource, new TypeReference<>() {
            });
            vedtakMedAktitivetskode.putAll(map);
        } catch (IOException e) {
            log.error("Kunne ikke laste inn aktivitetskoder.", e);
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

        // sender inn endredeltakerstatus
        if (rand.nextDouble() < 1) {
            var identerMedOpprettedeEndreDeltakerstatus = endreDeltakerstatus(
                    identerMedOpprettedeTiltak,
                    miljoe);
            identerMedOpprettedeTiltak.putAll(identerMedOpprettedeEndreDeltakerstatus);
        }

        return identerMedOpprettedeTiltak;
    }

    public Map<String, List<NyttVedtakResponse>> endreDeltakerstatus(
            Map<String, List<NyttVedtakResponse>> identerMedOpprettedeTiltakdeltakelse,
            String miljoe
    ) {

        List<RettighetRequest> rettigheter = new ArrayList<>(identerMedOpprettedeTiltakdeltakelse.size());
        for (String ident : identerMedOpprettedeTiltakdeltakelse.keySet()) {
            var nyeRettigheterTiltak = identerMedOpprettedeTiltakdeltakelse.get(ident).get(0).getNyeRettigheterTiltak();

            if (nyeRettigheterTiltak != null && nyeRettigheterTiltak.size() != 0) {
                var tiltaksdeltakelse = nyeRettigheterTiltak.get(0);

                var syntetisertRettighet = tiltakSyntConsumer.opprettDeltakerstatus(1).get(0);

                var rettighetRequest = new RettighetEndreDeltakerstatusRequest(Collections.singletonList(syntetisertRettighet));

                rettighetRequest.setPersonident(ident);
                rettighetRequest.setMiljoe(miljoe);

                rettighetRequest.getNyeEndreDeltakerstatus().get(0).setTiltakskode("");
                rettighetRequest.getNyeEndreDeltakerstatus().get(0).setTiltakskarakteristikk(tiltaksdeltakelse.getTiltakskarakteristikk());
                rettighetRequest.getNyeEndreDeltakerstatus().get(0).setDato(tiltaksdeltakelse.getFraDato());

                rettigheter.add(rettighetRequest);
            }
        }

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
            log.info("Fant ingen tiltaksdeltakelser å endre.");
            return new HashMap<>();
        }
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
        return responses;
    }

    Map<String, List<NyttVedtakResponse>> opprettTiltaksaktiviteter(List<RettighetRequest> rettigheter) {
        List<RettighetRequest> tiltaksaktiviteter = new ArrayList<>(rettigheter.size());
        for (var rettighet : rettigheter) {
            if (!(rettighet instanceof RettighetTilleggRequest)) {
                throw new RuntimeException("Opprettelse av tiltaksaktivitet er kun støttet for tilleggsstønad");
            }
            RettighetTiltaksaktivitetRequest rettighetRequest = new RettighetTiltaksaktivitetRequest();
            rettighetRequest.setPersonident(rettighet.getPersonident());
            rettighetRequest.setMiljoe(rettighet.getMiljoe());
            NyttVedtakTiltak nyttVedtakTiltak = new NyttVedtakTiltak();
            nyttVedtakTiltak.setAktivitetkode(serviceUtils.velgAktivitetBasertPaaSannsynlighet(vedtakMedAktitivetskode.get(rettighet.getVedtakTillegg().get(0).getRettighetKode())).getAktivitetkode());
            nyttVedtakTiltak.setBeskrivelse(BEGRUNNELSE);
            nyttVedtakTiltak.setFraDato(rettighet.getVedtakTillegg().get(0).getVedtaksperiode().getFom());
            List<NyttVedtakTiltak> nyTiltaksaktivitet = new ArrayList<>(Collections.singletonList(nyttVedtakTiltak));
            rettighetRequest.setNyeTiltaksaktivitet(nyTiltaksaktivitet);
            tiltaksaktiviteter.add(rettighetRequest);
        }

        return rettighetArenaForvalterConsumer.opprettRettighet(tiltaksaktiviteter);
    }

    private List<String> finnIdenterMedBarn(
            Long avspillergruppeId,
            String miljoe,
            int antallIdenter
    ) {
        var foedteIdenter = serviceUtils.getIdenterMedFoedselsmelding(avspillergruppeId);
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
