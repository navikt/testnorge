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

public final class BestillingTpsfStatusMapper extends BestillingSystemStatusMapper {

    private static final String SUCCESS = "OK";

    private BestillingTpsfStatusMapper() {
    }

    public static List<RsIdentSystemStatus> buildTpsfStatusMap(List<BestillingProgress> progressList) {
        Map<String, Map<String, Set<String>>> errorEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getFeil())) {
                newArrayList(progress.getFeil().split(",")).forEach(error -> {
                    String environ = error.split(":", 2)[0];
                    String errMsg = error.split(":", 2)[1].trim();
                    checkNUpdateStatus(errorEnvIdents, progress.getIdent(), environ, errMsg);
                });
            }
            if (nonNull(progress.getTpsfSuccessEnv())) {
                newArrayList(progress.getTpsfSuccessEnv().split(",")).forEach(environ ->
                        checkNUpdateStatus(errorEnvIdents, progress.getIdent(), environ, SUCCESS)
                );
            }
        });

        List<RsIdentSystemStatus> identTpsStatuses = new ArrayList<>();
        errorEnvIdents.keySet().forEach(env ->
                identTpsStatuses.add(RsIdentSystemStatus.builder()
                        .statusMelding(env)
                        .environmentIdents(errorEnvIdents.get(env))
                        .build())
        );
        return identTpsStatuses;
    }
}
