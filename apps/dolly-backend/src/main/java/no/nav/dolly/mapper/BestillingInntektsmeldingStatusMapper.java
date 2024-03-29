package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.SystemTyper.INNTKMELD;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.checkAndUpdateStatus;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingInntektsmeldingStatusMapper {

    public static List<RsStatusRapport> buildInntektsmeldingStatusMap(List<BestillingProgress> progressList) {

        //  status     miljø       ident
        Map<String, Map<String, Set<String>>> statusEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getInntektsmeldingStatus())) {
                List.of(progress.getInntektsmeldingStatus().split(",")).forEach(status -> {
                    var environErrMsg = status.split(":", 2);
                    var environ = environErrMsg[0];
                    if (environErrMsg.length > 1 && isNotBlank(environErrMsg[1]) && isNotBlank(progress.getIdent())) {
                        var errMsg = decodeMsg(environErrMsg[1]);
                        checkAndUpdateStatus(statusEnvIdents, progress.getIdent(), environ, errMsg);
                    }
                });
            }
        });

        return statusEnvIdents.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(INNTKMELD).navn(INNTKMELD.getBeskrivelse())
                        .statuser(statusEnvIdents.entrySet().stream().map(status -> RsStatusRapport.Status.builder()
                                        .melding(status.getKey())
                                        .detaljert(status.getValue().entrySet().stream().map(envIdent -> RsStatusRapport.Detaljert.builder()
                                                        .miljo(envIdent.getKey())
                                                        .identer(envIdent.getValue())
                                                        .build())
                                                .toList())
                                        .build())
                                .toList())
                        .build());
    }
}