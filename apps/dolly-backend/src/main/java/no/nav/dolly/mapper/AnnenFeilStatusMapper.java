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
import static no.nav.dolly.domain.resultset.SystemTyper.ANNEN_FEIL;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class AnnenFeilStatusMapper {

    public static List<RsStatusRapport> buildAnnenFeilStatusMap(List<BestillingProgress> progresser) {

        //  status   ident
        Map<String, List<String>> statusIdents = new HashMap<>();

        progresser.forEach(progress -> {
            if (isNotBlank(progress.getFeil()) && isNotBlank(progress.getIdent())) {
                if (statusIdents.containsKey(progress.getFeil())) {
                    statusIdents.get(progress.getFeil()).add(progress.getIdent());
                } else {
                    statusIdents.put(progress.getFeil(), new ArrayList<>(List.of(progress.getIdent())));
                }
            }
        });

        return statusIdents.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(ANNEN_FEIL).navn(ANNEN_FEIL.getBeskrivelse())
                        .statuser(statusIdents.entrySet().stream()
                                .map(entry -> RsStatusRapport.Status.builder()
                                        .melding(decodeMsg(entry.getKey()))
                                        .identer(entry.getValue())
                                        .build())
                                .toList())
                        .build());
    }
}