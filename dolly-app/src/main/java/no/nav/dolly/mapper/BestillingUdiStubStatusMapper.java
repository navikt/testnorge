package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.nonNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusIdent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingUdiStubStatusMapper {

    public static List<RsStatusIdent> buildUdiStubStatusMap(List<BestillingProgress> progressList) {

        Map<String, List<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getUdistubStatus())) {
                if (statusMap.containsKey(progress.getUdistubStatus())) {
                    statusMap.get(progress.getUdistubStatus()).add(progress.getIdent());
                } else {
                    statusMap.put(progress.getUdistubStatus(), newArrayList(progress.getIdent()));
                }
            }
        });

        List<RsStatusIdent> identStatus = new ArrayList<>();
        statusMap.forEach((key, value) ->
                identStatus.add(RsStatusIdent.builder()
                        .statusMelding(key)
                        .identer(value)
                        .build())
        );
        return identStatus;
    }
}
