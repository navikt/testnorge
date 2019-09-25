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
public final class BestillingAaregStatusMapper {

    public static List<RsStatusMiljoeIdentForhold> buildAaregStatusMap(List<BestillingProgress> progressList) {
        //  status     miljø       ident       forhold
        Map<String, Map<String, Map<String, List<String>>>> errorEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getAaregStatus())) {
                newArrayList(progress.getAaregStatus().split(",")).forEach(status -> {
                    String environ = status.split(":", 2)[0];
                    String errMsg = status.split(":", 2)[1].trim();
                    checkAndUpdateStatus(errorEnvIdents, progress.getIdent(), environ, errMsg);
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
}
