package no.nav.registre.orkestratoren.service;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        if (erPositivtTall(tilleggstoenadRequest.getAntallBoutgifter())) {
            var nyeTilleggBoutgifter = tilleggstoenadConsumer.opprettBoutgifter(byggSyntArenaRequest(tilleggstoenadRequest, tilleggstoenadRequest.getAntallBoutgifter()));
            byggTilleggstoenadResponse("boutgifter", nyeTilleggBoutgifter, arenaRespons);
        }
        if (erPositivtTall(tilleggstoenadRequest.getAntallDagligReise())) {
            var nyeTilleggDagligReise = tilleggstoenadConsumer.opprettDagligReise(byggSyntArenaRequest(tilleggstoenadRequest, tilleggstoenadRequest.getAntallDagligReise()));
            byggTilleggstoenadResponse("daglig reise", nyeTilleggDagligReise, arenaRespons);
        }
        if (erPositivtTall(tilleggstoenadRequest.getAntallFlytting())) {
            var nyeTilleggFlytting = tilleggstoenadConsumer.opprettFlytting(byggSyntArenaRequest(tilleggstoenadRequest, tilleggstoenadRequest.getAntallFlytting()));
            byggTilleggstoenadResponse("flytting", nyeTilleggFlytting, arenaRespons);
        }
        if (erPositivtTall(tilleggstoenadRequest.getAntallLaeremidler())) {
            var nyeTilleggLaeremidler = tilleggstoenadConsumer.opprettLaeremidler(byggSyntArenaRequest(tilleggstoenadRequest, tilleggstoenadRequest.getAntallLaeremidler()));
            byggTilleggstoenadResponse("læremidler", nyeTilleggLaeremidler, arenaRespons);
        }
        if (erPositivtTall(tilleggstoenadRequest.getAntallHjemreise())) {
            var nyeTilleggHjemreise = tilleggstoenadConsumer.opprettHjemreise(byggSyntArenaRequest(tilleggstoenadRequest, tilleggstoenadRequest.getAntallHjemreise()));
            byggTilleggstoenadResponse("hjemreise", nyeTilleggHjemreise, arenaRespons);
        }
        if (erPositivtTall(tilleggstoenadRequest.getAntallReiseObligatoriskSamling())) {
            var nyeTilleggReiseObligatoriskSamling = tilleggstoenadConsumer.opprettReiseObligatoriskSamling(byggSyntArenaRequest(tilleggstoenadRequest, tilleggstoenadRequest.getAntallReiseObligatoriskSamling()));
            byggTilleggstoenadResponse("reise obligatorisk samling", nyeTilleggReiseObligatoriskSamling, arenaRespons);
        }
        if (erPositivtTall(tilleggstoenadRequest.getAntallTilsynBarn())) {
            var nyeTilleggTilsynBarn = tilleggstoenadConsumer.opprettTilsynBarn(byggSyntArenaRequest(tilleggstoenadRequest, tilleggstoenadRequest.getAntallTilsynBarn()));
            byggTilleggstoenadResponse("tilsyn barn", nyeTilleggTilsynBarn, arenaRespons);
        }
        if (erPositivtTall(tilleggstoenadRequest.getAntallTilsynFamiliemedlemmer())) {
            var nyeTilleggTilsynFamiliemedlemmer = tilleggstoenadConsumer.opprettTilsynFamiliemedlemmer(byggSyntArenaRequest(tilleggstoenadRequest, tilleggstoenadRequest.getAntallTilsynFamiliemedlemmer()));
            byggTilleggstoenadResponse("tilsyn familiemedlemmer", nyeTilleggTilsynFamiliemedlemmer, arenaRespons);
        }
        return arenaRespons;
    }

    private SyntetiserArenaRequest byggSyntArenaRequest(SyntetiserArenaTilleggstoenadRequest request, Integer antallNyeIdenter) {
        return SyntetiserArenaRequest.builder()
                .avspillergruppeId(request.getAvspillergruppeId())
                .miljoe(request.getMiljoe())
                .antallNyeIdenter(antallNyeIdenter)
                .build();
    }

    private SyntetiserArenaRequest byggSyntArenaArbeidssoekerRequest(SyntetiserArenaTilleggstoenadArbeidssoekereRequest request, Integer antallNyeIdenter) {
        return SyntetiserArenaRequest.builder()
                .avspillergruppeId(request.getAvspillergruppeId())
                .miljoe(request.getMiljoe())
                .antallNyeIdenter(antallNyeIdenter)
                .build();
    }

    public List<NyttVedtakTillegg> opprettArenaTilleggstoenadArbeidssoekere(SyntetiserArenaTilleggstoenadArbeidssoekereRequest tilleggstoenadArbeidssoekereRequest) {
        List<NyttVedtakTillegg> arenaRespons = new ArrayList<>();

        if (erPositivtTall(tilleggstoenadArbeidssoekereRequest.getAntallTilsynBarnArbeidssoekere())) {
            var nyeTilleggTilsynBarnArbeidssoeker = tilleggstoenadConsumer
                    .opprettTilsynBarnArbeidssoekere(byggSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallTilsynBarnArbeidssoekere()));
            byggTilleggstoenadResponse("tilsyn barn - arbeidssøkere", nyeTilleggTilsynBarnArbeidssoeker, arenaRespons);
        }
        if (erPositivtTall(tilleggstoenadArbeidssoekereRequest.getAntallTilsynFamiliemedlemmerArbeidssoekere())) {
            var nyeTilleggTilsynFamiliemedlemmerArbeidssoeker = tilleggstoenadConsumer.opprettTilsynFamiliemedlemmerArbeidssoekere(
                    byggSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallTilsynFamiliemedlemmerArbeidssoekere()));
            byggTilleggstoenadResponse("tilsyn familiemedlemmer - arbeidssøkere", nyeTilleggTilsynFamiliemedlemmerArbeidssoeker, arenaRespons);
        }
        if (erPositivtTall(tilleggstoenadArbeidssoekereRequest.getAntallBoutgifterArbeidssoekere())) {
            var nyeTilleggBoutgifterArbeidssoeker = tilleggstoenadConsumer
                    .opprettBoutgifterArbeidssoekere(byggSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallBoutgifterArbeidssoekere()));
            byggTilleggstoenadResponse("boutgifter - arbeidssøkere", nyeTilleggBoutgifterArbeidssoeker, arenaRespons);
        }
        if (erPositivtTall(tilleggstoenadArbeidssoekereRequest.getAntallDagligReiseArbeidssoekere())) {
            var nyeTilleggDagligReiseArbeidssoeker = tilleggstoenadConsumer
                    .opprettDagligReiseArbeidssoekere(byggSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallDagligReiseArbeidssoekere()));
            byggTilleggstoenadResponse("daglig reise - arbeidssøkere", nyeTilleggDagligReiseArbeidssoeker, arenaRespons);
        }
        if (erPositivtTall(tilleggstoenadArbeidssoekereRequest.getAntallFlyttingArbeidssoekere())) {
            var nyeTilleggFlyttingArbeidssoeker = tilleggstoenadConsumer
                    .opprettFlyttingArbeidssoekere(byggSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallFlyttingArbeidssoekere()));
            byggTilleggstoenadResponse("flytting - arbeidssøkere", nyeTilleggFlyttingArbeidssoeker, arenaRespons);
        }
        if (erPositivtTall(tilleggstoenadArbeidssoekereRequest.getAntallLaeremidlerArbeidssoekere())) {
            var nyeTilleggLaeremidlerArbeidssoeker = tilleggstoenadConsumer
                    .opprettLaeremidlerArbeidssoekere(byggSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallLaeremidlerArbeidssoekere()));
            byggTilleggstoenadResponse("læremidler - arbeidssøkere", nyeTilleggLaeremidlerArbeidssoeker, arenaRespons);
        }
        if (erPositivtTall(tilleggstoenadArbeidssoekereRequest.getAntallHjemreiseArbeidssoekere())) {
            var nyeTilleggHjemreiseArbeidssoeker = tilleggstoenadConsumer
                    .opprettHjemreiseArbeidssoekere(byggSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallHjemreiseArbeidssoekere()));
            byggTilleggstoenadResponse("hjemreise - arbeidssøkere", nyeTilleggHjemreiseArbeidssoeker, arenaRespons);
        }
        if (erPositivtTall(tilleggstoenadArbeidssoekereRequest.getAntallReiseObligatoriskSamlingArbeidssoekere())) {
            var nyeTilleggReiseObligatoriskSamlingArbeidssoeker = tilleggstoenadConsumer.opprettReiseObligatoriskSamlingArbeidssoekere(
                    byggSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallReiseObligatoriskSamlingArbeidssoekere()));
            byggTilleggstoenadResponse("reise obligatorisk samling - arbeidssøkere", nyeTilleggReiseObligatoriskSamlingArbeidssoeker, arenaRespons);
        }
        if (erPositivtTall(tilleggstoenadArbeidssoekereRequest.getAntallReisestoenadArbeidssoekere())) {
            var nyeTilleggReisestoenadArbeidssoeker = tilleggstoenadConsumer
                    .opprettReisestoenadArbeidssoekere(byggSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallReisestoenadArbeidssoekere()));
            byggTilleggstoenadResponse("reisestønad - arbeidssøkere", nyeTilleggReisestoenadArbeidssoeker, arenaRespons);
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

    private void byggTiltakResponse(
            String type,
            List<NyttVedtakResponse> nyeVedtakTiltak,
            List<NyttVedtakTiltak> arenaRespons) {
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
            List<NyttVedtakTillegg> arenaRespons) {
        if (isNull(nyeVedtakTilleggstoenad)) {
            return;
        }
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

    private boolean erPositivtTall(Integer integer) {
        return integer != null && integer > 0;
    }
}
