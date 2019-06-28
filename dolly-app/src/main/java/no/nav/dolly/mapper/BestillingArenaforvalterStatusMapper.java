package no.nav.dolly.mapper;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsMeldingStatusIdent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingArenaforvalterStatusMapper {

    public static List<RsMeldingStatusIdent> buildArenaStatusMap(List<BestillingProgress> progressList) {

        // status    environment    ident
        Map<String, Map<String, List<String>>> statusEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getArenaforvalterStatus())) {
                asList(progress.getArenaforvalterStatus().split(",")).forEach(
                        entry -> {
                            String[] envStatus = entry.split("\\$");
                            String environment = envStatus[0];
                            String status = (envStatus.length > 1 ? envStatus[1] : "").replace('=', ',');
                            buildStatusMap(statusEnvIdents, status, environment, progress.getIdent());
                        });
            }
        });

        return BestillingMeldingStatusIdentMapper.prepareResult(statusEnvIdents);
    }

    private static void buildStatusMap(Map<String, Map<String, List<String>>> statusEnvIdents, String status, String environment, String ident) {

        if (statusEnvIdents.containsKey(status)) {
            if (statusEnvIdents.get(status).containsKey(environment)) {
                statusEnvIdents.get(status).get(environment).add(ident);
            } else {
                statusEnvIdents.get(status).put(environment, singletonList(ident));
            }
        } else {
            Map<String, List<String>> envIdent = new HashMap<>();
            envIdent.put(environment, singletonList(ident));
            statusEnvIdents.put(status, envIdent);
        }
    }
}
