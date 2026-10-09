package no.nav.dolly.mapper;

import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;

public class TypeStatusIdenterUtility {

    protected static void insertArtifact(Map<String, Map<String, Set<String>>> typeStatusIdents,
                                       String type, String status, String ident) {

        typeStatusIdents.computeIfAbsent(type, _ -> new HashMap<>())
                .computeIfAbsent(status, _ -> new HashSet<>())
                .add(ident);
    }

    protected static List<RsStatusRapport.Status> extractOKStatus(Map<String, Map<String, Set<String>>> typeStatusIdents) {

        var successfulIdents = getIdenterWithOkForEveryType(typeStatusIdents);

        return successfulIdents.isEmpty()
                ? emptyList()
                : List.of(RsStatusRapport.Status.builder()
                .melding("OK")
                .identer(successfulIdents.stream().toList())
                .build());
    }

    private static Set<String> getIdenterWithOkForEveryType(
            Map<String, Map<String, Set<String>>> typeStatusIdents) {

        var successfulIdents = new HashSet<String>();
        var firstType = true;

        for (var statusIdents : typeStatusIdents.values()) {
            var successfulForType = new HashSet<>(statusIdents.getOrDefault("OK", Set.of()));

            statusIdents.entrySet().stream()
                    .filter(statusEntry -> !"OK".equals(statusEntry.getKey()))
                    .flatMap(statusEntry -> statusEntry.getValue().stream())
                    .forEach(successfulForType::remove);

            if (firstType) {
                successfulIdents.addAll(successfulForType);
                firstType = false;
            } else {
                successfulIdents.retainAll(successfulForType);
            }
        }

        return successfulIdents;
    }

    protected static List<RsStatusRapport.Status> extractErrorStatus(Map<String, Map<String, Set<String>>> typeStatusIdents) {

        return typeStatusIdents.entrySet().stream()
                .map(typeEntry -> typeEntry.getValue().entrySet().stream()
                        .filter(statusEntry -> !"OK".equals(statusEntry.getKey()))
                        .map(statusEntry -> RsStatusRapport.Status.builder()
                                .melding("Feil: %s: %s".formatted(typeEntry.getKey().trim(), statusEntry.getKey()
                                        .replaceAll("Feil.\\s*", "")))
                                .identer(statusEntry.getValue().stream().toList())
                                .build())
                        .toList())
                .flatMap(List::stream)
                .toList();
    }
}
