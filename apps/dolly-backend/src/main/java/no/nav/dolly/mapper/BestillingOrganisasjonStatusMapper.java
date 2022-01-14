package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.resultset.RsOrganisasjonStatusRapport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static no.nav.dolly.domain.resultset.SystemTyper.ORGANISASJON_FORVALTER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BestillingOrganisasjonStatusMapper {

    public static List<RsOrganisasjonStatusRapport> buildOrganisasjonStatusMap(OrganisasjonBestillingProgress progress, String detaljertStatus) {

        if (isNull(progress) || isNull(progress.getOrganisasjonsforvalterStatus())) {
            return emptyList();
        }

        Map<String, List<String>> statusMap = new HashMap<>();

        List.of(progress.getOrganisasjonsforvalterStatus()
                .replace(",q", "$q")
                .replace(",t", "$t")
                .replace(",u", "$u")
                .split("\\$")).forEach(status -> {
            String[] environMsg = status.split(":", 2);
            if (environMsg.length < 2) {
                return;
            }
            String message = environMsg[1]
                    .replace("ERROR", "FEIL")
                    .replace("FEIL-Tidsavbrudd", "INFO-Tidsavbrudd");
            if (statusMap.containsKey(message)) {
                List<String> envStatus = new ArrayList<>(statusMap.get(message));
                envStatus.add(environMsg[0]);
                statusMap.replace(message, envStatus);
            } else {
                statusMap.put(message, List.of(environMsg[0]));
            }
        });

        return statusMap.isEmpty() ? emptyList()
                : singletonList(RsOrganisasjonStatusRapport.builder().id(ORGANISASJON_FORVALTER).navn(ORGANISASJON_FORVALTER.getBeskrivelse())
                .statuser(statusMap.entrySet().stream()
                        .map(entry -> RsOrganisasjonStatusRapport.Status.builder()
                                .melding(entry.getKey())
                                .detaljert(entry.getValue().stream().map(value -> RsOrganisasjonStatusRapport.Detaljert.builder()
                                        .miljo(value)
                                        .orgnummer(progress.getOrganisasjonsnummer())
                                        .detaljertStatus(detaljertStatus)
                                        .build()).collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .build());
    }
}
