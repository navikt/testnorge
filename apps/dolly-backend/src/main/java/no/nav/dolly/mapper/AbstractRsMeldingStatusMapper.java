package no.nav.dolly.mapper;

import static java.util.Collections.singleton;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AbstractRsMeldingStatusMapper {

    public static void buildStatusMap(Map<String, Map<String, List<String>>> statusEnvIdents, String status, String environment, String ident) {

        if (statusEnvIdents.containsKey(status)) {
            if (statusEnvIdents.get(status).containsKey(environment)) {
                statusEnvIdents.get(status).get(environment).add(ident);
            } else {
                statusEnvIdents.get(status).put(environment, new ArrayList<>(singleton(ident)));
            }
        } else {
            Map envIdent = new HashMap();
            envIdent.put(environment, new ArrayList<>(singleton(ident)));
            statusEnvIdents.put(status, envIdent);
        }
    }
}
