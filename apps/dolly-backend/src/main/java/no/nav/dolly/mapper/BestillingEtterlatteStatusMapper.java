package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.SystemTyper.ETTERLATTE;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static no.nav.dolly.util.ListUtil.listOf;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingEtterlatteStatusMapper {

    public static List<RsStatusRapport> buildEtterlatteStatusMap(List<BestillingProgress> progressList) {

        Map<String, List<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getEtterlatteStatus())) {
                if (statusMap.containsKey(progress.getEtterlatteStatus())) {
                    statusMap.get(progress.getEtterlatteStatus()).add(progress.getIdent());
                } else {
                    statusMap.put(progress.getEtterlatteStatus(), listOf(progress.getIdent()));
                }
            }
        });

        return statusMap.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(ETTERLATTE).navn(ETTERLATTE.getBeskrivelse())
                        .statuser(statusMap.entrySet().stream().map(entry -> RsStatusRapport.Status.builder()
                                        .melding(decodeMsg(entry.getKey()))
                                        .identer(entry.getValue())
                                        .build())
                                .toList())
                        .build());
    }
}
