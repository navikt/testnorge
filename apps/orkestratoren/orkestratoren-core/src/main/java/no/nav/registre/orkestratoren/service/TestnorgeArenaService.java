package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakFeil;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestnorgeArenaService {

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

    public List<NyttVedtakTiltak> opprettArenaTiltak(SyntetiserArenaTiltakRequest tiltakRequest) {
        List<NyttVedtakTiltak> arenaRespons = new ArrayList<>();

        if (tiltakRequest.getAntallTiltaksdeltakelse() != null && tiltakRequest.getAntallTiltaksdeltakelse() > 0) {
            var nyeTiltakDeltakelse = tiltakConsumer.opprettTiltaksdeltakelse(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tiltakRequest.getAvspillergruppeId())
                    .miljoe(tiltakRequest.getMiljoe())
                    .antallNyeIdenter(tiltakRequest.getAntallTiltaksdeltakelse())
                    .build());
            byggTiltakResponse("tiltaksdeltakelse", nyeTiltakDeltakelse, arenaRespons);
        }

        if (tiltakRequest.getAntallTiltakspenger() != null && tiltakRequest.getAntallTiltakspenger() > 0) {
            var nyeTiltakPenger = tiltakConsumer.opprettTiltakspenger(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tiltakRequest.getAvspillergruppeId())
                    .miljoe(tiltakRequest.getMiljoe())
                    .antallNyeIdenter(tiltakRequest.getAntallTiltakspenger())
                    .build());
            byggTiltakResponse("tiltakspenger", nyeTiltakPenger, arenaRespons);
        }

        if (tiltakRequest.getAntallBarnetillegg() != null && tiltakRequest.getAntallBarnetillegg() > 0) {
            var nyeTiltakBarnetillegg = tiltakConsumer.opprettBarnetillegg(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tiltakRequest.getAvspillergruppeId())
                    .miljoe(tiltakRequest.getMiljoe())
                    .antallNyeIdenter(tiltakRequest.getAntallBarnetillegg())
                    .build());
            byggTiltakResponse("barnetillegg", nyeTiltakBarnetillegg, arenaRespons);
        }

        return arenaRespons;
    }

    public List<NyttVedtakTillegg> opprettArenaTilleggstoenad(SyntetiserArenaTilleggstoenadRequest tilleggstoenadRequest) {
        List<NyttVedtakTillegg> arenaRespons = new ArrayList<>();

        if (tilleggstoenadRequest.getAntallBoutgifter() != null && tilleggstoenadRequest.getAntallBoutgifter() > 0) {
            var nyeTilleggBoutgifter = tilleggstoenadConsumer.opprettBoutgifter(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                    .miljoe(tilleggstoenadRequest.getMiljoe())
                    .antallNyeIdenter(tilleggstoenadRequest.getAntallBoutgifter())
                    .build());
            byggTilleggstoenadResponse("boutgifter", nyeTilleggBoutgifter, arenaRespons);
        }

        if (tilleggstoenadRequest.getAntallDagligReise() != null && tilleggstoenadRequest.getAntallDagligReise() > 0) {
            var nyeTilleggDagligReise = tilleggstoenadConsumer.opprettDagligReise(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                    .miljoe(tilleggstoenadRequest.getMiljoe())
                    .antallNyeIdenter(tilleggstoenadRequest.getAntallDagligReise())
                    .build());
            byggTilleggstoenadResponse("daglig reise", nyeTilleggDagligReise, arenaRespons);
        }

        if (tilleggstoenadRequest.getAntallFlytting() != null && tilleggstoenadRequest.getAntallFlytting() > 0) {
            var nyeTilleggFlytting = tilleggstoenadConsumer.opprettFlytting(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                    .miljoe(tilleggstoenadRequest.getMiljoe())
                    .antallNyeIdenter(tilleggstoenadRequest.getAntallFlytting())
                    .build());
            byggTilleggstoenadResponse("flytting", nyeTilleggFlytting, arenaRespons);
        }

        if (tilleggstoenadRequest.getAntallLaeremidler() != null && tilleggstoenadRequest.getAntallLaeremidler() > 0) {
            var nyeTilleggLaeremidler = tilleggstoenadConsumer.opprettLaeremidler(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                    .miljoe(tilleggstoenadRequest.getMiljoe())
                    .antallNyeIdenter(tilleggstoenadRequest.getAntallLaeremidler())
                    .build());
            byggTilleggstoenadResponse("læremidler", nyeTilleggLaeremidler, arenaRespons);
        }

        if (tilleggstoenadRequest.getAntallHjemreise() != null && tilleggstoenadRequest.getAntallHjemreise() > 0) {
            var nyeTilleggHjemreise = tilleggstoenadConsumer.opprettHjemreise(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                    .miljoe(tilleggstoenadRequest.getMiljoe())
                    .antallNyeIdenter(tilleggstoenadRequest.getAntallHjemreise())
                    .build());
            byggTilleggstoenadResponse("hjemreise", nyeTilleggHjemreise, arenaRespons);
        }

        if (tilleggstoenadRequest.getAntallReiseObligatoriskSamling() != null && tilleggstoenadRequest.getAntallReiseObligatoriskSamling() > 0) {
            var nyeTilleggReiseObligatoriskSamling = tilleggstoenadConsumer.opprettReiseObligatoriskSamling(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                    .miljoe(tilleggstoenadRequest.getMiljoe())
                    .antallNyeIdenter(tilleggstoenadRequest.getAntallReiseObligatoriskSamling())
                    .build());
            byggTilleggstoenadResponse("reise obligatorisk samling", nyeTilleggReiseObligatoriskSamling, arenaRespons);
        }

        if (tilleggstoenadRequest.getAntallTilsynBarn() != null && tilleggstoenadRequest.getAntallTilsynBarn() > 0) {
            var nyeTilleggTilsynBarn = tilleggstoenadConsumer.opprettTilsynBarn(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                    .miljoe(tilleggstoenadRequest.getMiljoe())
                    .antallNyeIdenter(tilleggstoenadRequest.getAntallTilsynBarn())
                    .build());
            byggTilleggstoenadResponse("tilsyn barn", nyeTilleggTilsynBarn, arenaRespons);
        }

        if (tilleggstoenadRequest.getAntallTilsynFamiliemedlemmer() != null && tilleggstoenadRequest.getAntallTilsynFamiliemedlemmer() > 0) {
            var nyeTilleggTilsynFamiliemedlemmer = tilleggstoenadConsumer.opprettTilsynFamiliemedlemmer(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tilleggstoenadRequest.getAvspillergruppeId())
                    .miljoe(tilleggstoenadRequest.getMiljoe())
                    .antallNyeIdenter(tilleggstoenadRequest.getAntallTilsynFamiliemedlemmer())
                    .build());
            byggTilleggstoenadResponse("tilsyn familiemedlemmer", nyeTilleggTilsynFamiliemedlemmer, arenaRespons);
        }

        return arenaRespons;
    }

    public List<NyttVedtakTillegg> opprettArenaTilleggstoenadArbeidssoekere(SyntetiserArenaTilleggstoenadArbeidssoekereRequest tilleggstoenadArbeidssoekereRequest) {
        List<NyttVedtakTillegg> arenaRespons = new ArrayList<>();

        if (tilleggstoenadArbeidssoekereRequest.getAntallTilsynBarnArbeidssoekere() != null && tilleggstoenadArbeidssoekereRequest.getAntallTilsynBarnArbeidssoekere() > 0) {
            var nyeTilleggTilsynBarnArbeidssoeker = tilleggstoenadConsumer.opprettTilsynBarnArbeidssoekere(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                    .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                    .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallTilsynBarnArbeidssoekere())
                    .build());
            byggTilleggstoenadResponse("tilsyn barn - arbeidssøkere", nyeTilleggTilsynBarnArbeidssoeker, arenaRespons);
        }

        if (tilleggstoenadArbeidssoekereRequest.getAntallTilsynFamiliemedlemmerArbeidssoekere() != null && tilleggstoenadArbeidssoekereRequest.getAntallTilsynFamiliemedlemmerArbeidssoekere() > 0) {
            var nyeTilleggTilsynFamiliemedlemmerArbeidssoeker = tilleggstoenadConsumer.opprettTilsynFamiliemedlemmerArbeidssoekere(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                    .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                    .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallTilsynFamiliemedlemmerArbeidssoekere())
                    .build());
            byggTilleggstoenadResponse("tilsyn familiemedlemmer - arbeidssøkere", nyeTilleggTilsynFamiliemedlemmerArbeidssoeker, arenaRespons);
        }

        if (tilleggstoenadArbeidssoekereRequest.getAntallBoutgifterArbeidssoekere() != null && tilleggstoenadArbeidssoekereRequest.getAntallBoutgifterArbeidssoekere() > 0) {
            var nyeTilleggBoutgifterArbeidssoeker = tilleggstoenadConsumer.opprettBoutgifterArbeidssoekere(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                    .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                    .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallBoutgifterArbeidssoekere())
                    .build());
            byggTilleggstoenadResponse("boutgifter - arbeidssøkere", nyeTilleggBoutgifterArbeidssoeker, arenaRespons);
        }

        if (tilleggstoenadArbeidssoekereRequest.getAntallDagligReiseArbeidssoekere() != null && tilleggstoenadArbeidssoekereRequest.getAntallDagligReiseArbeidssoekere() > 0) {
            var nyeTilleggDagligReiseArbeidssoeker = tilleggstoenadConsumer.opprettDagligReiseArbeidssoekere(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                    .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                    .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallDagligReiseArbeidssoekere())
                    .build());
            byggTilleggstoenadResponse("daglig reise - arbeidssøkere", nyeTilleggDagligReiseArbeidssoeker, arenaRespons);
        }

        if (tilleggstoenadArbeidssoekereRequest.getAntallFlyttingArbeidssoekere() != null && tilleggstoenadArbeidssoekereRequest.getAntallFlyttingArbeidssoekere() > 0) {
            var nyeTilleggFlyttingArbeidssoeker = tilleggstoenadConsumer.opprettFlyttingArbeidssoekere(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                    .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                    .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallFlyttingArbeidssoekere())
                    .build());
            byggTilleggstoenadResponse("flytting - arbeidssøkere", nyeTilleggFlyttingArbeidssoeker, arenaRespons);
        }

        if (tilleggstoenadArbeidssoekereRequest.getAntallLaeremidlerArbeidssoekere() != null && tilleggstoenadArbeidssoekereRequest.getAntallLaeremidlerArbeidssoekere() > 0) {
            var nyeTilleggLaeremidlerArbeidssoeker = tilleggstoenadConsumer.opprettLaeremidlerArbeidssoekere(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                    .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                    .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallLaeremidlerArbeidssoekere())
                    .build());
            byggTilleggstoenadResponse("læremidler - arbeidssøkere", nyeTilleggLaeremidlerArbeidssoeker, arenaRespons);
        }

        if (tilleggstoenadArbeidssoekereRequest.getAntallHjemreiseArbeidssoekere() != null && tilleggstoenadArbeidssoekereRequest.getAntallHjemreiseArbeidssoekere() > 0) {
            var nyeTilleggHjemreiseArbeidssoeker = tilleggstoenadConsumer.opprettHjemreiseArbeidssoekere(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                    .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                    .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallHjemreiseArbeidssoekere())
                    .build());
            byggTilleggstoenadResponse("hjemreise - arbeidssøkere", nyeTilleggHjemreiseArbeidssoeker, arenaRespons);
        }

        if (tilleggstoenadArbeidssoekereRequest.getAntallReiseObligatoriskSamlingArbeidssoekere() != null && tilleggstoenadArbeidssoekereRequest.getAntallReiseObligatoriskSamlingArbeidssoekere() > 0) {
            var nyeTilleggReiseObligatoriskSamlingArbeidssoeker = tilleggstoenadConsumer.opprettReiseObligatoriskSamlingArbeidssoekere(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                    .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                    .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallReiseObligatoriskSamlingArbeidssoekere())
                    .build());
            byggTilleggstoenadResponse("reise obligatorisk samling - arbeidssøkere", nyeTilleggReiseObligatoriskSamlingArbeidssoeker, arenaRespons);
        }

        if (tilleggstoenadArbeidssoekereRequest.getAntallReisestoenadArbeidssoekere() != null && tilleggstoenadArbeidssoekereRequest.getAntallReisestoenadArbeidssoekere() > 0) {
            var nyeTilleggReisestoenadArbeidssoeker = tilleggstoenadConsumer.opprettReisestoenadArbeidssoekere(SyntetiserArenaRequest.builder()
                    .avspillergruppeId(tilleggstoenadArbeidssoekereRequest.getAvspillergruppeId())
                    .miljoe(tilleggstoenadArbeidssoekereRequest.getMiljoe())
                    .antallNyeIdenter(tilleggstoenadArbeidssoekereRequest.getAntallReisestoenadArbeidssoekere())
                    .build());
            byggTilleggstoenadResponse("reisestønad - arbeidssøkere", nyeTilleggReisestoenadArbeidssoeker, arenaRespons);
        }

        return arenaRespons;
    }

    private void byggAapResponse(
            String type,
            List<NyttVedtakResponse> nyeVedtakAap,
            List<NyttVedtakAap> arenaRespons
    ) {
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

    private void byggTiltakResponse(
            String type,
            List<NyttVedtakResponse> nyeVedtakTiltak,
            List<NyttVedtakTiltak> arenaRespons
    ) {
        List<NyttVedtakTiltak> nyeRettigheter = new ArrayList<>();
        List<NyttVedtakFeil> feiledeRettigheter = new ArrayList<>();
        for (var vedtak : nyeVedtakTiltak) {
            nyeRettigheter.addAll(vedtak.getNyeRettigheterTiltak() != null ? vedtak.getNyeRettigheterTiltak() : new ArrayList<>());
            feiledeRettigheter.addAll(vedtak.getFeiledeRettigheter() != null ? vedtak.getFeiledeRettigheter() : new ArrayList<>());
        }
        log.info("{}: Opprettet {} tiltak. Antall feilede tiltak: {}",
                type,
                nyeRettigheter.size(),
                feiledeRettigheter.size());
        arenaRespons.addAll(nyeRettigheter);
    }

    private void byggTilleggstoenadResponse(
            String type,
            List<NyttVedtakResponse> nyeVedtakTilleggstoenad,
            List<NyttVedtakTillegg> arenaRespons
    ) {
        List<NyttVedtakTillegg> nyeRettigheter = new ArrayList<>();
        List<NyttVedtakFeil> feiledeRettigheter = new ArrayList<>();
        for (var vedtak : nyeVedtakTilleggstoenad) {
            nyeRettigheter.addAll(vedtak.getNyeRettigheterTillegg() != null ? vedtak.getNyeRettigheterTillegg() : new ArrayList<>());
            feiledeRettigheter.addAll(vedtak.getFeiledeRettigheter() != null ? vedtak.getFeiledeRettigheter() : new ArrayList<>());
        }
        log.info("{}: Opprettet {} tilleggstønader. Antall feilede tilleggstønader: {}",
                type,
                nyeRettigheter.size(),
                feiledeRettigheter.size());
        arenaRespons.addAll(nyeRettigheter);
    }
}
