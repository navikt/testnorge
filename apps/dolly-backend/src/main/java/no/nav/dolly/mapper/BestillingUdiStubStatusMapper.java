package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.SystemTyper.UDISTUB;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingUdiStubStatusMapper {

    public static List<RsStatusRapport> buildUdiStubStatusMap(List<BestillingProgress> progressList) {

        Map<String, List<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getUdistubStatus())) {
                if (statusMap.containsKey(progress.getUdistubStatus())) {
                    statusMap.get(progress.getUdistubStatus()).add(progress.getIdent());
                } else {
                    statusMap.put(progress.getUdistubStatus(), new ArrayList<>(List.of(progress.getIdent())));
                }
            }
        });

        return statusMap.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(UDISTUB).navn(UDISTUB.getBeskrivelse())
                        .statuser(statusMap.entrySet().stream()
                                .map(entry -> RsStatusRapport.Status.builder()
                                        .melding(entry.getKey().replaceAll("=", ":"))
                                        .identer(entry.getValue())
                                        .build())
                                .collect(Collectors.toList()))
                        .build());
    }
}
