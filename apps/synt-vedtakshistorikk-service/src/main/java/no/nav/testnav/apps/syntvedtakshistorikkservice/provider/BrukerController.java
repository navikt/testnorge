package no.nav.testnav.apps.syntvedtakshistorikkservice.provider;

import lombok.RequiredArgsConstructor;

import no.nav.testnav.apps.syntvedtakshistorikkservice.service.IdentService;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.TagsService;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.ArenaForvalterService;
import no.nav.testnav.apps.syntvedtakshistorikkservice.service.ArenaDagpengerService;
import no.nav.testnav.libs.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengerResponseDTO;
import no.nav.testnav.apps.syntvedtakshistorikkservice.provider.request.SyntetiserArenaRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.provider.utils.InputValidator.validateMiljoe;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MAKSIMUM_ALDER;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils.MINIMUM_ALDER;

@RestController
@RequestMapping("api/v1/bruker")
@RequiredArgsConstructor
public class BrukerController {
    private final IdentService identService;
    private final TagsService tagsService;
    private final ArenaForvalterService arenaForvalterService;
    private final ArenaDagpengerService arenaDagpengerService;

    @PostMapping("/oppfoelging")
    public Map<String, NyeBrukereResponse> registrerBrukereIArenaForvalterMedOppfoelging(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        validateMiljoe(syntetiserArenaRequest.getMiljoe());

        var personer = identService.getUtvalgteIdenterIAldersgruppe(
                        syntetiserArenaRequest.getAntallNyeIdenter(),
                        MINIMUM_ALDER,
                        MAKSIMUM_ALDER,
                        false
                );

        if (tagsService.opprettetTagsPaaIdenterOgPartner(personer)) {
            return arenaForvalterService.opprettArbeidssoekereUtenVedtak(
                    personer.stream().map(PersonDTO::getIdent).toList(),
                    syntetiserArenaRequest.getMiljoe());
        } else {
            return Collections.emptyMap();
        }
    }

    @PostMapping("/dagpenger")
    public Map<String, List<DagpengerResponseDTO>> registrerBrukereIArenaMedDagpenger(
            @RequestBody SyntetiserArenaRequest syntetiserArenaRequest
    ) {
        return arenaDagpengerService.registrerArenaBrukereMedDagpenger(syntetiserArenaRequest.getAntallNyeIdenter(), syntetiserArenaRequest.getMiljoe(), true);
    }

}
