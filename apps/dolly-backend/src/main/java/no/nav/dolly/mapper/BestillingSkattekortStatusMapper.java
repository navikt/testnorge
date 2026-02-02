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

        boolean hasSkattekortStatus = progressList.stream()
                .anyMatch(p -> isNotBlank(p.getSkattekortStatus()));

        if (!hasSkattekortStatus) {
            log.debug("[SKATTEKORT] No skattekort status found in progress list, returning empty");
            return emptyList();
        }

        log.debug("[SKATTEKORT] Building status map from {} progress records", progressList.size());

        Map<String, Set<String>> statusIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getSkattekortStatus()) && isNotBlank(progress.getIdent())) {
                log.debug("[SKATTEKORT] Processing status for ident: {}, status: '{}'", 
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

        var statusMap = new HashMap<String, Set<String>>();
        statusIdents.forEach((key, identsSet) -> {
            String melding = key;

            if (melding.contains("|")) {
                var parts = melding.split("\\|", 2);
                var orgYear = parts[0];
                var status = parts[1];

                if (status.equals("Skattekort lagret")) {
                    melding = "OK";
                } else {
                    var orgYearParts = orgYear.split("\\+");
                    if (orgYearParts.length >= 2) {
                        melding = "FEIL: organisasjon:" + orgYearParts[0] + ", inntektsår:" + orgYearParts[1] + ", melding:" + decodeMsg(status);
                    } else {
                        melding = "FEIL: " + decodeMsg(status);
                    }
                }
            } else {
                melding = decodeMsg(melding);
            }

            if (statusMap.containsKey(melding)) {
                statusMap.get(melding).addAll(identsSet);
            } else {
                statusMap.put(melding, new HashSet<>(identsSet));
            }
        });

        var statuser = statusMap.entrySet().stream()
                .map(entry -> {
                    log.debug("[SKATTEKORT] Status: '{}', identer: {}", entry.getKey(), entry.getValue().size());
                    return RsStatusRapport.Status.builder()
                            .melding(entry.getKey())
                            .identer(entry.getValue().stream().toList())
                            .build();
                })
                .toList();

        log.debug("[SKATTEKORT] Returning status rapport with {} status messages", statuser.size());

        return List.of(RsStatusRapport.builder()
                .id(SKATTEKORT)
                .navn(SKATTEKORT.getBeskrivelse())
                .statuser(statuser)
                .build());
    }
}
