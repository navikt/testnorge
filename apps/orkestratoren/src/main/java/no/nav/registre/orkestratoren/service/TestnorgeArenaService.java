package no.nav.registre.orkestratoren.service;

import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.consumer.rs.SyntVedtakshistorikkServiceConsumer;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaVedtakshistorikkRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestnorgeArenaService {

    private final SyntVedtakshistorikkServiceConsumer syntVedtakshistorikkServiceConsumer;

    public Map<String, NyeBrukereResponse> opprettArbeidssoekereMedOppfoelgingIArena(SyntetiserArenaRequest arenaRequest) {
        return syntVedtakshistorikkServiceConsumer.opprettArbeidsoekereMedOppfoelging(arenaRequest);
    }

    public void opprettArenaVedtakshistorikk(SyntetiserArenaVedtakshistorikkRequest vedtakshistorikkRequest) {
        syntVedtakshistorikkServiceConsumer.opprettVedtakshistorikk(SyntetiserArenaRequest.builder()
                .miljoe(vedtakshistorikkRequest.getMiljoe())
                .antallNyeIdenter(vedtakshistorikkRequest.getAntallVedtakshistorikker())
                .build());
    }

}
