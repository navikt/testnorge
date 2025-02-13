package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.SystemTyper.HISTARK;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingHistarkStatusMapper {

    private static final String FEIL = "FEIL";
    private static final String OK = "OK";

    public static List<RsStatusRapport> buildHistarkStatusMap(List<BestillingProgress> progressList) {

        Map<String, List<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getHistarkStatus())) {
                if (Stream.of(progress.getHistarkStatus().split(","))
                        .allMatch(status -> status.contains(OK) &&
                                !status.contains(FEIL))) {
                    updateMap(statusMap, OK, progress.getIdent());
                } else {
                    Stream.of(progress.getHistarkStatus().split(","))
                            .forEach(status ->
                                updateMap(statusMap, status, progress.getIdent()));
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

    private static void updateMap(Map<String, List<String>> statusMap, String status, String ident) {

        if (statusMap.containsKey(status)) {
            statusMap.get(status).add(ident);
        } else {
            statusMap.put(status, new ArrayList<>(List.of(ident)));
        }
    }
}
