package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.BAFeilkoder;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.SystemTyper.AAREG;
import static no.nav.dolly.mapper.AbstractRsStatusMiljoeIdentForhold.checkAndUpdateStatus;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingAaregStatusMapper {

    public static List<RsStatusRapport> buildAaregStatusMap(List<BestillingProgress> progressList) {
        //  status     miljø       ident
        Map<String, Map<String, Set<String>>> errorEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (isNotBlank(progress.getAaregStatus())) {
                List.of(progress.getAaregStatus().split(",")).forEach(status -> {
                    String[] environErrMsg = status.split(":");
                    String environ = environErrMsg[0];
                    String errMsg = environErrMsg.length > 1 ? environErrMsg[1].trim() : "";
                    String errMsgWithBAFeilmelding = errMsg.contains("BA")
                            ? konverterBAfeilkodeTilFeilmelding(errMsg)
                            : errMsg;
                    checkAndUpdateStatus(errorEnvIdents, progress.getIdent(), environ, errMsgWithBAFeilmelding);
                });
            }
        });

        return errorEnvIdents.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(AAREG).navn(AAREG.getBeskrivelse())
                        .statuser(errorEnvIdents.entrySet().stream().map(status ->
                                        RsStatusRapport.Status.builder()
                                                .melding(status.getKey().replace(";", ","))
                                                .detaljert(status.getValue().entrySet().stream().map(miljo ->
                                                                RsStatusRapport.Detaljert.builder()
                                                                        .miljo(miljo.getKey())
                                                                        .identer(new ArrayList<>(miljo.getValue()))
                                                                        .build())
                                                        .toList())
                                                .build())
                                .toList())
                        .build());
    }


    public static String konverterBAfeilkodeTilFeilmelding(String baKode) {
        var baFeilkode = getBaFeilkodeFromFeilmelding(baKode);
        try {
            return baKode.replace(baFeilkode, BAFeilkoder.valueOf(baFeilkode).getBeskrivelse());
        } catch (IllegalArgumentException e) {
            log.warn("Mottok ukjent BA feilkode i feilmeldingen: {}", baKode);
            return baKode;
        }
    }

    private static String getBaFeilkodeFromFeilmelding(String baKode) {
        var setninger = baKode.split(" ");
        return Arrays.stream(setninger)
                .filter(ord -> ord.contains("BA"))
                .findFirst()
                .orElse("");
    }
}