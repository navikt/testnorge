package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.mapper.BestillingMeldingStatusIdentMapper.prepareResult;
import static no.nav.dolly.mapper.BestillingMeldingStatusIdentMapper.resolveStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsMeldingStatusIdent;

public final class BestillingArenaforvalterStatusMapper {

    private BestillingArenaforvalterStatusMapper() {
    }

    public static List<RsMeldingStatusIdent> buildArenaforvalterStatusMap(List<BestillingProgress> progressList) {

        //  melding     status      ident
        Map<String, Map<String, List<String>>> msgStatusIdents = new HashMap();

        progressList.forEach(progress -> {
            if (nonNull(progress.getArenaforvalterStatus())) {
                newArrayList(progress.getArenaforvalterStatus().split("\\$")).forEach(
                        resolveStatus(msgStatusIdents, progress)
                );
            }
        });

        return prepareResult(msgStatusIdents);
    }
}
