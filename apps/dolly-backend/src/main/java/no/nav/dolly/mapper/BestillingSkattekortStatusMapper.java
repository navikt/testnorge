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
import static no.nav.dolly.domain.resultset.SystemTyper.SKATTEKORT;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingSkattekortStatusMapper {

    public static List<RsStatusRapport> buildSkattekortStatusMap(List<BestillingProgress> progressList) {

        //  status     org+year       ident
        Map<String, Map<String, Set<String>>> errorEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getSkattekortStatus()) && isNotBlank(progress.getIdent())) {
                var entries = progress.getSkattekortStatus().split(",");
                for (var entry : entries) {
                    var parts = entry.split(":");
                    if (parts.length < 2) {
                        continue;
                    }
                    var orgYear = parts[0];
                    var status = parts[1];
                    if (errorEnvIdents.containsKey(status)) {
                        if (errorEnvIdents.get(status).containsKey(orgYear)) {
                            errorEnvIdents.get(status).get(orgYear).add(progress.getIdent());
                        } else {
                            errorEnvIdents.get(status).put(orgYear, new HashSet<>(Set.of(progress.getIdent())));
                        }
                    } else {
                        errorEnvIdents.put(status, new HashMap<>(Map.of(orgYear, new HashSet<>(Set.of(progress.getIdent())))));
                    }
                }
            }
        });

        if (errorEnvIdents.isEmpty()) {
            return emptyList();

        } else {
            if (errorEnvIdents.entrySet().stream()
                    .allMatch(entry -> entry.getKey().equals("Skattekort lagret"))) {

                return errorEnvIdents.values().stream()
                        .map(entry -> RsStatusRapport.builder()
                                .id(SKATTEKORT)
                                .navn(SKATTEKORT.getBeskrivelse())
                                .statuser(List.of(RsStatusRapport.Status.builder()
                                        .melding("OK")
                                        .identer(entry.values().stream()
                                                .map(orgYear -> orgYear.stream().toList())
                                                .flatMap(Collection::stream)
                                                .distinct()
                                                .toList())
                                        .build()))
                                .build())
                        .toList();

            } else {

                return errorEnvIdents.entrySet().stream()
                        .filter(entry -> !entry.getKey().equals("Skattekort lagret"))
                        .map(entry -> RsStatusRapport.builder()
                                .id(SKATTEKORT)
                                .navn(SKATTEKORT.getBeskrivelse())
                                .statuser(entry.getValue().entrySet().stream()
                                        .map(orgYear -> RsStatusRapport.Status.builder()
                                                .melding(formatMelding(orgYear.getKey(), entry.getKey()))
                                                .identer(orgYear.getValue().stream().toList())
                                                .build())
                                        .toList())
                                .build())
                        .toList();
            }
        }
    }

    private static String formatMelding(String orgYear, String melding) {

        String[] parts = orgYear.split("\\+");
        if (parts.length < 2) {
            return "FEIL: %s".formatted(decodeMsg(melding));
        }
        
        return "FEIL: organisasjon:%s, inntektsår:%s, melding:%s".formatted(
                parts[0],
                parts[1],
                decodeMsg(melding));
    }
}
