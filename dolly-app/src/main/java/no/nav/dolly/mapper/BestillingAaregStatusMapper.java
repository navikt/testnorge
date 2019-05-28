package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singleton;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.list.TreeList;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusMiljoeIdentForhold;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingAaregStatusMapper {

    public static List<RsStatusMiljoeIdentForhold> buildAaregStatusMap(List<BestillingProgress> progressList) {
        //  status     miljø       ident       forhold
        Map<String, Map<String, Map<String, List<String>>>> errorEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getAaregStatus())) {
                newArrayList(progress.getAaregStatus().split(",")).forEach(status -> {
                    String environ = status.split(":", 2)[0];
                    String errMsg = status.split(":", 2)[1].trim();
                    checkNUpdateStatus(errorEnvIdents, progress.getIdent(), environ, errMsg);
                });
            }
        });

        List<RsStatusMiljoeIdentForhold> identAaregStatuses = new ArrayList<>();
        errorEnvIdents.keySet().forEach(status ->
                identAaregStatuses.add(RsStatusMiljoeIdentForhold.builder()
                        .statusMelding(status)
                        .environmentIdentsForhold(errorEnvIdents.get(status))
                        .build())
        );
        return identAaregStatuses;
    }

    private static void checkNUpdateStatus(Map<String, Map<String, Map<String, List<String>>>> errorEnvIdents, String ident, String environ, String errMsg) {

        if (errMsg.contains("$")) {
            String forhold = errMsg.split("\\$")[0].replace("=", ": ");
            String status = errMsg.split("\\$")[1].replace('&', ',').replace('=', ':');
            if (errorEnvIdents.containsKey(status)) {
                if (errorEnvIdents.get(status).containsKey(environ)) {
                    if (errorEnvIdents.get(status).get(environ).containsKey(ident)) {
                        errorEnvIdents.get(status).get(environ).get(ident).add(forhold);
                    } else {
                        errorEnvIdents.get(status).get(environ).put(ident, new TreeList(singleton(forhold)));
                    }
                } else {
                    Map<String, List<String>> identEntry = new HashMap();
                    identEntry.put(ident, new TreeList(singleton(forhold)));
                    errorEnvIdents.get(status).put(environ, identEntry);
                }
            } else {
                Map<String, Map<String, List<String>>> environEntry = new HashMap();
                Map<String, List<String>> identEntry = new HashMap();
                environEntry.put(environ, identEntry);
                identEntry.put(ident, new TreeList(singleton(forhold)));
                errorEnvIdents.put(status, environEntry);
            }
        }
    }
}
