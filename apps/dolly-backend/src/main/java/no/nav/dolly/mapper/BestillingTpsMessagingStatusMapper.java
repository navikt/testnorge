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
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.TPS_MESSAGING;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingTpsMessagingStatusMapper {

    public static List<RsStatusRapport> buildTpsMessagingStatusMap(List<BestillingProgress> progressList) {

        Map<String, List<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getTpsMessagingStatus())) {
                List<String> statusPerMiljoe = List.of(progress.getTpsMessagingStatus().split(","));
                statusPerMiljoe.forEach(status -> {
                    var formattedStatus = status.replaceAll("=\\s?", ":");
                    if (statusMap.containsKey(formattedStatus)) {
                        statusMap.get(formattedStatus).add(progress.getIdent());
                    } else {
                        statusMap.put(formattedStatus,
                                new ArrayList<>(List.of(progress.getIdent())));
                    }
                });
            }
        });

        if (statusMap.isEmpty()) {
            return emptyList();
        }

        return singletonList(RsStatusRapport.builder().id(TPS_MESSAGING).navn(TPS_MESSAGING.getBeskrivelse())
                .statuser(statusMap.entrySet().stream()
                        .map(entry -> RsStatusRapport.Status.builder()
                                .melding(entry.getKey().contains(":OK")
                                        ? "OK"
                                        : entry.getKey().substring(entry.getKey().indexOf(":") + 1))
                                .identer(entry.getValue())
                                .detaljert(singletonList(RsStatusRapport.Detaljert.builder()
                                        .identer(entry.getValue())
                                        .miljo(entry.getKey().contains("#")
                                                ? entry.getKey().substring(entry.getKey().lastIndexOf("#") + 1, entry.getKey().lastIndexOf(":"))
                                                : "NA")
                                        .build()))
                                .build())
                        .toList())
                .build());
    }
}
