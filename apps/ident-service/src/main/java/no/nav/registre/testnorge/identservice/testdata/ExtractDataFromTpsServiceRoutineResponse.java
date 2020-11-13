package no.nav.registre.testnorge.identservice.testdata;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExtractDataFromTpsServiceRoutineResponse {

    public static Set<String> trekkUtIdenterMedStatusFunnetFraResponse(JsonNode jsonResponse) {

        log.info(jsonResponse.asText());
        int antallIdenter = jsonResponse.get("antallFM201").asInt();

        Set<String> identer = new HashSet<>();

        String status = jsonResponse.get("returStatus").asText();
        log.info(status);
        if (!"12".equals(status)) {
            for (int i = 1; i < antallIdenter + 1; i++) {
                Map data = (Map) jsonResponse.get("data");

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
