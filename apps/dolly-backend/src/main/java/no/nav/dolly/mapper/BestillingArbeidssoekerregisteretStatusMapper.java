package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.SystemTyper.ARBEIDSSOEKERREGISTERET;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingArbeidssoekerregisteretStatusMapper {

    public static List<RsStatusRapport> buildArbeidssoekerregisteretStatusMap(List<BestillingProgress> progressList) {

        //  status    ident
        Map<String, Set<String>> statusIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getArbeidssoekerregisteretStatus())) {
                List.of(progress.getArbeidssoekerregisteretStatus().split(","))
                        .forEach(status -> {

                            var decodedStatus = decodeMsg(status);
                            if (statusIdents.containsKey(decodedStatus)) {
                                statusIdents.get(decodedStatus).add(progress.getIdent());
                            } else {
                                statusIdents.put(decodedStatus, new HashSet<>(Set.of(progress.getIdent())));
                            }
                        });
            }
        });

        return statusIdents.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(ARBEIDSSOEKERREGISTERET).navn(ARBEIDSSOEKERREGISTERET.getBeskrivelse())
                        .statuser(statusIdents.entrySet().stream()
                                .map(status -> RsStatusRapport.Status.builder()
                                        .melding(status.getKey())
                                        .identer(List.copyOf(status.getValue()))
                                        .build())
                                .toList())
                        .build());
    }
}