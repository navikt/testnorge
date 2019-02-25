package no.nav.dolly.mapper;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsIdentStatus;

public final class BestillingSigrunStubStatusMapper {

    private BestillingSigrunStubStatusMapper() {
    }

    public static List<RsIdentStatus> buildSigrunStubStatusMap(List<BestillingProgress> progressList) {

        Map<String, List<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getSigrunstubStatus())) {
                if (statusMap.containsKey(progress.getSigrunstubStatus())) {
                    statusMap.get(progress.getSigrunstubStatus()).add(progress.getIdent());
                } else {
                    statusMap.put(progress.getSigrunstubStatus(), Lists.newArrayList(progress.getIdent()));
                }
            }
        });

        List<RsIdentStatus> identStatus = new ArrayList<>();
        statusMap.forEach((key, value) ->
                identStatus.add(RsIdentStatus.builder()
                        .statusMelding(key)
                        .identer(value)
                        .build())
        );
        return identStatus;
    }

}
