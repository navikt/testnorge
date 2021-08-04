package no.nav.registre.testnorge.arena.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.response.Relasjon;
import no.nav.registre.testnorge.consumers.hodejegeren.response.RelasjonsResponse;
import no.nav.testnav.libs.servletcore.util.IdentUtil;

import static no.nav.registre.testnorge.arena.service.util.ServiceUtils.EIER;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    private static final String RELASJON_BARN = "BARN";

    private final HodejegerenConsumer hodejegerenConsumer;
    private final PdlPersonService pdlPersonService;
    private final TpsForvalterService tpsForvalterService;
    private final ArenaBrukerService arenaBrukerService;

    public List<String> getUtvalgteIdenterIAldersgruppe(
            Long avspillergruppeId,
            int antallNyeIdenter,
            int minimumAlder,
            int maksimumAlder,
            String miljoe,
            LocalDate tidligsteDatoBosatt
    ) {
        var levendeIdenterIAldersgruppe = new HashSet<>(hodejegerenConsumer.getLevende(avspillergruppeId, minimumAlder, maksimumAlder));
        return filtrerIdenterUtenAktoerId(levendeIdenterIAldersgruppe, miljoe, antallNyeIdenter, tidligsteDatoBosatt);
    }

    public List<String> getUtvalgteIdenterIAldersgruppeMedBarnUnder18(
            Long avspillergruppeId,
            int antallNyeIdenter,
            int minimumAlder,
            int maksimumAlder,
            String miljoe,
            LocalDate tidligsteDatoBosatt,
            LocalDate tidligsteDatoBarn
    ) {
        var levendeIdenterIAldersgruppe = new HashSet<>(hodejegerenConsumer.getLevende(avspillergruppeId, minimumAlder, maksimumAlder));
        return filtrerIdenterUtenAktoerIdOgBarnUnder18(levendeIdenterIAldersgruppe, miljoe, antallNyeIdenter, tidligsteDatoBosatt, tidligsteDatoBarn);
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
            LocalDate tidligsteDatoBosatt
    ) {
        var identerUtenArenabruker = filtrerEksisterendeBrukereIArena(identer, miljoe);
        List<String> utvalgteIdenter = new ArrayList<>(antallNyeIdenter);

        for (var ident : identerUtenArenabruker) {
            var aktoerId = pdlPersonService.getAktoerIdTilIdent(ident);
            if (aktoerId != null && (tidligsteDatoBosatt == null || tpsForvalterService.identHarPersonstatusBosatt(ident, miljoe, tidligsteDatoBosatt))) {
                utvalgteIdenter.add(ident);
                if (utvalgteIdenter.size() >= antallNyeIdenter) {
                    return utvalgteIdenter;
                }
            }
        }

        return utvalgteIdenter;
    }

    private List<String> filtrerIdenterUtenAktoerIdOgBarnUnder18(
            Set<String> identer,
            String miljoe,
            int antallNyeIdenter,
            LocalDate tidligsteDatoBosatt,
            LocalDate tidligsteDatoBarn
    ) {
        var identerUtenArenabruker = filtrerEksisterendeBrukereIArena(identer, miljoe);

        List<String> utvalgteIdenter = new ArrayList<>(antallNyeIdenter);
        for (var ident : identerUtenArenabruker) {
            var aktoerId = pdlPersonService.getAktoerIdTilIdent(ident);
            if (aktoerId != null && (tidligsteDatoBosatt == null || tpsForvalterService.identHarPersonstatusBosatt(ident, miljoe, tidligsteDatoBosatt))) {
                var relasjonsResponse = getRelasjonerTilIdent(ident, miljoe);
                if (inneholderBarnUnder18VedTidspunkt(relasjonsResponse, tidligsteDatoBarn)) {
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

            var alder = Math.toIntExact(ChronoUnit.YEARS.between(IdentUtil.getFoedselsdatoFraIdent(barnFnr), tidspunkt));

            return alder > -1 && alder < 18;
        }
        return false;
    }

    private List<String> filtrerEksisterendeBrukereIArena(
            Set<String> identerAsSet,
            String miljoe
    ) {
        var eksisterendeBrukere = new HashSet<>(arenaBrukerService.hentEksisterendeArbeidsoekerIdenter(EIER, miljoe, true));
        identerAsSet.removeAll(eksisterendeBrukere);
        var identer = new ArrayList<>(identerAsSet);
        Collections.shuffle(identer);
        return identer;
    }

    public RelasjonsResponse getRelasjonerTilIdent(
            String ident,
            String miljoe
    ) {
        return hodejegerenConsumer.getRelasjoner(ident, miljoe);
    }

}
