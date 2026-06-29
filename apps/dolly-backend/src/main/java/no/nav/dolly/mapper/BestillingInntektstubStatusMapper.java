package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.SystemTyper.INNTK;
import static no.nav.dolly.mapper.StatusMiljoeIdentForholdUtility.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingInntektstubStatusMapper {

    public static List<RsStatusRapport> buildInntektstubStatusMap(List<BestillingProgress> progressList) {

        Map<String, Set<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getInntektstubStatus())) {
                Arrays.stream(progress.getInntektstubStatus().split(","))
                        .filter(melding -> statusMap.keySet().stream().noneMatch(status -> status.contains(melding)))
                        .forEach(melding -> statusMap.computeIfAbsent(melding, _ -> new HashSet<>()).add(progress.getIdent()));
            }
        });

        return statusMap.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(INNTK).navn(INNTK.getBeskrivelse())
                        .statuser(statusMap.entrySet().stream()
                                .map(entry -> RsStatusRapport.Status.builder()
                                        .melding(decodeMsg(entry.getKey()))
                                        .identer(entry.getValue().stream().toList())
                                        .build())
                                .toList())
                        .build());
    }
}
