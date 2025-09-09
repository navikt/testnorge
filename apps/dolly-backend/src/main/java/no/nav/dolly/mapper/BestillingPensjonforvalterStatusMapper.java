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

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_AFP_OFFENTLIG;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_ANNET;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_AP;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_AP_REVURDERING;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_FORVALTER;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_INNTEKT;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_PENSJONSAVTALE;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_SAMBOER;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_UT;
import static no.nav.dolly.domain.resultset.SystemTyper.TP_FORVALTER;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingPensjonforvalterStatusMapper {

    private static final String PENSJON_FORVALTER = "PensjonForvalter";
    private static final String POPP_INNTEKTSREGISTER = "PoppInntekt";
    private static final String TP_FORHOLD = "TpForhold";
    private static final String ALDERSPENSJON = "AP";
    private static final String UFORETRYGD = "Ufoer";
    private static final String SAMBOER = "Samboer";
    private static final String PENSJONSAVTALE = "Pensjonsavtale";
    private static final String PEN_AFPOFFENTLIG = "AfpOffentlig";
    private static final String PEN_REVURDERING_AP = "RevurderingAP";
    private static final String ANNET = "Annet";

    public static List<RsStatusRapport> buildPensjonforvalterStatusMap(List<BestillingProgress> progressList) {

        //  melding     status     miljo       ident
        Map<String, Map<String, Map<String, Set<String>>>> meldStatusMiljoeIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getPensjonforvalterStatus()) && progress.getPensjonforvalterStatus().split("#").length > 1) {
                List.of(progress.getPensjonforvalterStatus()
                        .split("\\$")).forEach(meldingMiljoStatus -> {
                    String melding = meldingMiljoStatus.split("#")[0];
                    if (meldingMiljoStatus.split("#").length > 1 && isNotBlank(progress.getIdent())) {
                        List.of(meldingMiljoStatus.split("#")[1].split(",")).forEach(miljostatus -> {
                            String[] miljoStatuser = miljostatus.split(":");
                            String miljoe = miljoStatuser.length > 1 ? miljoStatuser[0] : null;
                            if (nonNull(miljoe)) {
                                String status = miljoStatuser[1];
                                insertArtifact(meldStatusMiljoeIdents, melding, status, miljoe, progress.getIdent());
                            }
                        });
                    }
                });
            }
        });

        var statusRapporter = new ArrayList<RsStatusRapport>();
        statusRapporter.addAll(extractStatus(meldStatusMiljoeIdents, PENSJON_FORVALTER, PEN_FORVALTER));
        statusRapporter.addAll(extractStatus(meldStatusMiljoeIdents, SAMBOER, PEN_SAMBOER));
        statusRapporter.addAll(extractStatus(meldStatusMiljoeIdents, POPP_INNTEKTSREGISTER, PEN_INNTEKT));
        statusRapporter.addAll(extractStatus(meldStatusMiljoeIdents, TP_FORHOLD, TP_FORVALTER));
        statusRapporter.addAll(extractStatus(meldStatusMiljoeIdents, PEN_REVURDERING_AP, PEN_AP_REVURDERING));
        statusRapporter.addAll(extractStatus(meldStatusMiljoeIdents, UFORETRYGD, PEN_UT));
        statusRapporter.addAll(extractStatus(meldStatusMiljoeIdents, ALDERSPENSJON, PEN_AP));
        statusRapporter.addAll(extractStatus(meldStatusMiljoeIdents, PENSJONSAVTALE, PEN_PENSJONSAVTALE));
        statusRapporter.addAll(extractStatus(meldStatusMiljoeIdents, PEN_AFPOFFENTLIG, PEN_AFP_OFFENTLIG));
        statusRapporter.addAll(extractStatus(meldStatusMiljoeIdents, ANNET, PEN_ANNET));

        return statusRapporter;
    }

    private static void insertArtifact(Map<String, Map<String, Map<String, Set<String>>>> msgStatusIdents,
                                       String melding, String status, String miljoe, String ident) {

        if (msgStatusIdents.containsKey(melding)) {
            if (msgStatusIdents.get(melding).containsKey(status)) {
                if (msgStatusIdents.get(melding).get(status).containsKey(miljoe)) {
                    msgStatusIdents.get(melding).get(status).get(miljoe).add(ident);
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

    private static List<RsStatusRapport> extractStatus(Map<String, Map<String, Map<String, Set<String>>>> meldStatusMiljoeIdents, String clientid, SystemTyper type) {

        if (meldStatusMiljoeIdents.containsKey(clientid)) {
            return Collections.singletonList(RsStatusRapport.builder()
                    .id(type)
                    .navn(type.getBeskrivelse())
                    .statuser(
                            meldStatusMiljoeIdents.get(clientid).entrySet().stream().map(entry ->
                                            RsStatusRapport.Status.builder()
                                                    .melding(decodeMsg(entry.getKey()))
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
