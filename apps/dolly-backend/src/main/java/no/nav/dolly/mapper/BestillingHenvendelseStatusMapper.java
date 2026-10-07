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
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static no.nav.dolly.domain.resultset.SystemTyper.HENVENDELSE;
import static no.nav.dolly.mapper.StatusMiljoeIdentForholdUtility.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingHenvendelseStatusMapper {

    public static List<RsStatusRapport> buildHenvendelseStatusMap(List<BestillingProgress> progressList) {

        // type    // status   // ident
        Map<String, Map<String, Set<String>>> typeStatusIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getHenvendelseStatus())) {
                List.of(progress.getHenvendelseStatus().split(",")).forEach(
                        entry -> {
                            if (isNotBlank(entry)) {
                                var typeStatus = entry.split(":");
                                var type = typeStatus[0];
                                var status = decodeMsg(typeStatus.length > 1 ? typeStatus[typeStatus.length - 1] : "");
                                insertArtifact(typeStatusIdents, type, status, progress.getIdent());
                            }
                        });
            }
        });

        if (typeStatusIdents.isEmpty()) {
            return emptyList();

        } else {
            return List.of(RsStatusRapport.builder()
                    .id(HENVENDELSE)
                    .navn(HENVENDELSE.getBeskrivelse())
                    .statuser(Stream.of(extractOKStatus(typeStatusIdents),
                                    extractErrorStatus(typeStatusIdents))
                            .flatMap(List::stream)
                            .toList())
                    .build());
        }
    }

    private static void insertArtifact(Map<String, Map<String, Set<String>>> typeStatusIdents,
                                       String type, String status, String ident) {

        typeStatusIdents.computeIfAbsent(type, _ -> new HashMap<>())
                .computeIfAbsent(status, _ -> new HashSet<>())
                .add(ident);
    }

    private static List<RsStatusRapport.Status> extractOKStatus(Map<String, Map<String, Set<String>>> typeStatusIdents) {

        if (typeStatusIdents.entrySet().stream()
                .allMatch(typeEntry -> typeEntry.getValue().entrySet().stream()
                        .allMatch(statusEntry -> "OK".equals(statusEntry.getKey())))) {

            return List.of(RsStatusRapport.Status.builder()
                    .melding("OK")
                    .identer(typeStatusIdents.values().stream()
                            .flatMap(typeEntry -> typeEntry.entrySet().stream())
                            .flatMap(statusEntry -> statusEntry.getValue().stream())
                            .distinct()
                            .toList())
                    .build());

        } else {
            return emptyList();
        }
    }

    private static List<RsStatusRapport.Status> extractErrorStatus(Map<String, Map<String, Set<String>>> typeStatusIdents) {

        return typeStatusIdents.entrySet().stream()
                .map(typeEntry -> typeEntry.getValue().entrySet().stream()
                        .filter(statusEntry -> !"OK".equals(statusEntry.getKey()))
                        .map(statusEntry -> RsStatusRapport.Status.builder()
                                .melding("Feil: %s: %s".formatted(typeEntry.getKey(), statusEntry.getKey()))
                                .identer(statusEntry.getValue().stream().toList())
                                .build())
                        .toList())
                .flatMap(List::stream)
                .toList();
    }
}