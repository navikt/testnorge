package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.KRRSTUB;
import static no.nav.dolly.util.ListUtil.listOf;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingKrrStubStatusMapper {

    public static List<RsStatusRapport> buildKrrStubStatusMap(List<BestillingProgress> progressList) {

        Map<String, List<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getKrrstubStatus())) {
                if (statusMap.containsKey(progress.getKrrstubStatus())) {
                    statusMap.get(progress.getKrrstubStatus()).add(progress.getIdent());
                } else {
                    statusMap.put(progress.getKrrstubStatus(), listOf(progress.getIdent()));
                }
            }
        });

        return statusMap.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(KRRSTUB).navn(KRRSTUB.getBeskrivelse())
                        .statuser(statusMap.entrySet().stream().map(entry -> RsStatusRapport.Status.builder()
                                        .melding(entry.getKey())
                                        .identer(entry.getValue())
                                        .build())
                                .collect(Collectors.toList()))
                        .build());
    }
}
