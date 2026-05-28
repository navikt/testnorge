package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import no.nav.dolly.domain.resultset.SystemTyper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static no.nav.dolly.domain.resultset.SystemTyper.INST2;
import static no.nav.dolly.domain.resultset.SystemTyper.INST_KDI;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingInstdataStatusMapper {

    private static final String INST2_STATUS = "INST2_STATUS";
    private static final String KDI_STATUS = "KDI_STATUS";
    private static final String OK_RESULT = "OK";

    public static List<RsStatusRapport> buildInstdataStatusMap(List<BestillingProgress> progressList) {

        // system     status     miljø       ident
        Map<String, Map<String, Map<String, Set<String>>>> systemStatusEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getInstdataStatus())) {
                var systemer = progress.getInstdataStatus().split("\\|");
                List.of(systemer).forEach(system -> {
                    String systemId;
                    String statuser;
                    if (system.contains("#")) {
                        systemId = system.split("#")[0];
                        statuser = system.split("#")[1];
                    } else {
                        systemId = INST2_STATUS;
                        statuser = system;
                    }
                    if (isNotBlank(statuser)) {
                        List.of(statuser.split(",")).forEach(status -> {
                            var environErrMsg = status.split(":", 2);
                            var environ = environErrMsg[0];
                            if (environErrMsg.length > 1 && isNotBlank(environErrMsg[1]) && isNotBlank(progress.getIdent())) {
                                var errMsg = decodeMsg(environErrMsg[1]);
                                checkAndUpdateStatus(systemStatusEnvIdents, systemId, progress.getIdent(), environ, errMsg);
                            }
                        });
                    }
                });
            }
        });

        var statusRapporter = new ArrayList<RsStatusRapport>();
        statusRapporter.addAll(extractStatus(systemStatusEnvIdents, INST2_STATUS, INST2));
        statusRapporter.addAll(extractStatus(systemStatusEnvIdents, KDI_STATUS, INST_KDI));
        return statusRapporter;
    }

    private static List<RsStatusRapport> extractStatus(Map<String, Map<String, Map<String, Set<String>>>> meldStatusMiljoeIdents, String clientid, SystemTyper type) {

        if (meldStatusMiljoeIdents.containsKey(clientid)) {
            return Collections.singletonList(RsStatusRapport.builder()
                    .id(type)
                    .navn(type.getBeskrivelse())
                    .statuser(
                            meldStatusMiljoeIdents.get(clientid).entrySet().stream().map(entry ->
                                            RsStatusRapport.Status.builder()
                                                    .melding(decodeMsg(entry.getKey()))
                                                    .detaljert(entry.getValue().entrySet().stream().map(miljeStatus ->
                                                                    RsStatusRapport.Detaljert.builder()
                                                                            .miljo(miljeStatus.getKey())
                                                                            .identer(miljeStatus.getValue())
                                                                            .build())
                                                            .toList())
                                                    .build())
                                    .toList())
                    .build());
        } else {
            return Collections.emptyList();
        }
    }

    public static void checkAndUpdateStatus(Map<String, Map<String, Map<String, Set<String>>>> systemStatusEnvIdents,
                                            String system, String ident, String environ, String errMsg) {

        String status;

        if (errMsg.contains("$")) {
            String[] forholdStatus = errMsg.split("\\$");
            String forhold = forholdStatus[0];
            status = decodeMsg(forholdStatus.length > 1 ? forholdStatus[1] : "");
            if (!OK_RESULT.equals(status)) {
                status = format("%s: %s", forhold, status);
            }
        } else {
            status = decodeMsg(errMsg);
        }

        systemStatusEnvIdents.putIfAbsent(system, new HashMap<>());
        systemStatusEnvIdents.get(system).putIfAbsent(status, new HashMap<>());
        systemStatusEnvIdents.get(system).get(status).putIfAbsent(environ, new HashSet<>());
        systemStatusEnvIdents.get(system).get(status).get(environ).add(ident);
    }
}
