package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;
import static no.nav.dolly.domain.resultset.SystemTyper.YRKESSKADE;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingYrkesskadeStatusMapper {

    public static List<RsStatusRapport> buildYrkesskadeStatusMap(List<BestillingProgress> progressList) {

        //  status     org+year       ident
        Map<String, Map<String, Set<String>>> errorEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getYrkesskadeStatus()) && isNotBlank(progress.getIdent())) {
                var entries = progress.getYrkesskadeStatus().split(",");
                for (var entry : entries) {
                    var parts = entry.split(":");
                    if (parts.length < 2) {
                        continue;
                    }
                    var yrkesskade = parts[0];
                    var status = parts[1];
                    if (errorEnvIdents.containsKey(status)) {
                        if (errorEnvIdents.get(status).containsKey(yrkesskade)) {
                            errorEnvIdents.get(status).get(yrkesskade).add(progress.getIdent());
                        } else {
                            errorEnvIdents.get(status).put(yrkesskade, new HashSet<>(Set.of(progress.getIdent())));
                        }
                    } else {
                        errorEnvIdents.put(status, new HashMap<>(Map.of(yrkesskade, new HashSet<>(Set.of(progress.getIdent())))));
                    }
                }
            }
        });

        if (errorEnvIdents.isEmpty()) {
            return emptyList();

        } else {
            if (errorEnvIdents.entrySet().stream()
                    .allMatch(entry -> entry.getKey().equals("OK"))) {

                return errorEnvIdents.values().stream()
                        .map(entry -> RsStatusRapport.builder()
                                .id(YRKESSKADE)
                                .navn(YRKESSKADE.getBeskrivelse())
                                .statuser(List.of(RsStatusRapport.Status.builder()
                                        .melding("OK")
                                        .identer(entry.values().stream()
                                                .map(skade -> skade.stream().toList())
                                                .flatMap(Collection::stream)
                                                .distinct()
                                                .toList())
                                        .build()))
                                .build())
                        .toList();

            } else {

                return errorEnvIdents.entrySet().stream()
                        .filter(entry -> !entry.getKey().equals("OK"))
                        .map(entry -> RsStatusRapport.builder()
                                .id(YRKESSKADE)
                                .navn(YRKESSKADE.getBeskrivelse())
                                .statuser(entry.getValue().values().stream()
                                        .map(strings -> RsStatusRapport.Status.builder()
                                                .melding(formatMelding(entry.getKey()))
                                                .identer(strings.stream().toList())
                                                .build())
                                        .toList())
                                .build())
                        .toList();
            }
        }
    }

    private static String formatMelding(String value) {

        return decodeMsg(value);
    }
}
