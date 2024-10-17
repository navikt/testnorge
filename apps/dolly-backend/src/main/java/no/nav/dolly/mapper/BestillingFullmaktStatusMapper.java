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
import static no.nav.dolly.domain.resultset.SystemTyper.FULLMAKT;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static no.nav.dolly.util.ListUtil.listOf;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingFullmaktStatusMapper {

    private static final String OK_STATUS = "Synkronisering mot fullmakt (Representasjon) tok";

    public static List<RsStatusRapport> buildFullmaktStatusMap(List<BestillingProgress> progressList) {

        Map<String, List<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getFullmaktStatus())) {
                var status = progress.getFullmaktStatus().contains(OK_STATUS) ? "OK" : progress.getFullmaktStatus();
                if (statusMap.containsKey(status)) {
                    statusMap.get(progress.getFullmaktStatus()).add(progress.getIdent());
                } else {
                    statusMap.put(progress.getFullmaktStatus(), listOf(progress.getIdent()));
                }
            }
        });

        return statusMap.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(FULLMAKT).navn(FULLMAKT.getBeskrivelse())
                        .statuser(statusMap.entrySet().stream().map(entry -> RsStatusRapport.Status.builder()
                                        .melding(decodeMsg(entry.getKey()))
                                        .identer(entry.getValue())
                                        .build())
                                .toList())
                        .build());
    }
}
