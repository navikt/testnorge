package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.SystemTyper.SKJERMINGSREGISTER;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingSkjermingsRegisterStatusMapper {

    public static List<RsStatusRapport> buildSkjermingsRegisterStatusMap(List<BestillingProgress> progressList) {

        Map<String, List<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getSkjermingsregisterStatus())) {
                if (statusMap.containsKey(progress.getSkjermingsregisterStatus())) {
                    statusMap.get(progress.getSkjermingsregisterStatus()).add(progress.getIdent());
                } else {
                    statusMap.put(progress.getSkjermingsregisterStatus(), new ArrayList<>(List.of(progress.getIdent())));
                }
            }
        });

        return statusMap.isEmpty() ? emptyList()
                : singletonList(RsStatusRapport.builder().id(SKJERMINGSREGISTER).navn(SKJERMINGSREGISTER.getBeskrivelse())
                .statuser(statusMap.entrySet().stream()
                        .map(entry -> RsStatusRapport.Status.builder()
                                .melding(decodeMsg(entry.getKey()))
                                .identer(entry.getValue())
                                .build())
                        .toList())
                .build());
    }
}
