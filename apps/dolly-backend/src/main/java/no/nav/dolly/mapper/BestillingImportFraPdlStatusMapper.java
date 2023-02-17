package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.SystemTyper.PDLIMPORT;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingImportFraPdlStatusMapper {

    public static List<RsStatusRapport> buildImportFraPdlStatusMap(List<BestillingProgress> progresser) {

        //  status   ident
        Map<String, List<String>> statusIdents = new HashMap<>();

        progresser.forEach(progress -> {
            if (isNotBlank(progress.getPdlImportStatus()) && isNotBlank(progress.getIdent())) {
                if (statusIdents.containsKey(progress.getPdlImportStatus())) {
                    statusIdents.get(progress.getPdlImportStatus()).add(progress.getIdent());
                } else {
                    statusIdents.put(progress.getPdlImportStatus(), new ArrayList<>(List.of(progress.getIdent())));
                }
            }
        });

        return statusIdents.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(PDLIMPORT).navn(PDLIMPORT.getBeskrivelse())
                        .statuser(statusIdents.entrySet().stream()
                                .map(entry -> RsStatusRapport.Status.builder()
                                        .melding(decodeMsg(entry.getKey()))
                                        .identer(entry.getValue())
                                        .build())
                                .toList())
                        .build());
    }
}