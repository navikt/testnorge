package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.TPS_MESSAGING;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingTpsMessagingStatusMapper {

    private static final String OKEY = "OK";

    public static List<RsStatusRapport> buildTpsMessagingStatusMap(List<BestillingProgress> progressList) {

        try {
            //melding     miljø     identer
            Map<String, Map<String, Set<String>>> statusMap = new HashMap<>();

            var intermediateStatus = progressList.stream()
                    .filter(progress -> nonNull(progress.getTpsMessagingStatus()))
                    .map(progress ->
                            Stream.of(progress.getTpsMessagingStatus().split("\\$"))
                                    .filter(Strings::isNotBlank)
                                    .filter(melding -> !melding.equals("Telefonnummer_slett#"))
                                    .filter(melding -> !melding.equals("Sikkerhetstiltak_slett#"))
                                    .map(melding ->
                                            Stream.of(melding.split("#")[1].split(","))
                                                    .filter(status -> !status.toLowerCase()
                                                            .contains("person ikke funnet i tps"))
                                                    .map(status -> StatusTemp.builder()
                                                            .ident(progress.getIdent())
                                                            .melding(cleanOK(String.format("%s %s", melding.split("#")[0],
                                                                    status.split(":")[1]).replace("=", ":")))
                                                            .miljoe(status.split(":")[0])
                                                            .build())
                                                    .toList())
                                    .flatMap(Collection::stream)
                                    .toList())
                    .flatMap(Collection::stream)
                    .toList();

            intermediateStatus.stream()
                    .filter(status -> OKEY.equals(status.getMelding()) && intermediateStatus.stream()
                            .noneMatch(status2 -> !OKEY.equals(status2.getMelding()) &&
                                    status.getMiljoe().equals(status2.getMiljoe()) &&
                                    status.getIdent().equals(status2.getIdent())) ||
                            !OKEY.equals(status.getMelding()))
                    .forEach(entry -> {
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

            return !statusMap.isEmpty() ? List.of(RsStatusRapport.builder()
                    .id(TPS_MESSAGING)
                    .navn(TPS_MESSAGING.getBeskrivelse())
                    .statuser(statusMap.entrySet().stream()
                            .map(status -> RsStatusRapport.Status.builder()
                                    .melding(status.getKey())
                                    .detaljert(status.getValue().entrySet().stream()
                                            .map(miljoIdenter -> RsStatusRapport.Detaljert.builder()
                                                    .miljo(miljoIdenter.getKey())
                                                    .identer(new ArrayList<>(miljoIdenter.getValue()))
                                                    .build())
                                            .toList())
                                    .build())
                            .toList())
                    .build()) :
                    emptyList();

        } catch (RuntimeException e) {

            log.error(e.getMessage(), e);
            return emptyList();
        }
    }

    private static String cleanOK(String status) {

        return status.contains(OKEY) ? OKEY : status;
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
