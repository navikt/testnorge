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
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingSkattekortStatusMapper {

    public static List<RsStatusRapport> buildSkattekortStatusMap(List<BestillingProgress> progressList) {

        //  status     org+year       ident
        Map<String, Map<String, Set<String>>> errorEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getSkattekortStatus()) && isNotBlank(progress.getIdent())) {
                var element = progress.getSkattekortStatus().split(",");
                for (String s : element) {
                    var orgYear = s.split(":")[0];
                    var status = s.split(":")[1];
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

//        return errorEnvIdents.isEmpty() ? emptyList() :
//                errorEnvIdents.entrySet().stream()
//
//
//                singletonList(RsStatusRapport.builder().id(SKATTEKORT).navn(SKATTEKORT.getBeskrivelse())
//                        .statuser(statusMap.entrySet().stream()
//                                .map(entry -> RsStatusRapport.Status.builder()
//                                        .melding(getStatus(decodeMsg(entry.getKey())))
//                                        .identer(entry.getValue())
//                                        .build())
//                                .toList())
//                        .build());
        return emptyList();
    }

    private static String getStatus(String melding) {

        return switch (melding) {
            case "Skattekort lagret" -> "OK";
            case null -> "FEIL: ingen status mottatt";
            default -> "FEIL: " + melding;
        };
    }
}
