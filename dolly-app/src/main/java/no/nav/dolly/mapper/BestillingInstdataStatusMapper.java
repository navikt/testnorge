package no.nav.dolly.mapper;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.checkAndUpdateStatus;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusMiljoeIdentForhold;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingInstdataStatusMapper {

    public static List<RsStatusMiljoeIdentForhold> buildInstdataStatusMap(List<BestillingProgress> progressList) {
        //  status     miljø       ident       forhold
        Map<String, Map<String, Map<String, List<String>>>> errorEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getInstdataStatus())) {
                asList(progress.getInstdataStatus().split(",")).forEach(status -> {
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
