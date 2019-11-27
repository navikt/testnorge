package no.nav.dolly.consumer.pdlperson;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlApiResponse {
    private List<PdlApiError> errors;
    private PdlApiData data;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PdlApiData {
        private PdlPerson hentPerson;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PdlApiError {
        private String message;
        private List<Location> locations;
        private List<String> path;
        private Extension extensions;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private Integer line;
        private Integer column;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Extension {
        private String code;
        private String classification;
    }
}
