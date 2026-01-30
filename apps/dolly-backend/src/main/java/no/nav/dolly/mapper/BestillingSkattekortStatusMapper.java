package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;
import static no.nav.dolly.domain.resultset.SystemTyper.SKATTEKORT;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class BestillingSkattekortStatusMapper {

    public static List<RsStatusRapport> buildSkattekortStatusMap(List<BestillingProgress> progressList) {

        log.info("[SKATTEKORT_MAPPER] Building skattekort status map for {} progress records", progressList.size());

        //  status     org+year       ident
        Map<String, Map<String, Set<String>>> errorEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            log.info("[SKATTEKORT_MAPPER] Processing progress for ident: {}, skattekortStatus: '{}'",
                    progress.getIdent(), progress.getSkattekortStatus());
            if (isNotBlank(progress.getSkattekortStatus()) && isNotBlank(progress.getIdent())) {
                var entries = progress.getSkattekortStatus().split(",");
                for (var entry : entries) {
                    var parts = entry.split("\\|");
                    if (parts.length < 2) {
                        log.warn("[SKATTEKORT_MAPPER] Skipping entry with invalid format: '{}'. Expected format: 'org+year|status'", entry);
                        continue;
                    }
                    var orgYear = parts[0];
                    var status = parts[1];
                    log.info("[SKATTEKORT_MAPPER] Parsed entry - orgYear: '{}', status: '{}'", orgYear, status);
                    if (errorEnvIdents.containsKey(status)) {
                        if (errorEnvIdents.get(status).containsKey(orgYear)) {
                            errorEnvIdents.get(status).get(orgYear).add(progress.getIdent());
                        } else {
                            errorEnvIdents.get(status).put(orgYear, new HashSet<>(Set.of(progress.getIdent())));
                        }
                    } else {
                        errorEnvIdents.put(status, new HashMap<>(Map.of(orgYear, new HashSet<>(Set.of(progress.getIdent())))));
                    }
                }
            }
        });

        if (errorEnvIdents.isEmpty()) {
            log.info("[SKATTEKORT_MAPPER] No skattekort statuses found, returning empty list");
            return emptyList();

        } else {
            if (errorEnvIdents.entrySet().stream()
                    .allMatch(entry -> entry.getKey().equals("Skattekort lagret"))) {

                log.info("[SKATTEKORT_MAPPER] All skattekort statuses are OK, returning success status");
                return errorEnvIdents.values().stream()
                        .map(entry -> RsStatusRapport.builder()
                                .id(SKATTEKORT)
                                .navn(SKATTEKORT.getBeskrivelse())
                                .statuser(List.of(RsStatusRapport.Status.builder()
                                        .melding("OK")
                                        .identer(entry.values().stream()
                                                .flatMap(Collection::stream)
                                                .distinct()
                                                .toList())
                                        .build()))
                                .build())
                        .toList();

            } else {

                return errorEnvIdents.entrySet().stream()
                        .filter(entry -> !entry.getKey().equals("Skattekort lagret"))
                        .map(entry -> RsStatusRapport.builder()
                                .id(SKATTEKORT)
                                .navn(SKATTEKORT.getBeskrivelse())
                                .statuser(entry.getValue().entrySet().stream()
                                        .map(orgYear -> RsStatusRapport.Status.builder()
                                                .melding(formatMelding(orgYear.getKey(), entry.getKey()))
                                                .identer(orgYear.getValue().stream().toList())
                                                .build())
                                        .toList())
                                .build())
                        .toList();
            }
        }
    }


    private static String formatMelding(String orgYear, String melding) {

        String[] parts = orgYear.split("\\+");
        if (parts.length < 2) {
            return "FEIL: " + decodeMsg(melding);
        }

        return "FEIL: organisasjon:%s, inntektsår:%s, melding:%s".formatted(
                parts[0],
                parts[1],
                decodeMsg(melding));
    }
}
