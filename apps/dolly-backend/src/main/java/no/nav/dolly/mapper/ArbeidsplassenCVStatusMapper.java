package no.nav.dolly.mapper;

import lombok.experimental.UtilityClass;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.SystemTyper.ARBEIDSPLASSENCV;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class ArbeidsplassenCVStatusMapper {

    public static List<RsStatusRapport> buildArbeidsplassenCVStatusMap(List<BestillingProgress> progresser) {

        //  status   ident
        Map<String, List<String>> statusIdents = new HashMap<>();

        progresser.forEach(progress -> {
            if (isNotBlank(progress.getArbeidsplassenCVStatus()) && isNotBlank(progress.getIdent())) {
                if (statusIdents.containsKey(progress.getArbeidsplassenCVStatus())) {
                    statusIdents.get(progress.getArbeidsplassenCVStatus()).add(progress.getIdent());
                } else {
                    statusIdents.put(progress.getArbeidsplassenCVStatus(), new ArrayList<>(List.of(progress.getIdent())));
                }
            }
        });

        return statusIdents.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(ARBEIDSPLASSENCV).navn(ARBEIDSPLASSENCV.getBeskrivelse())
                        .statuser(statusIdents.entrySet().stream()
                                .map(entry -> RsStatusRapport.Status.builder()
                                        .melding(decodeMsg(entry.getKey()).replace("error:", ""))
                                        .identer(entry.getValue())
                                        .build())
                                .toList())
                        .build());
    }
}