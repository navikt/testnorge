package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static no.nav.dolly.domain.resultset.SystemTyper.INNTK;
import static no.nav.dolly.mapper.StatusMiljoeIdentForholdUtility.decodeMsg;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingInntektstubStatusMapper extends TypeStatusIdenterUtility {

    public static List<RsStatusRapport> buildInntektstubStatusMap(List<BestillingProgress> progressList) {

        // type    // status   // ident
        Map<String, Map<String, Set<String>>> typeStatusIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getInntektstubStatus())) {
                List.of(progress.getInntektstubStatus().split(",")).forEach(
                        entry -> {
                            if (isNotBlank(entry)) {
                                var typeStatus = entry.split(":");
                                var type = typeStatus.length > 1 ? typeStatus[0] : "Oppretting";
                                var status = decodeMsg(typeStatus.length > 1 ? typeStatus[1] : typeStatus[0]);
                                insertArtifact(typeStatusIdents, type, status, progress.getIdent());
                            }
                        });
            }
        });

        if (typeStatusIdents.isEmpty()) {
            return emptyList();

        } else {

            return List.of(RsStatusRapport.builder()
                    .id(INNTK)
                    .navn(INNTK.getBeskrivelse())
                    .statuser(Stream.of(
                                    extractOKStatus(typeStatusIdents),
                                    extractErrorStatus(typeStatusIdents))
                            .flatMap(Collection::stream)
                            .toList())
                    .build());
        }
    }
}
