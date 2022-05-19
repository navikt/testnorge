package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PdlProxyConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.provider.request.SyntetiserArenaRequest;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.VedtakshistorikkService.SYNT_TAGS;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MAKSIMUM_ALDER;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MINIMUM_ALDER;

@Service
@RequiredArgsConstructor
public class BrukerService {

    private final ArenaForvalterService arenaForvalterService;
    private final IdentService identService;
    private final PdlProxyConsumer pdlProxyConsumer;

    public Map<String, NyeBrukereResponse> registrerArenaBrukereMedOppfoelging(
            SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        var identer = identService.getUtvalgteIdenterIAldersgruppe(
                        syntetiserArenaRequest.getAntallNyeIdenter(),
                        MINIMUM_ALDER,
                        MAKSIMUM_ALDER,
                        false
                )
                .stream().map(PersonDTO::getIdent).toList();

        if (pdlProxyConsumer.createTags(identer, SYNT_TAGS)) {
            return arenaForvalterService.opprettArbeidssoekereUtenVedtak(
                    identer,
                    syntetiserArenaRequest.getMiljoe());
        } else {
            return Collections.emptyMap();
        }

    }
}
