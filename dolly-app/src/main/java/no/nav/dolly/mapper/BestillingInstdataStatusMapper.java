package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.nonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsMeldingStatusIdent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingInstdataStatusMapper {

    public static List<RsMeldingStatusIdent> buildInstdataStatusMap(List<BestillingProgress> progressList) {

        // status    environment    ident
        Map<String, Map<String, List<String>>> statusEnvIdents = new HashMap();

        progressList.forEach(progress -> {
            if (nonNull(progress.getInstdataStatus())) {
                newArrayList(progress.getInstdataStatus().split(",")).forEach(
                        entry -> {
                            String[] envStatus = entry.split("\\$");
                            String environment = envStatus[0];
                            String status = (envStatus.length > 1 ? envStatus[1] : "").replace('=',',');
                            AbstractRsMeldingStatusMapper.buildStatusMap(statusEnvIdents, status, environment, progress.getIdent());
                        });
            }
        });

        return BestillingMeldingStatusIdentMapper.prepareResult(statusEnvIdents);
    }
}
