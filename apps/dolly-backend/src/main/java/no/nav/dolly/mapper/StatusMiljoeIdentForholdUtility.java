package no.nav.dolly.mapper;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

@UtilityClass
public class StatusMiljoeIdentForholdUtility {

    private static final String OK_RESULT = "OK";

    public static String resolveStatus(String errMsg) {

        if (errMsg.contains("$")) {
            var forholdStatus = errMsg.split("\\$");
            var forhold = forholdStatus[0];
            var status = decodeMsg(forholdStatus.length > 1 ? forholdStatus[1] : "");
            return OK_RESULT.equals(status) ? status : format("%s: %s", forhold, status);
        }
        return decodeMsg(errMsg);
    }

    public static void checkAndUpdateStatus(Map<String, Map<String, Set<String>>> statusEnvIdents, String ident, String environ, String errMsg) {

        var status = resolveStatus(errMsg);
        statusEnvIdents
                .computeIfAbsent(status, k -> new HashMap<>())
                .computeIfAbsent(environ, k -> new HashSet<>())
                .add(ident);
    }

    public static String decodeMsg(String encodedMsg) {

        return encodedMsg
                .trim()
                .replace(';', ',')
                .replace('=', ':')
                .replace('&', ',');
    }
}
