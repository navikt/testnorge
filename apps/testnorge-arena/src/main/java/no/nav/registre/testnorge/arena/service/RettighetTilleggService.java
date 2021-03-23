package no.nav.registre.testnorge.arena.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils;
import no.nav.registre.testnorge.arena.service.util.ArbeidssoekerUtils;
import no.nav.registre.testnorge.arena.service.util.IdenterUtils;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.arena.consumer.rs.RettighetArenaForvalterConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.TilleggSyntConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetRequest;
import no.nav.registre.testnorge.arena.consumer.rs.request.RettighetTilleggRequest;
import no.nav.registre.testnorge.arena.service.util.ServiceUtils;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;

import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.BEGRUNNELSE;

@Slf4j
@Service
@RequiredArgsConstructor
public class RettighetTilleggService {

    private final TilleggSyntConsumer tilleggSyntConsumer;
    private final RettighetArenaForvalterConsumer rettighetArenaForvalterConsumer;
    private final RettighetTiltakService rettighetTiltakService;
    private final ServiceUtils serviceUtils;
    private final IdenterUtils identerUtils;
    private final ArbeidssoekerUtils arbeidssoekerUtils;
    private final ConsumerUtils consumerUtils;

    public static final LocalDate ARENA_TILLEGG_TILSYN_FAMILIEMEDLEMMER_DATE_LIMIT = LocalDate.of(2020, 02, 29);

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadBoutgifter(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettBoutgifter(syntRequest));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadDagligReise(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettDagligReise(syntRequest));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadFlytting(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettFlytting(syntRequest));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadLaeremidler(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettLaeremidler(syntRequest));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadHjemreise(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettHjemreise(syntRequest));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadReiseObligatoriskSamling(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettReiseObligatoriskSamling(syntRequest));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadTilsynBarn(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettTilsynBarn(syntRequest));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadTilsynFamiliemedlemmer(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter, ARENA_TILLEGG_TILSYN_FAMILIEMEDLEMMER_DATE_LIMIT);
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettTilsynFamiliemedlemmer(syntRequest));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadTilsynBarnArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettTilsynBarnArbeidssoekere(syntRequest));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadTilsynFamiliemedlemmerArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettTilsynFamiliemedlemmerArbeidssoekere(syntRequest));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadBoutgifterArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettBoutgifterArbeidssoekere(syntRequest));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadDagligReiseArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettDagligReiseArbeidssoekere(syntRequest));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadFlyttingArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettFlyttingArbeidssoekere(syntRequest));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadLaeremidlerArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettLaeremidlerArbeidssoekere(syntRequest));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadHjemreiseArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettHjemreiseArbeidssoekere(syntRequest));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadReiseObligatoriskSamlingArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettReiseObligatoriskSamlingArbeidssoekere(syntRequest));
    }

    public Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenadReisestoenadArbeidssoekere(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        var syntRequest = consumerUtils.createSyntRequest(antallNyeIdenter);
        return opprettTilleggsstoenad(avspillergruppeId, miljoe, antallNyeIdenter, tilleggSyntConsumer.opprettReisestoenadArbeidssoekere(syntRequest));
    }

    private Map<String, List<NyttVedtakResponse>> opprettTilleggsstoenad(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter,
            List<NyttVedtakTillegg> syntetiserteRettigheter
    ) {
        var utvalgteIdenter = identerUtils.getUtvalgteIdenter(avspillergruppeId, antallNyeIdenter, miljoe);

        List<RettighetRequest> rettigheter = new ArrayList<>();

        for (var syntetisertRettighet : syntetiserteRettigheter) {
            var ident = utvalgteIdenter.remove(utvalgteIdenter.size() - 1);
            syntetisertRettighet.setBegrunnelse(BEGRUNNELSE);

            var rettighetRequest = new RettighetTilleggRequest(Collections.singletonList(syntetisertRettighet));

            rettighetRequest.setPersonident(ident);
            rettighetRequest.setMiljoe(miljoe);

            rettigheter.addAll(rettighetTiltakService.getTiltaksaktivitetRettigheter(ident, miljoe, Collections.singletonList(syntetisertRettighet), false));
            rettigheter.add(rettighetRequest);
        }

        arbeidssoekerUtils.opprettArbeidssoekerTillegg(rettigheter, miljoe);

        var identerMedOpprettedeTillegg = rettighetArenaForvalterConsumer.opprettRettighet(rettigheter);
        serviceUtils.lagreIHodejegeren(identerMedOpprettedeTillegg);

        return identerMedOpprettedeTillegg;
    }
}
