package no.nav.dolly.mapper;

import static com.google.common.collect.Sets.newHashSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BestillingSystemStatusMapper {

    protected BestillingSystemStatusMapper() {

    }

    protected static void checkNUpdateStatus(Map<String, Map<String, Set<String>>> errorEnvIdents, String ident, String environ, String status) {

        if (errorEnvIdents.containsKey(status)) {
            if (errorEnvIdents.get(status).containsKey(environ)) {
                errorEnvIdents.get(status).get(environ).add(ident);
            } else {
                errorEnvIdents.get(status).put(environ, newHashSet(ident));
            }
        } else {
            Map<String, Set<String>> entry = new HashMap();
            entry.put(environ, newHashSet(ident));
            errorEnvIdents.put(status, entry);
        }
    }

}
