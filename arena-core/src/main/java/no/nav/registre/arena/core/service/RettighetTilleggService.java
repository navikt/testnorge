package no.nav.registre.arena.core.service;

import static no.nav.registre.arena.core.service.util.ServiceUtils.BEGRUNNELSE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import no.nav.registre.arena.core.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.TilleggSyntConsumer;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTilleggRequest;
import no.nav.registre.arena.core.service.util.ServiceUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class RettighetTilleggService {

    private final TilleggSyntConsumer tilleggSyntConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;
    private final RettighetTiltakService rettighetTiltakService;
    private final ServiceUtils serviceUtils;

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadBoutgifter(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettBoutgifter(antallNyeIdenter));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadDagligReise(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettDagligReise(antallNyeIdenter));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadFlytting(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettFlytting(antallNyeIdenter));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadLaeremidler(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettLaeremidler(antallNyeIdenter));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadHjemreise(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettHjemreise(antallNyeIdenter));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadReiseObligatoriskSamling(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettReiseObligatoriskSamling(antallNyeIdenter));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadTilsynBarn(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettTilsynBarn(antallNyeIdenter));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadTilsynFamiliemedlemmer(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettTilsynFamiliemedlemmer(antallNyeIdenter));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadTilsynBarnArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettTilsynBarnArbeidssoekere(antallNyeIdenter));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadTilsynFamiliemedlemmerArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettTilsynFamiliemedlemmerArbeidssoekere(antallNyeIdenter));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadBoutgifterArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettBoutgifterArbeidssoekere(antallNyeIdenter));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadDagligReiseArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettDagligReiseArbeidssoekere(antallNyeIdenter));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadFlyttingArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettFlyttingArbeidssoekere(antallNyeIdenter));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadLaeremidlerArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettLaeremidlerArbeidssoekere(antallNyeIdenter));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadHjemreiseArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettHjemreiseArbeidssoekere(antallNyeIdenter));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadReiseObligatoriskSamlingArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettReiseObligatoriskSamlingArbeidssoekere(antallNyeIdenter));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadReisestoenadArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettReisestoenadArbeidssoekere(antallNyeIdenter));
    }

    private Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenad(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter,
            List<NyttVedtakTillegg> syntetiserteRettigheter
    ) {
        var utvalgteIdenter = serviceUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter);

        List<RettighetRequest> rettigheter = new ArrayList<>(syntetiserteRettigheter.size());
        for (var syntetisertRettighet : syntetiserteRettigheter) {
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);

            var rettighetRequest = new RettighetTilleggRequest(Collections.singletonList(syntetisertRettighet));

            rettighetRequest.setPersonident(utvalgteIdenter.remove(utvalgteIdenter.size() - 1));
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.add(rettighetRequest);
        }

        serviceUtils.opprettArbeidssoekerTillegg(rettigheter, miljoe);

        var aktivitetResponse = rettighetTiltakService.opprettTiltaksaktiviteter(rettigheter);
        for (var response : aktivitetResponse.values()) {
            for (var vedtakResponse : response) {
                if (vedtakResponse.getFeiledeRettigheter() != null && !vedtakResponse.getFeiledeRettigheter().isEmpty()) {
                    log.error("Kunne ikke opprette aktivitet på ident {}", vedtakResponse.getFeiledeRettigheter().get(0).getPersonident());
                }
            }
        }

        return rettighetArenaForvalterConsumer.opprettRettighet(rettigheter);
    }
}
