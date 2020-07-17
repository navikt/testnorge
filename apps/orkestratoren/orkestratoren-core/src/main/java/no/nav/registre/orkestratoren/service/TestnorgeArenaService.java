package no.nav.registre.orkestratoren.service;

import static java.util.Objects.isNull;

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

            var nyeTilleggBoutgifter = tilleggstoenadConsumer.opprettBoutgifter(buildSyntArenaRequest(tilleggstoenadRequest, tilleggstoenadRequest.getAntallBoutgifter()));
            byggTilleggstoenadResponse("boutgifter", nyeTilleggBoutgifter, arenaRespons);

            var nyeTilleggDagligReise = tilleggstoenadConsumer.opprettDagligReise(buildSyntArenaRequest(tilleggstoenadRequest, tilleggstoenadRequest.getAntallDagligReise()));
            byggTilleggstoenadResponse("daglig reise", nyeTilleggDagligReise, arenaRespons);

            var nyeTilleggFlytting = tilleggstoenadConsumer.opprettFlytting(buildSyntArenaRequest(tilleggstoenadRequest, tilleggstoenadRequest.getAntallFlytting()));
            byggTilleggstoenadResponse("flytting", nyeTilleggFlytting, arenaRespons);

            var nyeTilleggLaeremidler = tilleggstoenadConsumer.opprettLaeremidler(buildSyntArenaRequest(tilleggstoenadRequest, tilleggstoenadRequest.getAntallLaeremidler()));
            byggTilleggstoenadResponse("læremidler", nyeTilleggLaeremidler, arenaRespons);

            var nyeTilleggHjemreise = tilleggstoenadConsumer.opprettHjemreise(buildSyntArenaRequest(tilleggstoenadRequest, tilleggstoenadRequest.getAntallHjemreise()));
            byggTilleggstoenadResponse("hjemreise", nyeTilleggHjemreise, arenaRespons);

            var nyeTilleggReiseObligatoriskSamling = tilleggstoenadConsumer.opprettReiseObligatoriskSamling(buildSyntArenaRequest(tilleggstoenadRequest, tilleggstoenadRequest.getAntallReiseObligatoriskSamling()));
            byggTilleggstoenadResponse("reise obligatorisk samling", nyeTilleggReiseObligatoriskSamling, arenaRespons);

            var nyeTilleggTilsynBarn = tilleggstoenadConsumer.opprettTilsynBarn(buildSyntArenaRequest(tilleggstoenadRequest, tilleggstoenadRequest.getAntallTilsynBarn()));
            byggTilleggstoenadResponse("tilsyn barn", nyeTilleggTilsynBarn, arenaRespons);

            var nyeTilleggTilsynFamiliemedlemmer = tilleggstoenadConsumer.opprettTilsynFamiliemedlemmer(buildSyntArenaRequest(tilleggstoenadRequest, tilleggstoenadRequest.getAntallTilsynFamiliemedlemmer()));
            byggTilleggstoenadResponse("tilsyn familiemedlemmer", nyeTilleggTilsynFamiliemedlemmer, arenaRespons);

        return arenaRespons;
    }

    private SyntetiserArenaRequest buildSyntArenaRequest(SyntetiserArenaTilleggstoenadRequest request, Integer antallNyeIdenter) {
        if (antallNyeIdenter == null || antallNyeIdenter == 0) {
            return null;
        }
        return SyntetiserArenaRequest.builder()
                .avspillergruppeId(request.getAvspillergruppeId())
                .miljoe(request.getMiljoe())
                .antallNyeIdenter(antallNyeIdenter)
                .build();
    }

    private SyntetiserArenaRequest buildSyntArenaArbeidssoekerRequest(SyntetiserArenaTilleggstoenadArbeidssoekereRequest request, Integer antallNyeIdenter) {
        if (antallNyeIdenter == null || antallNyeIdenter == 0) {
            return null;
        }
        return SyntetiserArenaRequest.builder()
                .avspillergruppeId(request.getAvspillergruppeId())
                .miljoe(request.getMiljoe())
                .antallNyeIdenter(antallNyeIdenter)
                .build();
    }

    public List<NyttVedtakTillegg> opprettArenaTilleggstoenadArbeidssoekere(SyntetiserArenaTilleggstoenadArbeidssoekereRequest tilleggstoenadArbeidssoekereRequest) {
        List<NyttVedtakTillegg> arenaRespons = new ArrayList<>();

            var nyeTilleggTilsynBarnArbeidssoeker = tilleggstoenadConsumer.opprettTilsynBarnArbeidssoekere(buildSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallTilsynBarnArbeidssoekere()));
            byggTilleggstoenadResponse("tilsyn barn - arbeidssøkere", nyeTilleggTilsynBarnArbeidssoeker, arenaRespons);

            var nyeTilleggTilsynFamiliemedlemmerArbeidssoeker = tilleggstoenadConsumer.opprettTilsynFamiliemedlemmerArbeidssoekere(buildSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallTilsynFamiliemedlemmerArbeidssoekere()));
            byggTilleggstoenadResponse("tilsyn familiemedlemmer - arbeidssøkere", nyeTilleggTilsynFamiliemedlemmerArbeidssoeker, arenaRespons);

            var nyeTilleggBoutgifterArbeidssoeker = tilleggstoenadConsumer.opprettBoutgifterArbeidssoekere(buildSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallBoutgifterArbeidssoekere()));
            byggTilleggstoenadResponse("boutgifter - arbeidssøkere", nyeTilleggBoutgifterArbeidssoeker, arenaRespons);

            var nyeTilleggDagligReiseArbeidssoeker = tilleggstoenadConsumer.opprettDagligReiseArbeidssoekere(buildSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallDagligReiseArbeidssoekere()));
            byggTilleggstoenadResponse("daglig reise - arbeidssøkere", nyeTilleggDagligReiseArbeidssoeker, arenaRespons);

            var nyeTilleggFlyttingArbeidssoeker = tilleggstoenadConsumer.opprettFlyttingArbeidssoekere(buildSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallFlyttingArbeidssoekere()));
            byggTilleggstoenadResponse("flytting - arbeidssøkere", nyeTilleggFlyttingArbeidssoeker, arenaRespons);

            var nyeTilleggLaeremidlerArbeidssoeker = tilleggstoenadConsumer.opprettLaeremidlerArbeidssoekere(buildSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallLaeremidlerArbeidssoekere()));
            byggTilleggstoenadResponse("læremidler - arbeidssøkere", nyeTilleggLaeremidlerArbeidssoeker, arenaRespons);

            var nyeTilleggHjemreiseArbeidssoeker = tilleggstoenadConsumer.opprettHjemreiseArbeidssoekere(buildSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallHjemreiseArbeidssoekere()));
            byggTilleggstoenadResponse("hjemreise - arbeidssøkere", nyeTilleggHjemreiseArbeidssoeker, arenaRespons);

            var nyeTilleggReiseObligatoriskSamlingArbeidssoeker = tilleggstoenadConsumer.opprettReiseObligatoriskSamlingArbeidssoekere(buildSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallReiseObligatoriskSamlingArbeidssoekere()));
            byggTilleggstoenadResponse("reise obligatorisk samling - arbeidssøkere", nyeTilleggReiseObligatoriskSamlingArbeidssoeker, arenaRespons);

            var nyeTilleggReisestoenadArbeidssoeker = tilleggstoenadConsumer.opprettReisestoenadArbeidssoekere(buildSyntArenaArbeidssoekerRequest(tilleggstoenadArbeidssoekereRequest, tilleggstoenadArbeidssoekereRequest.getAntallReisestoenadArbeidssoekere()));
            byggTilleggstoenadResponse("reisestønad - arbeidssøkere", nyeTilleggReisestoenadArbeidssoeker, arenaRespons);

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
        if(isNull(nyeVedtakTilleggstoenad)) { return; }
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
