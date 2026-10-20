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

import static no.nav.dolly.domain.resultset.SystemTyper.SIGRUN_PENSJONSGIVENDE;
import static no.nav.dolly.domain.resultset.SystemTyper.SIGRUN_SUMMERT;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingSigrunStubStatusMapper {

    private static final String PENSJONSGIVENDE_INNTEKT = "SIGRUN_PENSJONSGIVENDE";
    private static final String SUMMERT_SKATTEGRUNNLAG = "SIGRUN_SUMMERT";

    public static List<RsStatusRapport> buildSigrunStubStatusMap(List<BestillingProgress> progressList) {

        //melding   //status    //ident
        Map<String, Map<String, List<String>>> statusMap = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getSigrunstubStatus())) {
                List.of(progress.getSigrunstubStatus().split(","))
                        .forEach(entry -> insertArtifact(entry, progress.getIdent(), statusMap));
            }
        });

        var statusRapporter = new ArrayList<RsStatusRapport>();
        statusRapporter.addAll(extractStatus(statusMap, PENSJONSGIVENDE_INNTEKT, SIGRUN_PENSJONSGIVENDE));
        statusRapporter.addAll(extractStatus(statusMap, SUMMERT_SKATTEGRUNNLAG, SIGRUN_SUMMERT));

        return statusRapporter;
    }

    private static void insertArtifact(String entry, String ident, Map<String, Map<String, List<String>>> msgStatusIdents) {

        var meldingStatus = entry.split(":");
        var melding = meldingStatus.length > 1 ? meldingStatus[0] : null;
        if (isNotBlank(melding)) {

            var status = meldingStatus[1];
            if (msgStatusIdents.containsKey(melding)) {
                if (msgStatusIdents.get(melding).containsKey(status)) {
                    msgStatusIdents.get(melding).get(status).add((ident));
                } else {
                    msgStatusIdents.get(melding).put(status, new ArrayList<>(List.of(ident)));
                }
            } else {
                var statusMap = new HashMap<String, List<String>>();
                statusMap.put(status, new ArrayList<>(List.of(ident)));
                msgStatusIdents.put(melding, statusMap);
            }
        }
    }

    private static List<RsStatusRapport> extractStatus(Map<String, Map<String, List<String>>> meldStatusIdents, String clientId, SystemTyper type) {

        return meldStatusIdents.containsKey(clientId) ?
                Collections.singletonList(RsStatusRapport.builder()
                        .id(type)
                        .navn(type.getBeskrivelse())
                        .statuser(
                                meldStatusIdents.get(clientId).entrySet().stream()
                                        .map(entry ->
                                                RsStatusRapport.Status.builder()
                                                        .melding(entry.getKey())
                                                        .identer(entry.getValue())
                                                        .build())
                                        .toList())
                        .build()) :
                Collections.emptyList();
    }
}
