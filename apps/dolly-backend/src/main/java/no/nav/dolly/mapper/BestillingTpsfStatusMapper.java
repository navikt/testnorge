package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.TPSF;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingTpsfStatusMapper {

    private static final String SUCCESS = "OK";

    public static List<RsStatusRapport> buildTpsfStatusMap(List<BestillingProgress> progressList) {

        Map<String, Map<String, Set<String>>> errorEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getTpsfSuccessEnv())) {
                List.of(progress.getTpsfSuccessEnv().split(",")).forEach(environ ->
                        checkNUpdateStatus(errorEnvIdents, progress.getIdent(), environ, SUCCESS)
                );
            }
            if (nonNull(progress.getFeil())) {
                List.of(progress.getFeil().split(",")).forEach(error -> {
                    String[] environErrMsg = error.split(":", 2);
                    String environ = environErrMsg[0];
                    String errMsg = environErrMsg.length > 1 ? environErrMsg[1].trim().replaceAll("\\d{11}\\s", "") : "";
                    checkNUpdateStatus(errorEnvIdents, progress.getIdent(), environ, getWhiteList(errMsg));
                });
            }
        });

        return errorEnvIdents.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(TPSF).navn(TPSF.getBeskrivelse())
                        .statuser(errorEnvIdents.entrySet().stream().map(status ->
                                        RsStatusRapport.Status.builder()
                                                .melding(status.getKey().replace('=', ':').replace(';', ':'))
                                                .detaljert(status.getValue().entrySet().stream()
                                                        .map(detaljert -> RsStatusRapport.Detaljert.builder()
                                                                .miljo(detaljert.getKey())
                                                                .identer(new ArrayList<>(detaljert.getValue()))
                                                                .build())
                                                        .collect(Collectors.toList()))
                                                .build())
                                .collect(Collectors.toList()))
                        .build());
    }

    private static String getWhiteList(String errMsg) {

        if (errMsg.toLowerCase().contains("lik adresse med samme tomdato finnes fra før")) {
            return SUCCESS;
        } else {
            return errMsg;
        }
    }

    private static void checkNUpdateStatus(Map<String, Map<String, Set<String>>> errorEnvIdents, String ident, String environ, String status) {

        if (!errorEnvIdents.containsKey(SUCCESS) || !errorEnvIdents.get(SUCCESS).containsKey(environ) || !errorEnvIdents.get(SUCCESS).get(environ).contains(ident)) {
            if (errorEnvIdents.containsKey(status)) {
                if (errorEnvIdents.get(status).containsKey(environ)) {
                    errorEnvIdents.get(status).get(environ).add(ident);
                } else {
                    errorEnvIdents.get(status).put(environ, new HashSet<>(Set.of(ident)));
                }
            } else if (nonNull(ident)) {
                Map<String, Set<String>> entry = new HashMap();
                entry.put(environ, new HashSet<>(Set.of(ident)));
                errorEnvIdents.put(status, entry);
            }
        }
    }
}