package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.SystemTyper.HISTARK;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingHistarkStatusMapper {

    public static List<RsStatusRapport> buildHistarkStatusMap(List<BestillingProgress> progressList) {

        Map<String, List<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getHistarkStatus())) {
                if (progress.getHistarkStatus().contains("DOK")) {
                    Stream.of(progress.getHistarkStatus().split(","))
                            .forEach(status -> {
                                if (statusMap.containsKey(status)) {
                                    statusMap.get(status).add(progress.getIdent());
                                } else {
                                    statusMap.put(status, new ArrayList<>(List.of(progress.getIdent())));
                                }
                            });
                } else {
                    if (statusMap.containsKey(progress.getHistarkStatus())) {
                        statusMap.get(progress.getHistarkStatus()).add(progress.getIdent());
                    } else {
                        statusMap.put(progress.getHistarkStatus(), new ArrayList<>(List.of(progress.getIdent())));
                    }
                }
            }
        });

        return statusMap.isEmpty() ? emptyList()
                : singletonList(RsStatusRapport.builder().id(HISTARK).navn(HISTARK.getBeskrivelse())
                .statuser(statusMap.entrySet().stream()
                        .map(entry -> RsStatusRapport.Status.builder()
                                .melding(decodeMsg(entry.getKey()))
                                .identer(entry.getValue())
                                .build())
                        .toList())
                .build());
    }
}
