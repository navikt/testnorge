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
import static no.nav.dolly.domain.resultset.SystemTyper.SKATTEKORT;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingSkattekortStatusMapper {

    public static List<RsStatusRapport> buildSkattekortStatusMap(List<BestillingProgress> progressList) {

        Map<String, Set<String>> statusIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getSkattekortStatus()) && isNotBlank(progress.getIdent())) {
                var entries = progress.getSkattekortStatus().split(",");
                for (var entry : entries) {
                    
                    if (!entry.contains("|")) {
                        if (statusIdents.containsKey(entry)) {
                            statusIdents.get(entry).add(progress.getIdent());
                        } else {
                            statusIdents.put(entry, new HashSet<>(Set.of(progress.getIdent())));
                        }
                        continue;
                    }
                    
                    var parts = entry.split("\\|");
                    if (parts.length < 2) {
                        continue;
                    }
                    var orgYear = parts[0];
                    var status = parts[1];
                    
                    var fullStatus = "org:" + orgYear + ", status:" + status;
                    
                    if (statusIdents.containsKey(fullStatus)) {
                        statusIdents.get(fullStatus).add(progress.getIdent());
                    } else {
                        statusIdents.put(fullStatus, new HashSet<>(Set.of(progress.getIdent())));
                    }
                }
            }
        });

        if (statusIdents.isEmpty()) {
            return emptyList();
        }

        var statuser = statusIdents.entrySet().stream()
                .map(entry -> {
                    String melding = entry.getKey();
                    
                    if (melding.startsWith("org:")) {
                        var statusPart = melding.substring(melding.indexOf("status:") + 7);
                        if (statusPart.equals("Skattekort lagret")) {
                            melding = "OK";
                        } else {
                            var orgPart = melding.substring(4, melding.indexOf(", status:"));
                            var parts = orgPart.split("\\+");
                            if (parts.length >= 2) {
                                melding = "FEIL: organisasjon:" + parts[0] + ", inntektsår:" + parts[1] + ", melding:" + decodeMsg(statusPart);
                            } else {
                                melding = "FEIL: " + decodeMsg(statusPart);
                            }
                        }
                    } else {
                        melding = decodeMsg(melding);
                    }
                    
                    return RsStatusRapport.Status.builder()
                            .melding(melding)
                            .identer(entry.getValue().stream().toList())
                            .build();
                })
                .toList();

        return List.of(RsStatusRapport.builder()
                .id(SKATTEKORT)
                .navn(SKATTEKORT.getBeskrivelse())
                .statuser(statuser)
                .build());
    }
}
