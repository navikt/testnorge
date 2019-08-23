package no.nav.dolly.mapper;

import static java.util.Collections.singleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.list.TreeList;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AbstractRsStatusMiljoeIdentForhold {

    public static void checkAndUpdateStatus(Map<String, Map<String, Map<String, List<String>>>> errorEnvIdents, String ident, String environ, String errMsg) {

        String forhold = "opphold: alle";
        String status;

        if (errMsg.contains("$")) {
            forhold = errMsg.split("\\$")[0].replace("=", ": ");
            status = errMsg.split("\\$")[1].replace('&', ',').replace('=', ':');
        } else {
            status = errMsg.replace('&', ',').replace('=', ':');
        }

        if (errorEnvIdents.containsKey(status)) {
            if (errorEnvIdents.get(status).containsKey(environ)) {
                if (errorEnvIdents.get(status).get(environ).containsKey(ident)) {
                    errorEnvIdents.get(status).get(environ).get(ident).add(forhold);
                } else {
                    errorEnvIdents.get(status).get(environ).put(ident, new TreeList(singleton(forhold)));
                }
            } else {
                Map<String, List<String>> identEntry = new HashMap();
                identEntry.put(ident, new TreeList(singleton(forhold)));
                errorEnvIdents.get(status).put(environ, identEntry);
            }
        } else {
            Map<String, Map<String, List<String>>> environEntry = new HashMap();
            Map<String, List<String>> identEntry = new HashMap();
            environEntry.put(environ, identEntry);
            identEntry.put(ident, new TreeList(singleton(forhold)));
            errorEnvIdents.put(status, environEntry);
        }
    }
}
