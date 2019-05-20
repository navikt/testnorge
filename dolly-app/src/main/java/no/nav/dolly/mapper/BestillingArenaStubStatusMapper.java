package no.nav.dolly.mapper;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusIdent;

public final class BestillingArenaStubStatusMapper {

    private BestillingArenaStubStatusMapper() {
    }

    public static List<RsStatusIdent> buildArenaStubStatusMap(List<BestillingProgress> progressList) {

        Map<String, List<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getArenaforvalterStatus())) {
                if (statusMap.containsKey(progress.getArenaforvalterStatus())) {
                    statusMap.get(progress.getArenaforvalterStatus()).add(progress.getIdent());
                } else {
                    statusMap.put(progress.getArenaforvalterStatus(), Lists.newArrayList(progress.getIdent()));
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
