package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

@Slf4j
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

    public List<NyttVedtakAap> opprettArenaAap(SyntetiserArenaAapRequest aapRequest) {
        List<NyttVedtakAap> arenaRespons = new ArrayList<>();

        var nyeVedtakAap = aapConsumer.opprettRettighetAap(SyntetiserArenaRequest.builder()
                .avspillergruppeId(aapRequest.getAvspillergruppeId())
                .miljoe(aapRequest.getMiljoe())
                .antallNyeIdenter(aapRequest.getAntallAap())
                .build());
        byggAapResponse("aap", nyeVedtakAap, arenaRespons);

        var nyeVedtakUngUfoer = aapConsumer.opprettRettighetAapUngUfoer(SyntetiserArenaRequest.builder()
                .avspillergruppeId(aapRequest.getAvspillergruppeId())
                .miljoe(aapRequest.getMiljoe())
                .antallNyeIdenter(aapRequest.getAntallUngUfoer())
                .build());
        byggAapResponse("ung-ufør", nyeVedtakUngUfoer, arenaRespons);

        var nyeVedtakTvungenForvaltning = aapConsumer.opprettRettighetAapTvungenForvaltning(SyntetiserArenaRequest.builder()
                .avspillergruppeId(aapRequest.getAvspillergruppeId())
                .miljoe(aapRequest.getMiljoe())
                .antallNyeIdenter(aapRequest.getAntallTvungenForvaltning())
                .build());
        byggAapResponse("tvungen forvaltning", nyeVedtakTvungenForvaltning, arenaRespons);

        var nyeVedtakFritakMeldekort = aapConsumer.opprettRettighetAapFritakMeldekort(SyntetiserArenaRequest.builder()
                .avspillergruppeId(aapRequest.getAvspillergruppeId())
                .miljoe(aapRequest.getMiljoe())
                .antallNyeIdenter(aapRequest.getAntallFritakMeldekort())
                .build());
        byggAapResponse("fritak meldekort", nyeVedtakFritakMeldekort, arenaRespons);

        return arenaRespons;
    }

    public List<NyttVedtakTiltak> opprettArenaTiltak(SyntetiserArenaTiltakRequest tiltakRequest) {
        List<NyttVedtakTiltak> arenaRespons = new ArrayList<>();

        var nyeTiltakDeltakelse = tiltakConsumer.opprettTiltaksdeltakelse(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tiltakRequest.getAvspillergruppeId())
                .miljoe(tiltakRequest.getMiljoe())
                .antallNyeIdenter(tiltakRequest.getAntallTiltaksdeltakelse())
                .build());
        byggTiltakResponse("tiltaksdeltakelse", nyeTiltakDeltakelse, arenaRespons);

        var nyeTiltakPenger = tiltakConsumer.opprettTiltakspenger(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tiltakRequest.getAvspillergruppeId())
                .miljoe(tiltakRequest.getMiljoe())
                .antallNyeIdenter(tiltakRequest.getAntallTiltakspenger())
                .build());
        byggTiltakResponse("tiltakspenger", nyeTiltakPenger, arenaRespons);

        var nyeTiltakBarnetillegg = tiltakConsumer.opprettBarnetillegg(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tiltakRequest.getAvspillergruppeId())
                .miljoe(tiltakRequest.getMiljoe())
                .antallNyeIdenter(tiltakRequest.getAntallBarnetillegg())
                .build());
        byggTiltakResponse("barnetillegg", nyeTiltakBarnetillegg, arenaRespons);

        return arenaRespons;
    }

    public List<NyttVedtakTillegg> opprettArenaTilleggstoenad(SyntetiserArenaTilleggstoenadRequest tilleggstoenadRequest) {
        List<NyttVedtakTillegg> arenaRespons = new ArrayList<>();

        var nyeTilleggBoutgifter = tilleggstoenadConsumer.opprettBoutgifter(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadRequest.getAntallBoutgifter())
                .build());
        byggTilleggstoenadResponse("boutgifter", nyeTilleggBoutgifter, arenaRespons);

        var nyeTilleggDagligReise = tilleggstoenadConsumer.opprettDagligReise(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadRequest.getAntallDagligReise())
                .build());
        byggTilleggstoenadResponse("daglig reise", nyeTilleggDagligReise, arenaRespons);

        var nyeTilleggFlytting = tilleggstoenadConsumer.opprettFlytting(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadRequest.getAntallFlytting())
                .build());
        byggTilleggstoenadResponse("flytting", nyeTilleggFlytting, arenaRespons);

        var nyeTilleggLaeremidler = tilleggstoenadConsumer.opprettLaeremidler(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadRequest.getAntallLaeremidler())
                .build());
        byggTilleggstoenadResponse("læremidler", nyeTilleggLaeremidler, arenaRespons);

        var nyeTilleggHjemreise = tilleggstoenadConsumer.opprettHjemreise(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadRequest.getAntallHjemreise())
                .build());
        byggTilleggstoenadResponse("hjemreise", nyeTilleggHjemreise, arenaRespons);

        var nyeTilleggReiseObligatoriskSamling = tilleggstoenadConsumer.opprettReiseObligatoriskSamling(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadRequest.getAntallReiseObligatoriskSamling())
                .build());
        byggTilleggstoenadResponse("reise obligatorisk samling", nyeTilleggReiseObligatoriskSamling, arenaRespons);

        var nyeTilleggTilsynBarn = tilleggstoenadConsumer.opprettTilsynBarn(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadRequest.getAntallTilsynBarn())
                .build());
        byggTilleggstoenadResponse("tilsyn barn", nyeTilleggTilsynBarn, arenaRespons);

        var nyeTilleggTilsynFamiliemedlemmer = tilleggstoenadConsumer.opprettTilsynFamiliemedlemmer(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadRequest.getAntallTilsynFamiliemedlemmer())
                .build());
        byggTilleggstoenadResponse("tilsyn familiemedlemmer", nyeTilleggTilsynFamiliemedlemmer, arenaRespons);

        return arenaRespons;
    }

    public List<NyttVedtakTillegg> opprettArenaTilleggstoenadArbeidssoekere(SyntetiserArenaTilleggstoenadArbeidssoekereRequest tilleggstoenadArbeidssoekereRequest) {
        List<NyttVedtakTillegg> arenaRespons = new ArrayList<>();

        var nyeTilleggTilsynBarnArbeidssoeker = tilleggstoenadConsumer.opprettTilsynBarnArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallTilsynBarnArbeidssoekere())
                .build());
        byggTilleggstoenadResponse("tilsyn barn - arbeidssøkere", nyeTilleggTilsynBarnArbeidssoeker, arenaRespons);


        var nyeTilleggTilsynFamiliemedlemmerArbeidssoeker = tilleggstoenadConsumer.opprettTilsynFamiliemedlemmerArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallTilsynFamiliemedlemmerArbeidssoekere())
                .build());
        byggTilleggstoenadResponse("tilsyn familiemedlemmer - arbeidssøkere", nyeTilleggTilsynFamiliemedlemmerArbeidssoeker, arenaRespons);

        var nyeTilleggBoutgifterArbeidssoeker = tilleggstoenadConsumer.opprettBoutgifterArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallBoutgifterArbeidssoekere())
                .build());
        byggTilleggstoenadResponse("boutgifter - arbeidssøkere", nyeTilleggBoutgifterArbeidssoeker, arenaRespons);

        var nyeTilleggDagligReiseArbeidssoeker = tilleggstoenadConsumer.opprettDagligReiseArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallDagligReiseArbeidssoekere())
                .build());
        byggTilleggstoenadResponse("daglig reise - arbeidssøkere", nyeTilleggDagligReiseArbeidssoeker, arenaRespons);

        var nyeTilleggFlyttingArbeidssoeker = tilleggstoenadConsumer.opprettFlyttingArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallFlyttingArbeidssoekere())
                .build());
        byggTilleggstoenadResponse("flytting - arbeidssøkere", nyeTilleggFlyttingArbeidssoeker, arenaRespons);

        var nyeTilleggLaeremidlerArbeidssoeker = tilleggstoenadConsumer.opprettLaeremidlerArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallLaeremidlerArbeidssoekere())
                .build());
        byggTilleggstoenadResponse("læremidler - arbeidssøkere", nyeTilleggLaeremidlerArbeidssoeker, arenaRespons);

        var nyeTilleggHjemreiseArbeidssoeker = tilleggstoenadConsumer.opprettHjemreiseArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallHjemreiseArbeidssoekere())
                .build());
        byggTilleggstoenadResponse("hjemreise - arbeidssøkere", nyeTilleggHjemreiseArbeidssoeker, arenaRespons);

        var nyeTilleggReiseObligatoriskSamlingArbeidssoeker = tilleggstoenadConsumer.opprettReiseObligatoriskSamlingArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallReiseObligatoriskSamlingArbeidssoekere())
                .build());
        byggTilleggstoenadResponse("reise obligatorisk samling - arbeidssøkere", nyeTilleggReiseObligatoriskSamlingArbeidssoeker, arenaRespons);

        var nyeTilleggReisestoenadArbeidssoeker = tilleggstoenadConsumer.opprettReisestoenadArbeidssoekere(SyntetiserArenaRequest.builder()
                .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallReisestoenadArbeidssoekere())
                .build());
        byggTilleggstoenadResponse("reisestønad - arbeidssøkere", nyeTilleggReisestoenadArbeidssoeker, arenaRespons);

        return arenaRespons;
    }

    private void byggAapResponse(String type, List<NyttVedtakResponse> nyeVedtakAap, List<NyttVedtakAap> arenaRespons) {
        for (var vedtak : nyeVedtakAap) {
            log.info("{}: Opprettet {} vedtak. Antall feilede vedtak: {}",
                    type,
                    vedtak.getNyeRettigheterAap() != null ? vedtak.getNyeRettigheterAap().size() : 0,
                    vedtak.getFeiledeRettigheter() != null ? vedtak.getFeiledeRettigheter().size() : 0);
            arenaRespons.addAll(vedtak.getNyeRettigheterAap() != null ? vedtak.getNyeRettigheterAap() : new ArrayList<>());
        }
    }

    private void byggTiltakResponse(String type, List<NyttVedtakResponse> nyeVedtakTiltak, List<NyttVedtakTiltak> arenaRespons) {
        for (var vedtak : nyeVedtakTiltak) {
            log.info("{}: Opprettet {} tiltak. Antall feilede tiltak: {}",
                    type,
                    vedtak.getNyeRettigheterTiltak() != null ? vedtak.getNyeRettigheterTiltak().size() : 0,
                    vedtak.getFeiledeRettigheter() != null ? vedtak.getFeiledeRettigheter().size() : 0);
            arenaRespons.addAll(vedtak.getNyeRettigheterTiltak() != null ? vedtak.getNyeRettigheterTiltak() : new ArrayList<>());
        }
    }

    private void byggTilleggstoenadResponse(String type, List<NyttVedtakResponse> nyeVedtakTilleggstoenad, List<NyttVedtakTillegg> arenaRespons) {
        for (var vedtak : nyeVedtakTilleggstoenad) {
            log.info("{}: Opprettet {} tilleggstønader. Antall feilede tilleggstønader: {}",
                    type,
                    vedtak.getNyeRettigheterTillegg() != null ? vedtak.getNyeRettigheterTillegg().size() : 0,
                    vedtak.getFeiledeRettigheter() != null ? vedtak.getFeiledeRettigheter().size() : 0);
            arenaRespons.addAll(vedtak.getNyeRettigheterTillegg() != null ? vedtak.getNyeRettigheterTillegg() : new ArrayList<>());
        }
    }
}
