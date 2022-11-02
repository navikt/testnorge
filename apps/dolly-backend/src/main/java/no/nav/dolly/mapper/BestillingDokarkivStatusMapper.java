package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.SystemTyper.DOKARKIV;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.checkAndUpdateStatus;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingDokarkivStatusMapper {

    public static List<RsStatusRapport> buildDokarkivStatusMap(List<BestillingProgress> progressList) {

        //  status     miljø       ident
        Map<String, Map<String, Set<String>>> statusEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getDokarkivStatus())) {
                List.of(progress.getDokarkivStatus().split(",")).forEach(status -> {
                    var environErrMsg = status.split(":", 2);
                    var environ = environErrMsg[0];
                    var errMsg = decodeMsg(environErrMsg.length > 1 ? environErrMsg[1] : "");
                    checkAndUpdateStatus(statusEnvIdents, progress.getIdent(), environ, errMsg);
                });
            }
        });

        return statusEnvIdents.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(DOKARKIV).navn(DOKARKIV.getBeskrivelse())
                        .statuser(statusEnvIdents.entrySet().stream().map(status -> RsStatusRapport.Status.builder()
                                        .melding(decodeMsg(status.getKey()))
                                        .detaljert(status.getValue().entrySet().stream().map(envIdent -> RsStatusRapport.Detaljert.builder()
                                                        .miljo(envIdent.getKey())
                                                        .identer(new ArrayList<>(envIdent.getValue()))
                                                        .build())
                                                .toList())
                                        .build())
                                .toList())
                        .build());
    }
}