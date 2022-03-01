package no.nav.registre.bisys.service;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.bisys.service.utils.Barn;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.responses.Relasjon;
import no.nav.registre.testnorge.consumers.hodejegeren.responses.RelasjonsResponse;
import org.joda.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static no.nav.registre.bisys.service.SyntetiseringService.MIN_MOTTATT_DATO;
import static no.nav.registre.bisys.service.utils.DateUtils.getBirthdate;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    public static final String RELASJON_MOR = "MORA";
    public static final String RELASJON_FAR = "FARA";
    public static final int MAX_AGE_BARN_AT_MOTTATTDATO = 20;

    private final HodejegerenConsumer hodejegerenConsumer;

    @Value("${USE_HISTORICAL_MOTTATTDATO}")
    private boolean useHistoricalMottattdato;

    public List<Barn> selectValidUids(
            int antallIdenter, Long avspillergruppeId, String miljoe) {

        List<String> identerMedFoedselsmelding = finnFoedteIdenter(avspillergruppeId);
        List<Barn> utvalgteIdenter = new ArrayList<>();

        for (String ident : identerMedFoedselsmelding) {

            if (isBarnTooOld(ident)) {
                continue;
            }

            RelasjonsResponse relasjonsResponse = finnRelasjonerTilIdent(ident, miljoe);
            List<Relasjon> relasjoner = relasjonsResponse.getRelasjoner();

            String morFnr = "";
            String farFnr = "";

            for (Relasjon relasjon : relasjoner) {
                if (RELASJON_MOR.equals(relasjon.getTypeRelasjon())) {
                    morFnr = relasjon.getFnrRelasjon();
                } else if (RELASJON_FAR.equals(relasjon.getTypeRelasjon())) {
                    farFnr = relasjon.getFnrRelasjon();
                }
            }

            if (!morFnr.isEmpty() && !farFnr.isEmpty()) {
                utvalgteIdenter.add(Barn.builder().fnr(ident).morFnr(morFnr).farFnr(farFnr).build());
            }

            if (utvalgteIdenter.size() >= antallIdenter) {
                break;
            }
        }

        return utvalgteIdenter;
    }

    private boolean isBarnTooOld(String baFnr) {
        LocalDate birthdate = getBirthdate(baFnr);
        if (useHistoricalMottattdato) {
            return birthdate.isBefore(MIN_MOTTATT_DATO.minusYears(MAX_AGE_BARN_AT_MOTTATTDATO));
        } else {
            return birthdate.isBefore(LocalDate.now().minusYears(MAX_AGE_BARN_AT_MOTTATTDATO));
        }
    }

    @Timed(value = "bisys.resource.latency", extraTags = {"operation", "hodejegeren"})
    public RelasjonsResponse finnRelasjonerTilIdent(String ident, String miljoe) {
        return hodejegerenConsumer.getRelasjoner(ident, miljoe);
    }

    @Timed(value = "bisys.resource.latency", extraTags = {"operation", "hodejegeren"})
    private List<String> finnFoedteIdenter(Long avspillergruppeId) {
        return hodejegerenConsumer.getFoedte(avspillergruppeId);
    }

}
