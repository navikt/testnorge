package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;
import static no.nav.dolly.domain.resultset.SystemTyper.SKATTEKORT;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingSkattekortStatusMapper {

    public static List<RsStatusRapport> buildSkattekortStatusMap(List<BestillingProgress> progressList) {

        log.info("[SKATTEKORT_MAPPER] buildSkattekortStatusMap called with {} progress records", progressList.size());

        boolean hasSkattekortStatus = progressList.stream()
                .anyMatch(p -> isNotBlank(p.getSkattekortStatus()));

        if (!hasSkattekortStatus) {
            log.info("[SKATTEKORT_MAPPER] No skattekort statuses found in any progress record, returning empty list");
            return emptyList();
        }

        Map<String, Set<String>> statusIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getSkattekortStatus()) && isNotBlank(progress.getIdent())) {
                log.info("[SKATTEKORT_MAPPER] Processing progress for ident: {}, skattekortStatus: '{}'", 
                    progress.getIdent(), progress.getSkattekortStatus());
                var entries = progress.getSkattekortStatus().split(",");
                for (var entry : entries) {
                    if (isNotBlank(entry)) {
                        if (statusIdents.containsKey(entry)) {
                            statusIdents.get(entry).add(progress.getIdent());
                        } else {
                            statusIdents.put(entry, new HashSet<>(Set.of(progress.getIdent())));
                        }
                    }
                }
            }
        });

        log.info("[SKATTEKORT_MAPPER] Building skattekort status map for {} status entries", statusIdents.size());

        var statusMap = new HashMap<String, Set<String>>();
        statusIdents.forEach((key, identsSet) -> {
            String melding = key;

            if (melding.contains("|")) {
                var parts = melding.split("\\|", 2);
                var orgYear = parts[0];
                var status = parts[1];

                if (status.equals("Skattekort lagret")) {
                    melding = "OK";
                    log.info("[SKATTEKORT_MAPPER] Converted status 'Skattekort lagret' to 'OK'");
                } else {
                    var orgYearParts = orgYear.split("\\+");
                    if (orgYearParts.length >= 2) {
                        melding = "FEIL: organisasjon:" + orgYearParts[0] + ", inntektsår:" + orgYearParts[1] + ", melding:" + decodeMsg(status);
                    } else {
                        melding = "FEIL: " + decodeMsg(status);
                    }
                    log.info("[SKATTEKORT_MAPPER] Converted error status: {}", melding);
                }
            } else {
                melding = decodeMsg(melding);
                log.info("[SKATTEKORT_MAPPER] Decoded non-pipe status: {}", melding);
            }

            if (statusMap.containsKey(melding)) {
                statusMap.get(melding).addAll(identsSet);
            } else {
                statusMap.put(melding, new HashSet<>(identsSet));
            }
        });

        log.info("[SKATTEKORT_MAPPER] Aggregated to {} unique status messages", statusMap.size());

        var statuser = statusMap.entrySet().stream()
                .map(entry -> {
                    log.info("[SKATTEKORT_MAPPER] Creating status report with melding: '{}', identer count: {}", entry.getKey(), entry.getValue().size());
                    return RsStatusRapport.Status.builder()
                            .melding(entry.getKey())
                            .identer(entry.getValue().stream().toList())
                            .build();
                })
                .toList();

        log.info("[SKATTEKORT_MAPPER] Returning status rapport with {} status messages", statuser.size());

        return List.of(RsStatusRapport.builder()
                .id(SKATTEKORT)
                .navn(SKATTEKORT.getBeskrivelse())
                .statuser(statuser)
                .build());
    }
}
