package no.nav.dolly.mapper;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusIdent;

public final class BestillingKrrStubStatusMapper {

    private BestillingKrrStubStatusMapper() {
    }

    public static List<RsStatusIdent> buildKrrStubStatusMap(List<BestillingProgress> progressList) {

        Map<String, List<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getKrrstubStatus())) {
                if (statusMap.containsKey(progress.getKrrstubStatus())) {
                    statusMap.get(progress.getKrrstubStatus()).add(progress.getIdent());
                } else {
                    statusMap.put(progress.getKrrstubStatus(), Lists.newArrayList(progress.getIdent()));
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
