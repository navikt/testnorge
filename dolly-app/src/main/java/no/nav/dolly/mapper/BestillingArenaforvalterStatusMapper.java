package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.nonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsMeldingStatusIdent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingArenaforvalterStatusMapper {

    public static List<RsMeldingStatusIdent> buildArenaStatusMap(List<BestillingProgress> progressList) {

        // status    environment    ident
        Map<String, Map<String, List<String>>> statusEnvIdents = new HashMap();

        progressList.forEach(progress -> {
            if (nonNull(progress.getArenaforvalterStatus())) {
                newArrayList(progress.getArenaforvalterStatus().split(",")).forEach(
                        entry -> {
                            String environment = entry.split("\\$")[0];
                            String status = entry.split("\\$")[1];
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
                statusEnvIdents.get(status).put(environment, newArrayList(ident));
            }
        } else {
            Map envIdent = new HashMap();
            envIdent.put(environment, newArrayList(ident));
            statusEnvIdents.put(status, envIdent);
        }
    }
}
