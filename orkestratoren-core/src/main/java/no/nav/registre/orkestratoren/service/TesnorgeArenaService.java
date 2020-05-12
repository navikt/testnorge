package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeArenaAapConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeArenaConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeArenaTilleggstoenadConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeArenaTiltakConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeArenaVedtakshistorikkConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaAapRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaTilleggstoenadArbeidssoekereRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaTilleggstoenadRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaTiltakRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaVedtakshistorikkRequest;

@Service
@RequiredArgsConstructor
public class TesnorgeArenaService {

    private final TestnorgeArenaConsumer testnorgeArenaConsumer;
    private final TestnorgeArenaVedtakshistorikkConsumer vedtakshistorikkConsumer;
    private final TestnorgeArenaAapConsumer aapConsumer;
    private final TestnorgeArenaTiltakConsumer tiltakConsumer;
    private final TestnorgeArenaTilleggstoenadConsumer tilleggstoenadConsumer;

    public List<String> opprettArbeidssokereIArena(SyntetiserArenaRequest arenaRequest) {
        return testnorgeArenaConsumer.opprettArbeidsoekere(arenaRequest);
    }

    public void opprettArenaVedtakshistorikk(SyntetiserArenaVedtakshistorikkRequest vedtakshistorikkRequest) {
        vedtakshistorikkConsumer.opprettVedtakshistorikk(SyntetiserArenaRequest.builder()
                .avspillergruppeId(vedtakshistorikkRequest.getAvspillergruppeId())
                .miljoe(vedtakshistorikkRequest.getMiljoe())
                .antallNyeIdenter(vedtakshistorikkRequest.getAntallVedtakshistorikker())
                .build());
    }

    public void opprettArenaAap(SyntetiserArenaAapRequest aapRequest) {
        aapConsumer.opprettRettighetAap(SyntetiserArenaRequest.builder()
                .avspillergruppeId(aapRequest.getAvspillergruppeId())
                .miljoe(aapRequest.getMiljoe())
                .antallNyeIdenter(aapRequest.getAntallAap())
                .build());

        aapConsumer.opprettRettighetAapUngUfoer(SyntetiserArenaRequest.builder()
                .avspillergruppeId(aapRequest.getAvspillergruppeId())
                .miljoe(aapRequest.getMiljoe())
                .antallNyeIdenter(aapRequest.getAntallUngUfoer())
                .build());

        aapConsumer.opprettRettighetAapTvungenForvaltning(SyntetiserArenaRequest.builder()
                .avspillergruppeId(aapRequest.getAvspillergruppeId())
                .miljoe(aapRequest.getMiljoe())
                .antallNyeIdenter(aapRequest.getAntallTvungenForvaltning())
                .build());

        aapConsumer.opprettRettighetAapFritakMeldekort(SyntetiserArenaRequest.builder()
                .avspillergruppeId(aapRequest.getAvspillergruppeId())
                .miljoe(aapRequest.getMiljoe())
                .antallNyeIdenter(aapRequest.getAntallFritakMeldekort())
                .build());
    }

    public void opprettArenaTiltak(SyntetiserArenaTiltakRequest tiltakRequest) {
        tiltakConsumer.opprettTiltaksdeltakelse(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tiltakRequest.getAvspillergruppeId())
                .miljoe(tiltakRequest.getMiljoe())
                .antallNyeIdenter(tiltakRequest.getAntallTiltaksdeltakelse())
                .build());

        tiltakConsumer.opprettTiltakspenger(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tiltakRequest.getAvspillergruppeId())
                .miljoe(tiltakRequest.getMiljoe())
                .antallNyeIdenter(tiltakRequest.getAntallTiltakspenger())
                .build());

        tiltakConsumer.opprettBarnetillegg(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tiltakRequest.getAvspillergruppeId())
                .miljoe(tiltakRequest.getMiljoe())
                .antallNyeIdenter(tiltakRequest.getAntallBarnetillegg())
                .build());
    }

    public void opprettArenaTilleggstoenad(SyntetiserArenaTilleggstoenadRequest tilleggstoenadRequest) {
        tilleggstoenadConsumer.opprettBoutgifter(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadRequest.getAntallBoutgifter())
                .build());

        tilleggstoenadConsumer.opprettDagligReise(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadRequest.getAntallDagligReise())
                .build());

        tilleggstoenadConsumer.opprettFlytting(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadRequest.getAntallFlytting())
                .build());

        tilleggstoenadConsumer.opprettLaeremidler(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadRequest.getAntallLaeremidler())
                .build());

        tilleggstoenadConsumer.opprettHjemreise(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadRequest.getAntallHjemreise())
                .build());

        tilleggstoenadConsumer.opprettReiseObligatoriskSamling(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadRequest.getAntallReiseObligatoriskSamling())
                .build());

        tilleggstoenadConsumer.opprettTilsynBarn(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadRequest.getAntallTilsynBarn())
                .build());

        tilleggstoenadConsumer.opprettTilsynFamiliemedlemmer(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadRequest.getAntallTilsynFamiliemedlemmer())
                .build());
    }

    public void opprettArenaTilleggstoenadArbeidssoekere(SyntetiserArenaTilleggstoenadArbeidssoekereRequest tilleggstoenadArbeidssoekereRequest) {
        tilleggstoenadConsumer.opprettTilsynBarnArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallTilsynBarnArbeidssoekere())
                .build());

        tilleggstoenadConsumer.opprettTilsynFamiliemedlemmerArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallTilsynFamiliemedlemmerArbeidssoekere())
                .build());

        tilleggstoenadConsumer.opprettBoutgifterArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallBoutgifterArbeidssoekere())
                .build());

        tilleggstoenadConsumer.opprettDagligReiseArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallDagligReiseArbeidssoekere())
                .build());

        tilleggstoenadConsumer.opprettFlyttingArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallFlyttingArbeidssoekere())
                .build());

        tilleggstoenadConsumer.opprettLaeremidlerArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallLaeremidlerArbeidssoekere())
                .build());

        tilleggstoenadConsumer.opprettHjemreiseArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallHjemreiseArbeidssoekere())
                .build());

        tilleggstoenadConsumer.opprettReiseObligatoriskSamlingArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallReiseObligatoriskSamlingArbeidssoekere())
                .build());

        tilleggstoenadConsumer.opprettReisestoenadArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallReisestoenadArbeidssoekere())
                .build());
    }
}
