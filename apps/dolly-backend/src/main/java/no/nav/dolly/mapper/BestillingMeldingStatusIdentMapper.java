package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.arenaforvalter.RsMeldingStatusIdent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static no.nav.dolly.util.ListUtil.listOf;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingMeldingStatusIdentMapper {

    public static Consumer<String> resolveStatus(Map<String, Map<String, List<String>>> msgStatusIdents, BestillingProgress progress) {

        return message -> {
            String[] melding = message.split("\\&");
            if (melding.length > 1) {
                String[] status = melding[1].split(",");

                if (msgStatusIdents.containsKey(melding[0])) {
                    appendStatusIdent(msgStatusIdents.get(melding[0]), progress, status[0]);
                } else {
                    Map<String, List<String>> statusIdent = new HashMap<>();
                    statusIdent.put(status[0], listOf(progress.getIdent()));
                    msgStatusIdents.put(melding[0], statusIdent);
                }
            }
        };
    }

    public static List<RsMeldingStatusIdent> prepareResult(Map<String, Map<String, List<String>>> msgStatusIdents) {

        List<RsMeldingStatusIdent> result = new ArrayList<>();
        msgStatusIdents.keySet().forEach(melding ->
                result.add(RsMeldingStatusIdent.builder()
                        .status(melding)
                        .envIdent(msgStatusIdents.get(melding))
                        .build()));
        return result;
    }

    private static void appendStatusIdent(Map<String, List<String>> stringListMap, BestillingProgress progress, String status) {

        if (stringListMap.containsKey(status)) {
            stringListMap.get(status).add(progress.getIdent());
        } else {
            stringListMap.put(status, listOf(progress.getIdent()));
        }
    }
}
