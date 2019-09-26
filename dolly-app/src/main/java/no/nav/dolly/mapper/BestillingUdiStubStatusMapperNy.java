package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.UDISTUB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingUdiStubStatusMapperNy {

    public static List<RsStatusRapport> buildUdiStubStatusMap(List<BestillingProgress> progressList) {

        Map<String, List<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getUdistubStatus())) {
                if (statusMap.containsKey(progress.getUdistubStatus())) {
                    statusMap.get(progress.getUdistubStatus()).add(progress.getIdent());
                } else {
                    statusMap.put(progress.getUdistubStatus(), newArrayList(progress.getIdent()));
                }
            }
        });

        return statusMap.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(UDISTUB).navn(UDISTUB.getBeskrivelse())
                        .statuser(statusMap.entrySet().stream()
                                .map(entry -> RsStatusRapport.Status.builder()
                                        .melding(entry.getKey())
                                        .identer(entry.getValue())
                                        .build())
                                .collect(Collectors.toList()))
                        .build());
    }
}
