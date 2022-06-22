package no.nav.registre.orkestratoren.service;

import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.consumer.rs.SyntVedtakshistorikkServiceConsumer;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengerResponseDTO;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArenaService {

    private final SyntVedtakshistorikkServiceConsumer syntVedtakshistorikkServiceConsumer;

    public Map<String, NyeBrukereResponse> opprettArbeidssoekereMedOppfoelgingIArena(SyntetiserArenaRequest arenaRequest) {
        return syntVedtakshistorikkServiceConsumer.opprettArbeidsoekereMedOppfoelging(arenaRequest);
    }

    public Map<String, List<NyttVedtakResponse>> opprettArenaVedtakshistorikk(SyntetiserArenaRequest vedtakshistorikkRequest) {
        return syntVedtakshistorikkServiceConsumer.opprettVedtakshistorikk(vedtakshistorikkRequest);
    }

    public Map<String, List<DagpengerResponseDTO>> opprettDagpengerArena(SyntetiserArenaRequest dagpengerRequest){
        return syntVedtakshistorikkServiceConsumer.opprettDagpenger(dagpengerRequest);
    }

}
