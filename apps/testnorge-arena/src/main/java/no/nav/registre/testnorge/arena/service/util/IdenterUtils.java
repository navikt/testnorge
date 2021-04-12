package no.nav.registre.testnorge.arena.service.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.BrukereArenaForvalterConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.AktoerRegisteretConsumer;
import no.nav.registre.testnorge.arena.service.TpsForvalterService;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.response.Relasjon;
import no.nav.registre.testnorge.consumers.hodejegeren.response.RelasjonsResponse;
import no.nav.registre.testnorge.libs.core.util.IdentUtil;

import static no.nav.registre.testnorge.arena.consumer.rs.util.ConsumerUtils.EIER;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdenterUtils {

    private static final int PAGE_SIZE = 10;
    private static final String RELASJON_BARN = "BARN";

    private final HodejegerenConsumer hodejegerenConsumer;
    private final BrukereArenaForvalterConsumer brukereArenaForvalterConsumer;
    private final AktoerRegisteretConsumer aktoerRegisteretConsumer;
    private final TpsForvalterService tpsForvalterService;

    public List<String> getLevende(
            Long avspillergruppeId,
            String miljoe
    ) {
        var levendeIdenterAsSet = new HashSet<>(hodejegerenConsumer.getLevende(avspillergruppeId));
        return filtrerEksisterendeBrukereIArena(levendeIdenterAsSet, miljoe);
    }

    public List<String> getUtvalgteIdenter(
            Long avspillergruppeId,
            int antallNyeIdenter,
            String miljoe
    ) {
        var levendeIdenter = new HashSet<>(hodejegerenConsumer.getLevende(avspillergruppeId));
        return filtrerIdenterUtenAktoerId(levendeIdenter, miljoe, antallNyeIdenter, false);
    }

    public List<String> getUtvalgteIdenterIAldersgruppe(
            Long avspillergruppeId,
            int antallNyeIdenter,
            int minimumAlder,
            int maksimumAlder,
            String miljoe,
            boolean maaVaereBosatt
    ) {
        var levendeIdenterIAldersgruppe = new HashSet<>(hodejegerenConsumer.getLevende(avspillergruppeId, minimumAlder, maksimumAlder));
        return filtrerIdenterUtenAktoerId(levendeIdenterIAldersgruppe, miljoe, antallNyeIdenter, maaVaereBosatt);
    }

    public List<String> getUtvalgteIdenterIAldersgruppeMedBarnUnder18(
            Long avspillergruppeId,
            int antallNyeIdenter,
            int minimumAlder,
            int maksimumAlder,
            String miljoe,
            LocalDate tidligsteDato,
            boolean maaVaereBosatt
    ) {
        var levendeIdenterIAldersgruppe = new HashSet<>(hodejegerenConsumer.getLevende(avspillergruppeId, minimumAlder, maksimumAlder));
        return filtrerIdenterUtenAktoerIdOgBarnUnder18(levendeIdenterIAldersgruppe, miljoe, antallNyeIdenter, tidligsteDato, maaVaereBosatt);
    }

    public List<KontoinfoResponse> getIdenterMedKontoinformasjon(
            Long avspillergruppeId,
            String miljoe,
            int antallNyeIdenter
    ) {
        if (antallNyeIdenter == 0) {
            return new ArrayList<>();
        }
        var identerMedKontonummer = hodejegerenConsumer.getIdenterMedKontonummer(avspillergruppeId, miljoe, antallNyeIdenter, null, null);
        Collections.shuffle(identerMedKontonummer);
        return identerMedKontonummer;
    }

    private List<String> filtrerIdenterUtenAktoerId(
            Set<String> identer,
            String miljoe,
            int antallNyeIdenter,
            boolean maaVaereBosatt
    ) {
        var identerUtenArenabruker = filtrerEksisterendeBrukereIArena(identer, miljoe);
        var identerPartisjonert = partisjonerListe(identerUtenArenabruker, PAGE_SIZE);
        Map<String, String> identerMedAktoerId = new HashMap<>();
        for (var partisjon : identerPartisjonert) {
            var aktoerIdenter = aktoerRegisteretConsumer.hentAktoerIderTilIdenter(partisjon, miljoe);
            if (maaVaereBosatt) {
                aktoerIdenter = aktoerIdenter.entrySet().stream()
                        .filter(x -> tpsForvalterService.identHarPersonstatusBosatt(x.getKey(), miljoe))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            }
            identerMedAktoerId.putAll(aktoerIdenter);
            if (identerMedAktoerId.size() >= antallNyeIdenter) {
                break;
            }
        }
        return new ArrayList<>(identerMedAktoerId.keySet()).subList(0, antallNyeIdenter);
    }

    private List<String> filtrerIdenterUtenAktoerIdOgBarnUnder18(
            Set<String> identer,
            String miljoe,
            int antallNyeIdenter,
            LocalDate tidligsteDato,
            boolean maaVaereBosatt
    ) {
        var identerUtenArenabruker = filtrerEksisterendeBrukereIArena(identer, miljoe);

        var identerPartisjonert = partisjonerListe(identerUtenArenabruker, PAGE_SIZE);

        List<String> utvalgteIdenter = new ArrayList<>(antallNyeIdenter);

        for (var partisjon : identerPartisjonert) {
            Map<String, String> identerMedAktoerId = aktoerRegisteretConsumer.hentAktoerIderTilIdenter(partisjon, miljoe);

            for (var ident : identerMedAktoerId.keySet()) {
                var relasjonsResponse = getRelasjonerTilIdent(ident, miljoe);
                if (inneholderBarnUnder18VedTidspunkt(relasjonsResponse, tidligsteDato) && (!maaVaereBosatt || tpsForvalterService.identHarPersonstatusBosatt(ident, miljoe))) {
                    utvalgteIdenter.add(ident);
                    if (utvalgteIdenter.size() >= antallNyeIdenter) {
                        return utvalgteIdenter;
                    }
                }
            }
        }
        return utvalgteIdenter;
    }

    private boolean inneholderBarnUnder18VedTidspunkt(RelasjonsResponse relasjonsResponse, LocalDate tidligsteDato) {
        if (relasjonsResponse != null && relasjonsResponse.getRelasjoner() != null) {
            for (var relasjon : relasjonsResponse.getRelasjoner()) {
                if (erRelasjonEtBarnUnder18VedTidspunkt(relasjon, tidligsteDato)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean erRelasjonEtBarnUnder18VedTidspunkt(
            Relasjon relasjon,
            LocalDate tidspunkt
    ) {
        if (RELASJON_BARN.equals(relasjon.getTypeRelasjon())) {
            var doedsdato = relasjon.getDatoDo();
            if (doedsdato != null && !doedsdato.equals("")) {
                return false;
            }

            var barnFnr = relasjon.getFnrRelasjon();

            int alder = Math.toIntExact(ChronoUnit.YEARS.between(IdentUtil.getFoedselsdatoFraIdent(barnFnr), tidspunkt));

            return alder > -1 && alder < 18;
        }
        return false;
    }

    private List<String> filtrerEksisterendeBrukereIArena(
            Set<String> identerAsSet,
            String miljoe
    ) {
        var eksisterendeBrukere = new HashSet<>(hentEksisterendeArbeidsoekerIdenter(EIER, miljoe, true));
        identerAsSet.removeAll(eksisterendeBrukere);
        var identer = new ArrayList<>(identerAsSet);
        Collections.shuffle(identer);
        return identer;
    }

    public List<String> getIdenterMedFoedselsmelding(
            Long avspillergruppeId,
            int maxAlder
    ) {
        return hodejegerenConsumer.getFoedte(avspillergruppeId, null, maxAlder);
    }

    public RelasjonsResponse getRelasjonerTilIdent(
            String ident,
            String miljoe
    ) {
        return hodejegerenConsumer.getRelasjoner(ident, miljoe);
    }

    private <T> Collection<List<T>> partisjonerListe(
            List<T> list,
            long partitionSize
    ) {
        AtomicInteger counter = new AtomicInteger(0);
        return list
                .stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / partitionSize))
                .values();
    }

    public List<String> hentEksisterendeArbeidsoekerIdenter(boolean useCache) {
        var arbeidsoekere = brukereArenaForvalterConsumer.hentArbeidsoekere(null, null, null, useCache);
        return hentIdentListe(arbeidsoekere);
    }

    public List<String> hentEksisterendeArbeidsoekerIdent(String personident, boolean useCache) {
        var arbeidsoekere = brukereArenaForvalterConsumer.hentArbeidsoekere(personident, null, null, useCache);
        return hentIdentListe(arbeidsoekere);
    }

    public List<String> hentEksisterendeArbeidsoekerIdenter(String eier, String miljoe, boolean useCache) {
        var arbeidsoekere = brukereArenaForvalterConsumer.hentArbeidsoekere(null, eier, miljoe, useCache);
        return hentIdentListe(arbeidsoekere);
    }

    private List<String> hentIdentListe(
            List<Arbeidsoeker> arbeidsoekere
    ) {
        if (arbeidsoekere.isEmpty()) {
            return new ArrayList<>();
        }

        return arbeidsoekere.stream().map(Arbeidsoeker::getPersonident).collect(Collectors.toList());
    }

}
