package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;
import static no.nav.dolly.domain.resultset.SystemTyper.SKATTEKORT;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingSkattekortStatusMapper {

    private static final String OK_STATUS = "OK";

    public static List<RsStatusRapport> buildSkattekortStatusMap(List<BestillingProgress> progressList) {

        Map<String, Set<String>> statusIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getSkattekortStatus()) && isNotBlank(progress.getIdent())) {
                var entries = progress.getSkattekortStatus().split(",");
                for (var entry : entries) {
                    var status = entry.trim();
                    statusIdents.computeIfAbsent(status, k -> new HashSet<>()).add(progress.getIdent());
                }
            }
        });

        if (statusIdents.isEmpty()) {
            return emptyList();
        }

        List<RsStatusRapport.Status> statuses = new ArrayList<>();

        Set<String> okIdents = statusIdents.remove(OK_STATUS);
        if (okIdents != null && !okIdents.isEmpty()) {
            statuses.add(RsStatusRapport.Status.builder()
                    .melding(OK_STATUS)
                    .identer(new ArrayList<>(okIdents))
                    .build());
        }

        statusIdents.forEach((status, idents) ->
                statuses.add(RsStatusRapport.Status.builder()
                        .melding(decodeMsg(status))
                        .identer(new ArrayList<>(idents))
                        .build()));

        if (statuses.isEmpty()) {
            return emptyList();
        }

        return List.of(RsStatusRapport.builder()
                .id(SKATTEKORT)
                .navn(SKATTEKORT.getBeskrivelse())
                .statuser(statuses)
                .build());
    }
}
