package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static no.nav.dolly.domain.resultset.SystemTyper.TPS_MESSAGING;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingTpsMessagingStatusMapper {

    private static final List<String> HIDE_MILJOER = List.of("qx");
    private static final String OKEY = "OK";
    private static final String ADVARSEL = "Advarsel: ";
    private static final String FEIL = "Feil: ";

    public static List<RsStatusRapport> buildTpsMessagingStatusMap(List<BestillingProgress> progressList) {

        try {
            //melding     miljø     identer
            Map<String, Map<String, Set<String>>> statusMap = new HashMap<>();

            var intermediateStatus = progressList.stream()
                    .filter(progress -> isNotBlank(progress.getTpsMessagingStatus()))
                    .filter(progress -> isNotBlank(progress.getIdent()))
                    .map(progress ->
                            Stream.of(progress.getTpsMessagingStatus().split("\\$"))
                                    .filter(Strings::isNotBlank)
                                    .filter(melding -> !melding.contains("Telefonnummer_slett"))
                                    .filter(melding -> !melding.contains("Sikkerhetstiltak_slett"))
                                    .filter(melding -> melding.split("#").length > 1)
                                    .map(melding ->
                                            Stream.of(melding.split("#")[1].split(","))
                                                    .filter(status -> status.contains(":"))
                                                    .filter(BestillingTpsMessagingStatusMapper::containsValidMiljoe)
                                                    .map(status -> StatusTemp.builder()
                                                            .ident(progress.getIdent())
                                                            .melding(getStatus(String.format("%s %s", melding.split("#")[0],
                                                                    status.split(":")[1]).replace("=", ":")))
                                                            .miljoe(status.split(":")[0])
                                                            .build())
                                                    .toList())
                                    .flatMap(Collection::stream)
                                    .toList())
                    .flatMap(Collection::stream)
                    .toList();

            intermediateStatus.stream()
                    .filter(status -> !OKEY.equals(status.getMelding()) || intermediateStatus.stream()
                            .noneMatch(status2 -> !OKEY.equals(status2.getMelding()) &&
                                    status.getMiljoe().equals(status2.getMiljoe()) &&
                                    status.getIdent().equals(status2.getIdent())))
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
                                    .melding(formatMsg(status.getKey()))
                                    .detaljert(status.getValue().entrySet().stream()
                                            .map(miljoIdenter -> RsStatusRapport.Detaljert.builder()
                                                    .miljo(miljoIdenter.getKey())
                                                    .identer(miljoIdenter.getValue())
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

    private static String formatMsg(String message) {

        if (StringUtils.containsIgnoreCase(message, ADVARSEL)) {
            return decodeMsg(ADVARSEL + message.replace(" ADVARSEL", "")
                    .replace("_", " "));
        }
        if (StringUtils.containsIgnoreCase(message, FEIL)) {
            return decodeMsg(FEIL + message.replace(" FEIL", "")
                    .replace("_", " "));
        }
        if (StringUtils.containsIgnoreCase(message, "Startet ")) {
            return decodeMsg(message.replace("Startet ", ""));
        }
        return message;
    }

    private static String getStatus(String status) {

        if (status.toLowerCase().contains("feil: ingen svarstatus mottatt fra tps")) {
            return status.replaceFirst("FEIL: .*", "") + "ADVARSEL: Status ukjent (tidsavbrudd)";

        } else {
            return status.contains(OKEY) ||
                    status.toLowerCase().contains("person ikke funnet i tps") ||
                    status.toLowerCase().contains("dette er data som allerede er registrert i tps") ||
                    status.toLowerCase().contains("utgått fødselsnr") ||
                    status.toLowerCase().contains("mottaker svarer ikke") && !status.toUpperCase().contains("MELDINGER TIL TPS") ||
                    status.toLowerCase().contains("forbindelsen er ustabil")
                    ? OKEY
                    : status;
        }
    }

    private static boolean containsValidMiljoe(String status) {
        return !HIDE_MILJOER.contains(status.split(":")[0]);
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
