package no.nav.dolly.mapper.strategy;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.SYNT_SYKEMELDING;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.checkAndUpdateStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BestillingSyntSykemeldingStatusMapper {

    public static List<RsStatusRapport> buildSyntSykemeldingStatusMap(List<BestillingProgress> progressList) {

        //  status     miljø       ident
        Map<String, Map<String, Set<String>>> statusEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getSyntSykemeldingStatus())) {
                newArrayList(progress.getSyntSykemeldingStatus().split(",")).forEach(status -> {
                    String[] environErrMsg = status.split(":", 2);
                    String environ = environErrMsg[0];
                    String errMsg = environErrMsg.length > 1 ? environErrMsg[1].trim().replaceAll("&", ",") : "";
                    checkAndUpdateStatus(statusEnvIdents, progress.getIdent(), environ, errMsg);
                });
            }
        });

        return statusEnvIdents.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(SYNT_SYKEMELDING).navn(SYNT_SYKEMELDING.getBeskrivelse())
                        .statuser(statusEnvIdents.entrySet().stream().map(status -> RsStatusRapport.Status.builder()
                                .melding(status.getKey())
                                .detaljert(status.getValue().entrySet().stream().map(envIdent -> RsStatusRapport.Detaljert.builder()
                                        .miljo(envIdent.getKey())
                                        .identer(newArrayList(envIdent.getValue()))
                                        .build())
                                        .collect(Collectors.toList()))
                                .build())
                                .collect(Collectors.toList()))
                        .build());
    }

}
