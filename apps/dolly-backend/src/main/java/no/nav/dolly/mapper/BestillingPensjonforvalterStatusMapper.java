package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import no.nav.dolly.domain.resultset.SystemTyper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterClient.PENSJON_FORVALTER;
import static no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterClient.POPP_INNTEKTSREGISTER;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_FORVALTER;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_INNTEKT;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingPensjonforvalterStatusMapper {

    public static List<RsStatusRapport> buildPensjonforvalterStatusMap(List<BestillingProgress> progressList) {

        //  melding     status     miljo       ident
        Map<String, Map<String, Map<String, List<String>>>> meldStatusMiljoeIdents = new HashMap();

        progressList.forEach(progress -> {
            if (nonNull(progress.getPensjonforvalterStatus())) {
                List.of(progress.getPensjonforvalterStatus()
                        .split("\\$")).forEach(meldingMiljoStatus -> {
                    String melding = meldingMiljoStatus.split("\\#")[0];
                    List.of(meldingMiljoStatus.split("\\#")[1].split(",")).forEach(miljostatus -> {
                        String[] miljoStatuser = miljostatus.split(":");
                        String miljoe = miljoStatuser.length > 1 ? miljoStatuser[0] : null;
                        if (nonNull(miljoe) && miljoe.length() == 2) {
                            String status = miljoStatuser.length > 1 ? miljoStatuser[1] : miljoStatuser[0];
                            insertArtifact(meldStatusMiljoeIdents, melding, status, miljoe, progress.getIdent());
                        }
                    });
                });
            }
        });

        List<RsStatusRapport> statusRapporter = new ArrayList<>();
        statusRapporter.addAll(extractStatus(meldStatusMiljoeIdents, POPP_INNTEKTSREGISTER, PEN_INNTEKT));
        statusRapporter.addAll(extractStatus(meldStatusMiljoeIdents, PENSJON_FORVALTER, PEN_FORVALTER));

        return statusRapporter;
    }

    private static void insertArtifact(Map<String, Map<String, Map<String, List<String>>>> msgStatusIdents,
            String melding, String status, String miljoe, String ident) {

        if (msgStatusIdents.containsKey(melding)) {
            if (msgStatusIdents.get(melding).containsKey(status)) {
                if (msgStatusIdents.get(melding).get(status).containsKey(miljoe)) {
                    msgStatusIdents.get(melding).get(status).get(miljoe).add(ident);
                } else {
                    msgStatusIdents.get(melding).get(status).put(miljoe, new ArrayList<>(List.of(ident)));
                }
            } else {
                Map<String, List<String>> miljoeIdent = new HashMap<>();
                miljoeIdent.put(miljoe, new ArrayList<>(List.of(ident)));
                msgStatusIdents.get(melding).put(status, miljoeIdent);
            }
        } else {
            Map<String, Map<String, List<String>>> statusMap = new HashMap<>();
            Map<String, List<String>> miljoeIdent = new HashMap();
            miljoeIdent.put(miljoe, new ArrayList<>(List.of(ident)));
            statusMap.put(status, miljoeIdent);
            msgStatusIdents.put(melding, statusMap);
        }
    }

    private static List<RsStatusRapport> extractStatus(Map<String, Map<String, Map<String, List<String>>>> meldStatusMiljoeIdents, String clientid, SystemTyper type) {

        if (meldStatusMiljoeIdents.containsKey(clientid)) {
            return Collections.singletonList(RsStatusRapport.builder()
                    .id(type)
                    .navn(type.getBeskrivelse())
                    .statuser(
                            meldStatusMiljoeIdents.get(clientid).entrySet().stream().map(entry ->
                                    RsStatusRapport.Status.builder()
                                            .melding(entry.getKey().replaceAll("=", ":").replaceAll("&", ","))
                                            .detaljert(entry.getValue().entrySet().stream().map(miljeStatus ->
                                                    RsStatusRapport.Detaljert.builder()
                                                            .miljo(miljeStatus.getKey())
                                                            .identer(miljeStatus.getValue())
                                                            .build())
                                                    .collect(Collectors.toList()))
                                            .build())
                                    .collect(Collectors.toList()))
                    .build());
        } else {
            return Collections.emptyList();
        }
    }
}