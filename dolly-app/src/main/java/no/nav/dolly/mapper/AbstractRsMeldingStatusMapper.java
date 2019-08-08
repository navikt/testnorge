package no.nav.dolly.mapper;

import static com.google.common.collect.Lists.newArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AbstractRsMeldingStatusMapper {

    public static void buildStatusMap(Map<String, Map<String, List<String>>> statusEnvIdents, String status, String environment, String ident) {

        if (statusEnvIdents.containsKey(status)) {
            if (statusEnvIdents.get(status).containsKey(environment)) {
                statusEnvIdents.get(status).get(environment).add(ident);
            } else {
                statusEnvIdents.get(status).put(environment, newArrayList(ident));
            }
        } else {
            Map envIdent = new HashMap();
            envIdent.put(environment, newArrayList(ident));
            statusEnvIdents.put(status, envIdent);
        }
    }
}
