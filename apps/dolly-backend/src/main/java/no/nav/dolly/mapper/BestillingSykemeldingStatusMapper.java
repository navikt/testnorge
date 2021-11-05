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
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.SYKEMELDING;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BestillingSykemeldingStatusMapper {

    public static List<RsStatusRapport> buildSykemeldingStatusMap(List<BestillingProgress> progressList) {

        Map<String, List<String>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getSykemeldingStatus())) {
                if (statusMap.containsKey(progress.getSykemeldingStatus())) {
                    statusMap.get(progress.getSykemeldingStatus()).add(progress.getIdent());
                } else {
                    statusMap.put(progress.getSykemeldingStatus(), new ArrayList<>(List.of(progress.getIdent())));
                }
            }
        });

        return statusMap.isEmpty() ? emptyList()
                : singletonList(RsStatusRapport.builder().id(SYKEMELDING).navn(SYKEMELDING.getBeskrivelse())
                .statuser(statusMap.entrySet().stream()
                        .map(entry -> RsStatusRapport.Status.builder()
                                .melding(entry.getKey().replaceAll("=", ":"))
                                .identer(entry.getValue())
                                .build())
                        .collect(Collectors.toList()))
                .build());
    }
}
