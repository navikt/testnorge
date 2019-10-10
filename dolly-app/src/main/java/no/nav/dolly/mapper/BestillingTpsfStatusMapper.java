package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.TPSF;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BestillingTpsfStatusMapper {

    private static final String SUCCESS = "OK";

    public static List<RsStatusRapport> buildTpsfStatusMap(List<BestillingProgress> progressList) {

        Map<String, Map<String, Set<String>>> errorEnvIdents = new HashMap<>();

        progressList.forEach(progress -> {
            if (nonNull(progress.getTpsfSuccessEnv())) {
                asList(progress.getTpsfSuccessEnv().split(",")).forEach(environ ->
                        checkNUpdateStatus(errorEnvIdents, progress.getIdent(), environ, SUCCESS)
                );
            }
            if (nonNull(progress.getFeil())) {
                asList(progress.getFeil().split(",")).forEach(error -> {
                    String[] environErrMsg = error.split(":", 2);
                    String environ = environErrMsg[0];
                    String errMsg = environErrMsg.length > 1 ? environErrMsg[1].trim() : "";
                    checkNUpdateStatus(errorEnvIdents, progress.getIdent(), environ, errMsg);
                });
            }
            if (nonNull(progress.getTpsfSuccessEnv())) {
                asList(progress.getTpsfSuccessEnv().split(",")).forEach(environ ->
                        checkNUpdateStatus(errorEnvIdents, progress.getIdent(), environ, SUCCESS)
                );
            }
        });

        return errorEnvIdents.isEmpty() ? emptyList() :
                singletonList(RsStatusRapport.builder().id(TPSF).navn(TPSF.getBeskrivelse())
                        .statuser(errorEnvIdents.entrySet().stream().map(status ->
                                RsStatusRapport.Status.builder()
                                        .melding(status.getKey())
                                        .detaljert(status.getValue().entrySet().stream()
                                                .map(detaljert -> RsStatusRapport.Detaljert.builder()
                                                        .miljo(detaljert.getKey())
                                                        .identer(newArrayList(detaljert.getValue()))
                                                        .build())
                                                .collect(Collectors.toList()))
                                        .build())
                                .collect(Collectors.toList()))
                        .build());
    }

    private static void checkNUpdateStatus(Map<String, Map<String, Set<String>>> errorEnvIdents, String ident, String environ, String status) {

        if (errorEnvIdents.containsKey(status)) {
            if (errorEnvIdents.get(status).containsKey(environ)) {
                errorEnvIdents.get(status).get(environ).add(ident);
            } else {
                errorEnvIdents.get(status).put(environ, new HashSet<>(Collections.singleton(ident)));
            }
        } else {
            Map<String, Set<String>> entry = new HashMap<>();
            entry.put(environ, new HashSet<>(Collections.singleton(ident)));
            errorEnvIdents.put(status, entry);
        }
    }
}
