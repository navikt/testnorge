package no.nav.registre.testnorge.identservice.testdata;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.response.TpsServiceRoutineResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
public final class ExtractDataFromTpsServiceRoutineResponse {

    private ExtractDataFromTpsServiceRoutineResponse() {
    }

    public static Set<String> trekkUtIdenterMedStatusFunnetFraResponse(TpsServiceRoutineResponse tpsResponse) {

        Map responseMap = (Map) tpsResponse.getResponse();
        log.info(responseMap.toString());
        int antallIdenter = (int) responseMap.get("antallTotalt");

        Set<String> identer = new HashSet<>();

        for (int i = 1; i < antallIdenter + 1; i++) {
            mapResponseToData(responseMap, identer, i);
        }
        return identer;
    }

    private static void mapResponseToData(Map responseMap, Set<String> identer, int identPosition) {
        Map data = (Map) getArtifact(responseMap, "data" + identPosition);

        Map aFnr = (Map) getArtifact(data, "AFnr");
        Map svarStatus;
        if (nonNull(aFnr)) {
            addFoundIdentToMap(identer, aFnr);
        } else {
            svarStatus = (Map) getArtifact(data, "svarStatus");
            String returStatus = (String) getArtifact(svarStatus, "returStatus");
            if (!"08".equals(returStatus) && nonNull(data) && nonNull(data.get("fnr"))) {
                identer.add(String.valueOf(data.get("fnr")));
            }
        }
    }

    private static void addFoundIdentToMap(Set<String> identer, Map aFnr) {
        Map svarStatus;
        if (getArtifact(aFnr, "EFnr") instanceof List) {
            List<Map> eFnr = (List) getArtifact(aFnr, "EFnr");
            eFnr.forEach(efnr -> {
                if (isNull(getArtifact(efnr, "svarStatus"))) {
                    identer.add(String.valueOf(efnr.get("fnr")));
                }
            });
        } else {
            Map eFnr = (Map) getArtifact(aFnr, "EFnr");
            svarStatus = (Map) getArtifact(eFnr, "svarStatus");
            String returStatus = (String) getArtifact(svarStatus, "returStatus");
            if (!"08".equals(returStatus) && nonNull(eFnr)) {
                identer.add(String.valueOf(eFnr.get("fnr")));
            }
        }
    }

    private static Object getArtifact(Map map, String key) {
        return nonNull(map) ? map.get(key) : null;
    }
}
