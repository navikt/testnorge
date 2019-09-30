package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.checkAndUpdateStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusMiljoeIdentForhold;

@Deprecated
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingInstdataStatusMapper {

    public static List<RsStatusMiljoeIdentForhold> buildInstdataStatusMap(List<BestillingProgress> progressList) {
        //  status     miljø       ident       forhold
        Map<String, Map<String, Map<String, List<String>>>> errorEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getInstdataStatus())) {
                newArrayList(progress.getInstdataStatus().split(",")).forEach(status -> {
                    String environ = status.split(":", 2)[0];
                    String errMsg = status.split(":", 2)[1].trim();
                    checkAndUpdateStatus(errorEnvIdents, progress.getIdent(), environ, errMsg);
                });
            }
        });

        List<RsStatusMiljoeIdentForhold> identInstdataStatuses = new ArrayList<>();
        errorEnvIdents.keySet().forEach(status ->
                identInstdataStatuses.add(RsStatusMiljoeIdentForhold.builder()
                        .statusMelding(status)
                        .environmentIdentsForhold(errorEnvIdents.get(status))
                        .build())
        );
        return identInstdataStatuses;
    }
}
