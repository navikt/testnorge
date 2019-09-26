package no.nav.dolly.mapper;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusMiljoeIdent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Deprecated
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingTpsfStatusMapper {

    private static final String SUCCESS = "OK";

    public static List<RsStatusMiljoeIdent> buildTpsfStatusMap(List<BestillingProgress> progressList) {
        Map<String, Map<String, Set<String>>> errorEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getFeil())) {
                asList(progress.getFeil().split(",")).forEach(error -> {
                    String environ = error.split(":", 2)[0];
                    String errMsg = error.split(":", 2)[1].trim();
                    checkNUpdateStatus(errorEnvIdents, progress.getIdent(), environ, errMsg);
                });
            }
            if (nonNull(progress.getTpsfSuccessEnv())) {
                asList(progress.getTpsfSuccessEnv().split(",")).forEach(environ ->
                        checkNUpdateStatus(errorEnvIdents, progress.getIdent(), environ, SUCCESS)
                );
            }
        });

        List<RsStatusMiljoeIdent> identTpsStatuses = new ArrayList<>();
        errorEnvIdents.keySet().forEach(status ->
                identTpsStatuses.add(RsStatusMiljoeIdent.builder()
                        .statusMelding(status)
                        .environmentIdents(errorEnvIdents.get(status))
                        .build())
        );
        return identTpsStatuses;
    }

    private static void checkNUpdateStatus(Map<String, Map<String, Set<String>>> errorEnvIdents, String ident, String environ, String status) {

        if (errorEnvIdents.containsKey(status)) {
            if (errorEnvIdents.get(status).containsKey(environ)) {
                errorEnvIdents.get(status).get(environ).add(ident);
            } else {
                errorEnvIdents.get(status).put(environ, new HashSet<>(Collections.singleton(ident)));
            }
        } else {
            Map<String, Set<String>> entry = new HashMap<>();
            entry.put(environ, new HashSet<>(Collections.singleton(ident)));
            errorEnvIdents.put(status, entry);
        }
    }
}
