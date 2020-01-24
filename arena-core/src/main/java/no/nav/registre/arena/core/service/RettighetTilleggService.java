package no.nav.registre.arena.core.service;

import static no.nav.registre.arena.core.service.ServiceUtils.BEGRUNNELSE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.registre.arena.core.consumer.rs.RettighetTilleggArenaForvalterConsumer;
import no.nav.registre.arena.core.consumer.rs.TilleggSyntConsumer;
import no.nav.registre.arena.core.consumer.rs.request.RettighetRequest;
import no.nav.registre.arena.core.consumer.rs.request.RettighetTilleggRequest;
import no.nav.registre.arena.domain.vedtak.NyttVedtakResponse;
import no.nav.registre.arena.domain.vedtak.NyttVedtakTillegg;

@Slf4j
@Service
@RequiredArgsConstructor
public class RettighetTilleggService {

    private final TilleggSyntConsumer tilleggSyntConsumer;
    private final RettighetTilleggArenaForvalterConsumer rettighetTilleggArenaForvalterConsumer;
    private final RettighetTiltakService rettighetTiltakService;
    private final ServiceUtils serviceUtils;

    public List<NyttVedtakResponse> opprettTilleggsstoenadLaeremidler(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettLaeremidler(antallNyeIdenter));
    }

    public List<NyttVedtakResponse> opprettTilleggsstoenadBoutgifter(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettBoutgifter(antallNyeIdenter));
    }

    public List<NyttVedtakResponse> opprettTilleggsstoenadFlytting(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettFlytting(antallNyeIdenter));
    }

    public List<NyttVedtakResponse> opprettTilleggsstoenadHjemreise(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettHjemreise(antallNyeIdenter));
    }

    public List<NyttVedtakResponse> opprettTilleggsstoenadReisestoenadArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettReisestoenadArbeidssoekere(antallNyeIdenter));
    }

    public List<NyttVedtakResponse> opprettTilleggsstoenadBoutgifterArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettBoutgifterArbeidssoekere(antallNyeIdenter));
    }

    public List<NyttVedtakResponse> opprettDagligReise(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettDagligReise(antallNyeIdenter));
    }

    public List<NyttVedtakResponse> opprettReiseObligatoriskSamling(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettReiseObligatoriskSamling(antallNyeIdenter));
    }

    private List<NyttVedtakResponse> opprettTilleggsstoenad(
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
        for (var response : aktivitetResponse) {
            if (response.getFeiledeRettigheter() != null && !response.getFeiledeRettigheter().isEmpty()) {
                log.warn("Kunne ikke opprette aktivitet på ident {}", response.getFeiledeRettigheter().get(0).getPersonident());
            }
        }

        return rettighetTilleggArenaForvalterConsumer.opprettRettighet(rettigheter);
    }
}
