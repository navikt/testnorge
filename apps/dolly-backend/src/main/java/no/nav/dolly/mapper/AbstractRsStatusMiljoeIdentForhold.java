package no.nav.dolly.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AbstractRsStatusMiljoeIdentForhold {

    private static final String OK_RESULT = "OK";

    public static void checkAndUpdateStatus(Map<String, Map<String, Set<String>>> statusEnvIdents, String ident, String environ, String errMsg) {

        String status;

        if (errMsg.contains("$")) {
            String[] forholdStatus = errMsg.split("\\$");
            String forhold = forholdStatus[0];
            status = (forholdStatus.length > 1 ? forholdStatus[1] : "").replace('&', ',').replace('=', ':');
            if (!OK_RESULT.equals(status)) {
                status = format("%s: %s", forhold, status);
            }
        } else {
            status = errMsg.replace('&', ',').replace('=', ':');
        }

        if (statusEnvIdents.containsKey(status)) {
            if (statusEnvIdents.get(status).containsKey(environ)) {
                statusEnvIdents.get(status).get(environ).add(ident);
            } else {
                statusEnvIdents.get(status).put(environ, new HashSet<>(Set.of(ident)));
            }
        } else {
            Map envIdent = new HashMap();
            envIdent.put(environ, new HashSet<>(Set.of(ident)));
            statusEnvIdents.put(status, envIdent);
        }
    }
}
