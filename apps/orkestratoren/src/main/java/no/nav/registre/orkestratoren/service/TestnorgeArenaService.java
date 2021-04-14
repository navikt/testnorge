package no.nav.registre.orkestratoren.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeArenaAapConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeArenaConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeArenaVedtakshistorikkConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaAapRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaVedtakshistorikkRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakFeil;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestnorgeArenaService {

    private final TestnorgeArenaConsumer testnorgeArenaConsumer;
    private final TestnorgeArenaVedtakshistorikkConsumer vedtakshistorikkConsumer;
    private final TestnorgeArenaAapConsumer aapConsumer;

    public List<String> opprettArbeidssokereIArena(SyntetiserArenaRequest arenaRequest, boolean medOppfoelging) {
        return testnorgeArenaConsumer.opprettArbeidsoekere(arenaRequest, medOppfoelging);
    }

    public void opprettArenaVedtakshistorikk(SyntetiserArenaVedtakshistorikkRequest vedtakshistorikkRequest) {
        vedtakshistorikkConsumer.opprettVedtakshistorikk(SyntetiserArenaRequest.builder()
                .avspillergruppeId(vedtakshistorikkRequest.getAvspillergruppeId())
                .miljoe(vedtakshistorikkRequest.getMiljoe())
                .antallNyeIdenter(vedtakshistorikkRequest.getAntallVedtakshistorikker())
                .build());
    }

    public List<NyttVedtakAap> opprettArenaAap(SyntetiserArenaAapRequest aapRequest) {
        List<NyttVedtakAap> arenaRespons = new ArrayList<>();

        if (aapRequest.getAntallAap() != null && aapRequest.getAntallAap() > 0) {
            var nyeVedtakAap = aapConsumer.opprettRettighetAap(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(aapRequest.getAvspillergruppeId())
                    .miljoe(aapRequest.getMiljoe())
                    .antallNyeIdenter(aapRequest.getAntallAap())
                    .build());
            byggAapResponse("aap", nyeVedtakAap, arenaRespons);
        }

        if (aapRequest.getAntallUngUfoer() != null && aapRequest.getAntallUngUfoer() > 0) {
            var nyeVedtakUngUfoer = aapConsumer.opprettRettighetAapUngUfoer(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(aapRequest.getAvspillergruppeId())
                    .miljoe(aapRequest.getMiljoe())
                    .antallNyeIdenter(aapRequest.getAntallUngUfoer())
                    .build());
            byggAapResponse("ung-ufør", nyeVedtakUngUfoer, arenaRespons);
        }

        if (aapRequest.getAntallTvungenForvaltning() != null && aapRequest.getAntallTvungenForvaltning() > 0) {
            var nyeVedtakTvungenForvaltning = aapConsumer.opprettRettighetAapTvungenForvaltning(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(aapRequest.getAvspillergruppeId())
                    .miljoe(aapRequest.getMiljoe())
                    .antallNyeIdenter(aapRequest.getAntallTvungenForvaltning())
                    .build());
            byggAapResponse("tvungen forvaltning", nyeVedtakTvungenForvaltning, arenaRespons);
        }

        if (aapRequest.getAntallFritakMeldekort() != null && aapRequest.getAntallFritakMeldekort() > 0) {
            var nyeVedtakFritakMeldekort = aapConsumer.opprettRettighetAapFritakMeldekort(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(aapRequest.getAvspillergruppeId())
                    .miljoe(aapRequest.getMiljoe())
                    .antallNyeIdenter(aapRequest.getAntallFritakMeldekort())
                    .build());
            byggAapResponse("fritak meldekort", nyeVedtakFritakMeldekort, arenaRespons);
        }

        return arenaRespons;
    }

    private void byggAapResponse(
            String type,
            List<NyttVedtakResponse> nyeVedtakAap,
            List<NyttVedtakAap> arenaRespons) {
        List<NyttVedtakAap> nyeRettigheter = new ArrayList<>();
        List<NyttVedtakFeil> feiledeRettigheter = new ArrayList<>();
        for (var vedtak : nyeVedtakAap) {
            nyeRettigheter.addAll(vedtak.getNyeRettigheterAap() != null ? vedtak.getNyeRettigheterAap() : new ArrayList<>());
            feiledeRettigheter.addAll(vedtak.getFeiledeRettigheter() != null ? vedtak.getFeiledeRettigheter() : new ArrayList<>());
        }
        log.info("{}: Opprettet {} vedtak. Antall feilede vedtak: {}",
                type,
                nyeRettigheter.size(),
                feiledeRettigheter.size());
        arenaRespons.addAll(nyeRettigheter);
    }

}
