package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import org.apache.logging.log4j.util.Strings;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingTpsMessagingStatusMapper {

    public static Map<String, Map<String, Set<String>>>  buildTpsMessagingStatusMap(List<BestillingProgress> progressList) {

        Map<String, Map<String, Set<String>>> statusMap = new HashMap<>();

        var intermediateStatus = progressList.stream()
                .filter(progress -> nonNull(progress.getTpsMessagingStatus()))
                .map(progress ->
                        Stream.of(progress.getTpsMessagingStatus().split("\\$"))
                                .filter(Strings::isNotBlank)
                                .map(melding ->
                                        Stream.of(melding.split("#")[1].split(","))
                                                .filter(status -> !status.contains("OK"))
                                                .map(status -> StatusTemp.builder()
                                                        .ident(progress.getIdent())
                                                        .melding(String.format("%s %s", melding.split("#")[0],
                                                                status.split(":")[1]).replace("=", ":"))
                                                        .miljoe(status.split(":")[0])
                                                        .build())
                                                .toList())
                                .flatMap(Collection::stream)
                                .toList())
                .flatMap(Collection::stream)
                .toList();

        intermediateStatus.forEach(entry -> {
            if (statusMap.containsKey(entry.getMelding())) {
                if (statusMap.get(entry.getMelding()).containsKey(entry.getMiljoe())) {
                    statusMap.get(entry.getMelding()).get(entry.getMiljoe()).add(entry.getIdent());
                } else {
                    statusMap.get(entry.getMelding()).put(entry.getMiljoe(), new HashSet<>(Set.of(entry.getIdent())));
                }
            } else {
                statusMap.put(entry.getMelding(), new HashMap<>(Map.of(entry.getMiljoe(), new HashSet<>(Set.of(entry.getIdent())))));
            }
        });

        return statusMap;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class StatusTemp {

        private String ident;
        private String melding;
        private String miljoe;
    }
}
