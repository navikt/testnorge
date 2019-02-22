package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsIdentTpsStatus;

public final class BestillingTpsfStatusMapper {

    private static final String SUCCESS = "OK";

    private BestillingTpsfStatusMapper() {
    }

    public static List<RsIdentTpsStatus> buildTpsfStatusMap(List<BestillingProgress> progressList) {
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

        List<RsIdentTpsStatus> identTpsStatuses = new ArrayList<>();
        errorEnvIdents.keySet().forEach(env ->
                identTpsStatuses.add(RsIdentTpsStatus.builder()
                        .statusMelding(env)
                        .environmentIdents(errorEnvIdents.get(env))
                        .build())
        );
        return identTpsStatuses;
    }

    private static void checkNUpdateStatus(Map<String, Map<String, Set<String>>> errorEnvIdents, String ident, String environ, String status) {
        if (errorEnvIdents.containsKey(status)) {
            if (errorEnvIdents.get(status).containsKey(environ)) {
                errorEnvIdents.get(status).get(environ).add(ident);
            } else {
                errorEnvIdents.get(status).put(environ, newHashSet(ident));
            }
        } else {
            Map<String, Set<String>> entry = new HashMap();
            entry.put(environ, newHashSet(ident));
            errorEnvIdents.put(status, entry);
        }
    }
}
