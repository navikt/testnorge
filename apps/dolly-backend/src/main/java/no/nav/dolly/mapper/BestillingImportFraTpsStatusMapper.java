package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.SystemTyper.TPSIMPORT;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.checkAndUpdateStatus;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingImportFraTpsStatusMapper {

    public static List<RsStatusRapport> buildImportFraTpsStatusMap(Bestilling bestilling) {

        //  status     miljø       ident
        Map<String, Map<String, Set<String>>> statusEnvIdents = new HashMap<>();

        if (isNotBlank(bestilling.getTpsImport())) {
            bestilling.getProgresser().forEach(progress -> {
                if (isNotBlank(progress.getTpsImportStatus())) {
                    String status =
                            progress.getTpsImportStatus().replaceAll("\\d{11}", "").replace("  ", " ");
                    String environ = bestilling.getKildeMiljoe();
                    checkAndUpdateStatus(statusEnvIdents, progress.getIdent(), environ, status);
                }
            });
        }

        return statusEnvIdents.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(TPSIMPORT).navn(TPSIMPORT.getBeskrivelse())
                        .statuser(statusEnvIdents.entrySet().stream().map(status -> RsStatusRapport.Status.builder()
                                        .melding(status.getKey())
                                        .detaljert(status.getValue().entrySet().stream().map(envIdent -> RsStatusRapport.Detaljert.builder()
                                                        .miljo(envIdent.getKey())
                                                        .identer(new ArrayList<>(envIdent.getValue()))
                                                        .build())
                                                .collect(Collectors.toList()))
                                        .build())
                                .collect(Collectors.toList()))
                        .build());
    }
}