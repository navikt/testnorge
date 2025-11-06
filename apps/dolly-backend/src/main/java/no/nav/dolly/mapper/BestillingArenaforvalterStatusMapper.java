package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import no.nav.dolly.domain.resultset.SystemTyper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static no.nav.dolly.bestilling.arenaforvalter.ArenaUtils.fixFormatUserDefinedError;
import static no.nav.dolly.domain.resultset.SystemTyper.ARENA;
import static no.nav.dolly.domain.resultset.SystemTyper.ARENA_AAP;
import static no.nav.dolly.domain.resultset.SystemTyper.ARENA_AAP115;
import static no.nav.dolly.domain.resultset.SystemTyper.ARENA_BRUKER;
import static no.nav.dolly.domain.resultset.SystemTyper.ARENA_DAGP;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingArenaforvalterStatusMapper {

    private static final String BRUKER = "BRUKER";
    private static final String AAP115 = "AAP115";
    private static final String AAP = "AAP";
    private static final String DAGPENGER = "DAGP";
    private static final String ARENA_FAGSYSTEM = "ARENA";

    public static List<RsStatusRapport> buildArenaStatusMap(List<BestillingProgress> progressList) {

        //melding    // status    environment    ident
        Map<String, Map<String, Map<String, Set<String>>>> meldStatusMiljoeIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getArenaforvalterStatus())) {
                List.of(progress.getArenaforvalterStatus().split(",")).forEach(
                        entry -> {
                            if (isNotBlank(entry)) {
                                var envStatus = entry.split("\\$");
                                var environment = envStatus[0];
                                var status = decodeMsg(envStatus.length > 1 ? envStatus[envStatus.length - 1] : "");
                                insertArtifact(meldStatusMiljoeIdents, status, environment, progress.getIdent());
                            }
                        });
            }
        });

        List<RsStatusRapport> statusRapporter = new ArrayList<>();
        statusRapporter.addAll(extractStatus(meldStatusMiljoeIdents, BRUKER, ARENA_BRUKER));
        statusRapporter.addAll(extractStatus(meldStatusMiljoeIdents, AAP115, ARENA_AAP115));
        statusRapporter.addAll(extractStatus(meldStatusMiljoeIdents, AAP, ARENA_AAP));
        statusRapporter.addAll(extractStatus(meldStatusMiljoeIdents, DAGPENGER, ARENA_DAGP));
        statusRapporter.addAll(extractStatus(meldStatusMiljoeIdents, ARENA_FAGSYSTEM, ARENA));

        return statusRapporter;
    }

    private static void insertArtifact(Map<String, Map<String, Map<String, Set<String>>>> msgStatusIdents,
                                       String status, String miljoe, String ident) {

        String melding = getMelding(status);

        if (status.contains("OK")) {
            status = "OK";
        }

        if (msgStatusIdents.containsKey(melding)) {
            if (msgStatusIdents.get(melding).containsKey(status)) {
                if (msgStatusIdents.get(melding).get(status).containsKey(miljoe)) {
                    if (!msgStatusIdents.get(melding).get(status).get(miljoe).contains(ident)) {
                        msgStatusIdents.get(melding).get(status).get(miljoe).add(ident);
                    }
                } else {
                    msgStatusIdents.get(melding).get(status).put(miljoe, new HashSet<>(Set.of(ident)));
                }
            } else {
                Map<String, Set<String>> miljoeIdent = new HashMap<>();
                miljoeIdent.put(miljoe, new HashSet<>(Set.of(ident)));
                msgStatusIdents.get(melding).put(status, miljoeIdent);
            }
        } else {
            Map<String, Map<String, Set<String>>> statusMap = new HashMap<>();
            Map<String, Set<String>> miljoeIdent = new HashMap<>();
            miljoeIdent.put(miljoe, new HashSet<>(Set.of(ident)));
            statusMap.put(status, miljoeIdent);
            msgStatusIdents.put(melding, statusMap);
        }
    }

    private static String getMelding(String status) {

        if (status.contains(BRUKER)) {
            return BRUKER;
        } else if (status.contains(AAP115)) { // må komme før sjekk på aap
            return AAP115;
        } else if (status.contains(AAP)) {
            return AAP;
        } else if (status.contains(DAGPENGER)) {
            return DAGPENGER;
        } else {
            return ARENA_FAGSYSTEM;
        }
    }

    private static List<RsStatusRapport> extractStatus(Map<String, Map<String, Map<String, Set<String>>>> meldStatusMiljoeIdents, String clientid, SystemTyper type) {

        if (meldStatusMiljoeIdents.containsKey(clientid)) {
            return Collections.singletonList(RsStatusRapport.builder()
                    .id(type)
                    .navn(type.getBeskrivelse())
                    .statuser(
                            meldStatusMiljoeIdents.get(clientid).entrySet().stream().map(entry ->
                                            RsStatusRapport.Status.builder()
                                                    .melding(fixFormatUserDefinedError(decodeMsg(entry.getKey()))
                                                            .replace("BRUKER: Info:", "Info:")
                                                            .replace("BRUKER Avvik:", "Avvik:"))
                                                    .detaljert(entry.getValue().entrySet().stream().map(miljeStatus ->
                                                                    RsStatusRapport.Detaljert.builder()
                                                                            .miljo(miljeStatus.getKey())
                                                                            .identer(miljeStatus.getValue())
                                                                            .build())
                                                            .toList())
                                                    .build())
                                    .toList())
                    .build());
        } else {
            return Collections.emptyList();
        }
    }
}