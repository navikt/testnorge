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

import static no.nav.dolly.domain.resultset.SystemTyper.HENVENDELSE;
import static no.nav.dolly.mapper.StatusMiljoeIdentForholdUtility.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingHenvendelseStatusMapper {

    public static List<RsStatusRapport> buildHenvendelseStatusMap(List<BestillingProgress> progressList) {

        // type    // status   // ident
        Map<String, Map<String, Set<String>>> meldStatusIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getHenvendelseStatus())) {
                List.of(progress.getHenvendelseStatus().split(",")).forEach(
                        entry -> {
                            if (isNotBlank(entry)) {
                                var typeStatus = entry.split(":");
                                var type = typeStatus[0];
                                var status = decodeMsg(typeStatus.length > 1 ? typeStatus[typeStatus.length - 1] : "");
                                insertArtifact(meldStatusIdents, type, status, progress.getIdent());
                            }
                        });
            }
        });

        List<RsStatusRapport.Status> statusRapporter = new ArrayList<>();
        statusRapporter.addAll(extractOKStatus(meldStatusIdents));
        statusRapporter.addAll(extractErrorStatus(meldStatusIdents));

        return List.of(RsStatusRapport.builder()
                .id(HENVENDELSE)
                .navn(HENVENDELSE.getBeskrivelse())
                .statuser(statusRapporter)
                .build());
    }

    private static void insertArtifact(Map<String, Map<String, Set<String>>> msgStatusIdents,
                                       String type, String status, String ident) {

        msgStatusIdents.computeIfAbsent(type, _ -> new HashMap<>())
                .computeIfAbsent(status, _ -> new HashSet<>())
                .add(ident);
    }

    private static List<RsStatusRapport.Status> extractOKStatus(Map<String, Map<String, Set<String>>> typeStatusIdents) {

        return typeStatusIdents.entrySet().stream()
                .map(typeEntry -> typeEntry.getValue().entrySet().stream()
                        .filter(statusEntry -> "OK".equals(statusEntry.getKey()))
                        .map(statusEntry -> RsStatusRapport.Status.builder()
                                .melding("OK")
                                .identer(statusEntry.getValue().stream().toList())
                                .build())
                        .toList())
                .flatMap(List::stream)
                .toList();
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