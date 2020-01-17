package no.nav.registre.arena.core.service;

import static no.nav.registre.arena.core.service.ServiceUtils.BEGRUNNELSE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import no.nav.registre.arena.core.consumer.rs.RettighetTiltakArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.TiltakSyntConsumer;
import no.nav.registre.arena.core.consumer.rs.request.RettighetBarnetilleggRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTiltaksdeltakelseRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTiltakspengerRequest;
import no.nav.registre.arena.domain.vedtak.NyttVedtakResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class TiltakService {

    private static final String RELASJON_MOR = "MORA";
    private static final String RELASJON_FAR = "FARA";

    private final TiltakSyntConsumer tiltakSyntConsumer;
    private final RettighetTiltakArenaForvalterConsumer rettighetTiltakArenaForvalterConsumer;
    private final ServiceUtils serviceUtils;
    private final Random rand;

    public List<NyttVedtakResponse> opprettTiltaksdeltakelse(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        return aktiverTiltaksdeltakelse(utvalgteIdenter, miljoe);
    }

    public List<NyttVedtakResponse> opprettTiltakspenger(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntetiserteRettigheter = tiltakSyntConsumer.opprettTiltakspenger(antallNyeIdenter);
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);
        aktiverTiltaksdeltakelse(utvalgteIdenter, miljoe);

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetTiltakspengerRequest(Collections.singletonList(syntetisertRettighet));

            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return rettighetTiltakArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoeker(rettigheter, miljoe));
    }

    public List<NyttVedtakResponse> opprettBarnetillegg(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntetiserteRettigheter = tiltakSyntConsumer.opprettBarnetillegg(antallNyeIdenter);
        var utvalgteIdenter = finnIdenterMedBarn(avspillergruppeId, miljoe, antallNyeIdenter);
        aktiverTiltaksdeltakelse(utvalgteIdenter, miljoe);

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);
            var rettighetRequest = new RettighetBarnetilleggRequest(Collections.singletonList(syntetisertRettighet));

            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        return rettighetTiltakArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoeker(rettigheter, miljoe));
    }

    private List<NyttVedtakResponse> aktiverTiltaksdeltakelse(
            List<String> identer,
            String miljoe
    ) {
        var utvalgteIdenter = new ArrayList<>(identer);
        var syntetiserteRettigheter = tiltakSyntConsumer.opprettTiltaksdeltakelse(utvalgteIdenter.size());

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelseInnsoking(BEGRUNNELSE);
            var rettighetRequest = new RettighetTiltaksdeltakelseRequest(Collections.singletonList(syntetisertRettighet));

            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        List<NyttVedtakResponse> responses = rettighetTiltakArenaForvalterConsumer.opprettRettighet(serviceUtils.opprettArbeidssoeker(rettigheter, miljoe));
        for (var response : responses) {
            if (!response.getFeiledeRettigheter().isEmpty()) {
                log.warn("Kunne ikke opprette deltakelse for alle identer");
            }
        }
        return responses;
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
