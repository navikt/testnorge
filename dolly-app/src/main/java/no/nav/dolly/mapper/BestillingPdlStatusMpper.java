package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsMeldingStatusIdent;

public final class BestillingPdlStatusMpper {

    private BestillingPdlStatusMpper() {
    }

    public static List<RsMeldingStatusIdent> buildPdldataStatusMap(List<BestillingProgress> progressList) {

        //  melding     status      ident
        Map<String, Map<String, List<String>>> msgStatusIdents = new HashMap();

        progressList.forEach(progress -> {
            if (nonNull(progress.getPdlforvalterStatus())) {
                newArrayList(progress.getPdlforvalterStatus().split("\\$")).forEach(
                        message -> {
                            String[] melding = message.split("\\&");
                            if (melding.length > 1) {
                                String[] status = melding[1].split(",");

                                if (msgStatusIdents.containsKey(melding[0])) {
                                    msgStatusIdents.get(melding[0]).get(status[0]).add(progress.getIdent());
                                } else {
                                    Map<String, List<String>> statusIdent = new HashMap();
                                    statusIdent.put(status[0], newArrayList(progress.getIdent()));
                                    msgStatusIdents.put(melding[0], statusIdent);
                                }
                            }
                        }
                );
            }
        });

        return prepareResult(msgStatusIdents);
    }

    private static List<RsMeldingStatusIdent> prepareResult(Map<String, Map<String, List<String>>> msgStatusIdents) {

        List<RsMeldingStatusIdent> result = new ArrayList();
        msgStatusIdents.keySet().forEach(melding ->
                result.add(RsMeldingStatusIdent.builder()
                        .melding(melding)
                        .statusIdent(msgStatusIdents.get(melding))
                        .build()));
        return result;
    }
}