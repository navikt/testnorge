package no.nav.dolly.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.SystemTyper.PDL_FORVALTER;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static no.nav.dolly.util.ListUtil.listOf;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@UtilityClass
public final class BestillingPdlForvalterStatusMapper {

    public static List<RsStatusRapport> buildPdlForvalterStatusMap(List<BestillingProgress> bestProgress) {

        Map<String, List<String>> statusMap = new HashMap<>();

        bestProgress.forEach(progress -> {
            if (isNotBlank(progress.getPdlForvalterStatus())) {
                if (statusMap.containsKey(progress.getPdlForvalterStatus())) {
                    statusMap.get(progress.getPdlForvalterStatus()).add(progress.getIdent());
                } else {
                    statusMap.put(progress.getPdlForvalterStatus(), listOf(progress.getIdent()));
                }
            }
        });

        return statusMap.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(PDL_FORVALTER).navn(PDL_FORVALTER.getBeskrivelse())
                        .statuser(statusMap.entrySet().stream().map(entry -> RsStatusRapport.Status.builder()
                                        .melding(decodeMsg(entry.getKey()))
                                        .identer(entry.getValue())
                                        .build())
                                .toList())
                        .build());
    }
}