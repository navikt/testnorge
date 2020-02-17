package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pensjon.PensjonClient.PENSJON_FORVALTER;
import static no.nav.dolly.bestilling.pensjon.PensjonClient.POPP_INNTEKTSREGISTER;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_FORVALTER;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_INNTEKT;
import static no.nav.dolly.mapper.BestillingMeldingStatusIdentMapper.resolveStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import no.nav.dolly.domain.resultset.SystemTyper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingPensjonStatusMapper {

    public static List<RsStatusRapport> buildPensjonStatusMap(List<BestillingProgress> progressList) {

        //  melding     status      ident
        Map<String, Map<String, List<String>>> msgStatusIdents = new HashMap();

        progressList.forEach(progress -> {
            if (nonNull(progress.getPensjonforvalterStatus())) {
                newArrayList(progress.getPensjonforvalterStatus().split("\\$")).forEach(
                        resolveStatus(msgStatusIdents, progress)
                );
            }
        });

        List<RsStatusRapport> statusRapporter = new ArrayList();
        statusRapporter.addAll(extractStatus(msgStatusIdents, POPP_INNTEKTSREGISTER, PEN_INNTEKT));
        statusRapporter.addAll(extractStatus(msgStatusIdents, PENSJON_FORVALTER, PEN_FORVALTER));

        return statusRapporter;
    }

    private static List<RsStatusRapport> extractStatus(Map<String, Map<String, List<String>>> msgStatusIdents, String clientid, SystemTyper type) {
        return msgStatusIdents.entrySet().stream().filter(entry -> clientid.equals(entry.getKey()))
                .map(entry -> RsStatusRapport.builder().id(type).navn(type.getBeskrivelse())
                        .statuser(entry.getValue().entrySet().stream()
                                .map(entry1 -> RsStatusRapport.Status.builder()
                                        .melding(entry1.getKey().replaceAll(";",","))
                                        .detaljert(singletonList(RsStatusRapport.Detaljert.builder()
                                                .identer(entry1.getValue())
                                                .build()))
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }
}
