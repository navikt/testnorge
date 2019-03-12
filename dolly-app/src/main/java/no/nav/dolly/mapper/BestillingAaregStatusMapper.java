package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsIdentSystemStatus;

public final class BestillingAaregStatusMapper extends BestillingSystemStatusMapper {

    private BestillingAaregStatusMapper() {

    }

    public static List<RsIdentSystemStatus> buildAaregStatusMap(List<BestillingProgress> progressList) {
        Map<String, Map<String, Set<String>>> errorEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getAaregStatus())) {
                newArrayList(progress.getAaregStatus().split(",")).forEach(status -> {
                    String environ = status.split(":", 2)[0];
                    String errMsg = status.split(":", 2)[1].trim();
                    checkNUpdateStatus(errorEnvIdents, progress.getIdent(), environ, errMsg);
                });
            }
        });

        List<RsIdentSystemStatus> identAaaregStatuses = new ArrayList<>();
        errorEnvIdents.keySet().forEach(env ->
                identAaaregStatuses.add(RsIdentSystemStatus.builder()
                        .statusMelding(env)
                        .environmentIdents(errorEnvIdents.get(env))
                        .build())
        );
        return identAaaregStatuses;
    }
}
