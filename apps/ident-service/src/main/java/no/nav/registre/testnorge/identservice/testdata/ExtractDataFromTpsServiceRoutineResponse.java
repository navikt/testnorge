package no.nav.registre.testnorge.identservice.testdata;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.identservice.testdata.response.ResponseStatus;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.response.TpsServiceRoutineResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExtractDataFromTpsServiceRoutineResponse {

    public static Set<String> trekkUtIdenterMedStatusFunnetFraResponse(TpsServiceRoutineResponse tpsResponse) {

        Map responseMap = (Map) tpsResponse.getResponse();
        log.info(responseMap.toString());
        int antallIdenter = (int) responseMap.get("antallFM201");

        Set<String> identer = new HashSet<>();

        ResponseStatus status = (ResponseStatus) getArtifact(responseMap, "svarStatus");
        log.info(status.toString());
        if (!"12".equals(status.getReturStatus())) {
            for (int i = 1; i < antallIdenter + 1; i++) {
                Map data = (Map) getArtifact(responseMap, "data" + i);

                Map aFnr = (Map) getArtifact(data, "AFnr");
                Map svarStatus;
                if (nonNull(aFnr)) {
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
                } else {
                    svarStatus = (Map) getArtifact(data, "svarStatus");
                    String returStatus = (String) getArtifact(svarStatus, "returStatus");
                    if (!"08".equals(returStatus) && nonNull(data) && nonNull(data.get("fnr"))) {
                        identer.add(String.valueOf(data.get("fnr")));
                    }
                }
            }
        }
        return identer;
    }

    private static Object getArtifact(Map map, String key) {
        return nonNull(map) ? map.get(key) : null;
    }
}
