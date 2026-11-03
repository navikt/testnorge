package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import no.nav.dolly.domain.resultset.SystemTyper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.resultset.SystemTyper.INST2;
import static no.nav.dolly.domain.resultset.SystemTyper.INST_KDI;
import static no.nav.dolly.mapper.StatusMiljoeIdentForholdUtility.decodeMsg;
import static no.nav.dolly.mapper.StatusMiljoeIdentForholdUtility.resolveStatus;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingInstdataStatusMapper {

    private static final String FEIL = "Feil: ";
    private static final String INST2_STATUS = "INST2_STATUS";
    private static final String KDI_STATUS = "KDI_STATUS";

    public static List<RsStatusRapport> buildInstdataStatusMap(List<BestillingProgress> progressList) {

        // system     status     miljø       ident
        Map<String, Map<String, Map<String, Set<String>>>> systemStatusEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getInstdataStatus())) {
                for (var system : progress.getInstdataStatus().split("\\|")) {
                    String systemId;
                    String statuser;
                    if (system.contains("#")) {
                        var parts = system.split("#", 2);
                        systemId = parts[0];
                        statuser = parts[1];
                    } else {
                        systemId = INST2_STATUS;
                        statuser = system;
                    }
                    if (isNotBlank(statuser)) {
                        for (var status : statuser.split(",")) {
                            var environErrMsg = status.split(":", 2);
                            var environ = environErrMsg[0];
                            if (environErrMsg.length > 1 && isNotBlank(environErrMsg[1]) && isNotBlank(progress.getIdent())) {
                                var errMsg = decodeMsg(environErrMsg[1]);
                                checkAndUpdateStatus(systemStatusEnvIdents, systemId, progress.getIdent(), environ, errMsg);
                            }
                        }
                    }
                }
            }
        });

        systemStatusEnvIdents.values().forEach(statusEnvIdents -> {
            var okEnvIdents = statusEnvIdents.get("OK");
            if (isNull(okEnvIdents)) {
                return;
            }

            statusEnvIdents.forEach((status, envIdents) -> {
                if (!"OK".equals(status)) {
                    envIdents.forEach((environment, idents) ->
                            okEnvIdents.computeIfPresent(environment, (_, okIdents) -> {
                                okIdents.removeAll(idents);
                                return okIdents.isEmpty() ? null : okIdents;
                            }));
                }
            });

            if (okEnvIdents.isEmpty()) {
                statusEnvIdents.remove("OK");
            }
        });

        var statusRapporter = new ArrayList<RsStatusRapport>();
        statusRapporter.addAll(extractStatus(systemStatusEnvIdents, INST2_STATUS, INST2));
        statusRapporter.addAll(extractStatus(systemStatusEnvIdents, KDI_STATUS, INST_KDI));
        return statusRapporter;
    }

    private static List<RsStatusRapport> extractStatus(Map<String, Map<String, Map<String, Set<String>>>> meldStatusMiljoeIdents, String clientid, SystemTyper type) {

        if (!meldStatusMiljoeIdents.containsKey(clientid)) {
            return List.of();
        }
        return List.of(RsStatusRapport.builder()
                .id(type)
                .navn(type.getBeskrivelse())
                .statuser(
                        meldStatusMiljoeIdents.get(clientid).entrySet().stream().map(entry -> {
                                    var status = decodeMsg(entry.getKey());
                                    if (status.contains(FEIL)) {
                                        status = FEIL + status.replace(FEIL, "");
                                    }
                                    return RsStatusRapport.Status.builder()
                                            .melding(status)
                                            .detaljert(entry.getValue().entrySet().stream().map(miljeStatus ->
                                                            RsStatusRapport.Detaljert.builder()
                                                                    .miljo(miljeStatus.getKey())
                                                                    .identer(miljeStatus.getValue())
                                                                    .build())
                                                    .toList())
                                            .build();
                                })
                                .toList())
                .build());
    }

    private static void checkAndUpdateStatus(Map<String, Map<String, Map<String, Set<String>>>> systemStatusEnvIdents,
                                             String system, String ident, String environ, String errMsg) {

        var status = resolveStatus(errMsg);
        systemStatusEnvIdents
                .computeIfAbsent(system, _ -> new HashMap<>())
                .computeIfAbsent(status, _ -> new HashMap<>())
                .computeIfAbsent(environ, _ -> new HashSet<>())
                .add(ident);
    }
}
