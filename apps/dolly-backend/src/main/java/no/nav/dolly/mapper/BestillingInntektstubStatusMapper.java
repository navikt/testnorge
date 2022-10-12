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
import static no.nav.dolly.domain.resultset.SystemTyper.INNTK;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingInntektstubStatusMapper {

    public static List<RsStatusRapport> buildInntektstubStatusMap(List<BestillingProgress> progressList) {

        Map<String, List<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getInntektstubStatus())) {
                if (statusMap.containsKey(progress.getInntektstubStatus())) {
                    statusMap.get(progress.getInntektstubStatus()).add(progress.getIdent());
                } else {
                    statusMap.put(progress.getInntektstubStatus(), new ArrayList<>(List.of(progress.getIdent())));
                }
            }
        });

        return statusMap.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(INNTK).navn(INNTK.getBeskrivelse())
                        .statuser(statusMap.entrySet().stream()
                                .map(entry -> RsStatusRapport.Status.builder()
                                        .melding(entry.getKey())
                                        .identer(entry.getValue())
                                        .build())
                                .collect(Collectors.toList()))
                        .build());
    }
}
