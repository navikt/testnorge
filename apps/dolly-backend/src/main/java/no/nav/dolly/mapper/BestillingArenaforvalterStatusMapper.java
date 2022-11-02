package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.SystemTyper.ARENA;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingArenaforvalterStatusMapper {

    public static List<RsStatusRapport> buildArenaStatusMap(List<BestillingProgress> progressList) {

        // status    environment    ident
        Map<String, Map<String, List<String>>> statusEnvIdents = new HashMap();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getArenaforvalterStatus())) {
                List.of(progress.getArenaforvalterStatus().split(",")).forEach(
                        entry -> {
                            var envStatus = entry.split("\\$");
                            var environment = envStatus[0];
                            var status = decodeMsg(envStatus.length > 1 ? envStatus[1] : "");
                            AbstractRsMeldingStatusMapper.buildStatusMap(statusEnvIdents, status, environment, progress.getIdent());
                        });
            }
        });

        return statusEnvIdents.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(ARENA).navn(ARENA.getBeskrivelse())
                        .statuser(statusEnvIdents.entrySet().stream().map(entry -> RsStatusRapport.Status.builder()
                                        .melding(decodeMsg(entry.getKey()))
                                        .detaljert(entry.getValue().entrySet().stream().map(entry1 -> RsStatusRapport.Detaljert.builder()
                                                .miljo(entry1.getKey())
                                                .identer(entry1.getValue())
                                                .build())
                                                .toList())
                                        .build())
                                .toList())
                        .build());
    }
}